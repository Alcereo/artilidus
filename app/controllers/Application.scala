package controllers

import java.util.UUID
import javax.inject.Inject

import DAO.{ArticleDAO, NoteDAO}
import play.api._
import play.api.mvc._
import play.twirl.api.Html
import models.{Article, GraphData, Note}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


class Application @Inject()(
                             articleDAO: ArticleDAO,
                             noteDAO: NoteDAO)
  extends Controller {

  implicit val ArticleReader = Json.reads[Article]

  def index = Action.async {
    articleDAO.getAllNotesMainArticles.map(
      articleSeq =>
        Ok(views.html.index(articleSeq.toList.sortBy(_._2)))
    )
  }

  def mainGraph(id: Int) = Action.async(
    articleDAO.getAllByNoteId(id).map(
      articleSeq =>
        Ok(views.html.mainGraph(GraphData.formatGraphData(articleSeq), id))
    )
  )

  def editArticle(id: Int) = Action.async {
    articleDAO.getById(id).flatMap(articleOpt =>
      articleOpt.map(article =>
        articleDAO.getAllByNoteUid(articleOpt.get.noteUid).flatMap(articleSeq =>
          noteDAO.getByUid(article.noteUid).map(noteOpt =>
            Ok(views.html.articleEdit(article, articleSeq, noteOpt.getOrElse(Note(None, None, None))))
          )
        )
      ).getOrElse(Future(NotFound(s"Статья с id=$id не наидена")))
    )
  }

  def newArticle(parentId: Int, noteId: Int) = Action.async {

    articleDAO.getById(parentId).flatMap(parentArticleOpt =>
      noteDAO.getById(noteId).flatMap(noteOpt =>
        articleDAO.getAllByNoteUid(
          noteOpt.map(
            _.uid.getOrElse(new UUID(0, 0))
          ).getOrElse(new UUID(0, 0))
        ).map(articlesSeq =>

          noteOpt.map(note =>
            Ok(
              views.html.articleEdit(
                Article(
                  uid = None,
                  id = None,
                  parentUid = parentArticleOpt.map(_.uid).getOrElse(None),
                  title = "",
                  contentHtml = "",
                  contentMarkdown = "",
                  noteUid = note.uid.getOrElse(new UUID(0, 0))
                ),
                articlesSeq,
                note
              )
            )
          ).getOrElse(BadRequest(s"Не наиден конспект с id=$noteId"))
        )
      )

    )
  }

  def saveArticle = Action.async {
    request =>
      request.body.asJson match {
        case jsObj: Some[JsValue] =>
          jsObj.get.validate[Article].fold(
            invalid = {
              fieldErrors =>
                Future(BadRequest(
                  (for (err <- fieldErrors)
                    yield "field: " + err._1 + ", errors: " + err._2).toString
                ))
            },
            valid = {
              article =>
                articleDAO.saveArticle(article).map(
                  articleOpt =>
                    Ok("Id:" + articleOpt.map(_.id).getOrElse(""))
                )
            }
          )
        case _ => Future(NotFound("Нет JSON тела!"))
      }
  }

  def deleteArticle(id: Int) = Action.async {
    articleDAO.deleteArticleById(id).map(
      _ =>
        Redirect(routes.Application.mainGraph(1))
    )
  }

  def newNote = Action(
    Ok(views.html.noteAdd(
      Article(
        uid = None,
        id = None,
        parentUid = None,
        title = "",
        contentHtml = "",
        contentMarkdown = "",
        new UUID(0,0)
      )
    ))
  )

  def saveNote = Action.async(
    request =>
      request.body.asJson match {
        case jsObj: Some[JsValue] =>
          jsObj.get.validate[Article].fold(
            invalid = {
              fieldErrors =>
                Future(BadRequest(
                  (for (err <- fieldErrors)
                    yield "field: " + err._1 + ", errors: " + err._2).toString
                ))
            },
            valid = {
              article =>
                noteDAO.insertNote(Note(None,None,None)).flatMap(
                  newNote => {
                    articleDAO.saveArticle {
                      Article(
                        id = None,
                        uid = None,
                        parentUid = None,
                        title = article.title,
                        contentHtml = article.contentHtml,
                        contentMarkdown = article.contentMarkdown,
                        noteUid = newNote.uid.get
                      )
                    }.flatMap(
                      articleOpt =>
                        articleOpt.map(
                          newArticle =>
                            noteDAO.saveNote(
                              Note(
                                newNote.uid,
                                newArticle.uid,
                                newNote.id
                              )).map(
                              savedNote =>
                                Ok("")
                            )
                        ).getOrElse(Future(BadRequest("Ошибка сохранения статьи.")))
                    )
                  }
                )
            }
          )
        case _ => Future(NotFound("Нет JSON тела!"))
      }
  )

  //  def article(id: Int) = Action{
  //    Article.findByID(id).map(
  //      art => Ok(views.html.post(art))
  //    ).getOrElse(BadRequest)
  //  }

}