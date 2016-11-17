package controllers

import java.util.UUID
import javax.inject.Inject

import DAO.{ArticleDAO, GraphDataDAO, NoteDAO}
import models.GraphData.graphElement
import play.api._
import play.api.mvc._
import play.twirl.api.Html
import models.{Article, GraphData, Note}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


class Application @Inject()(
                             graphDataDAO: GraphDataDAO,
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
    noteDAO.getById(id).flatMap(
      noteOpt =>
        noteOpt.map(
          note =>
            graphDataDAO.getGraphDataByNoteUid(note.uid.get).map(
              graphDataSeq =>
                Ok(views.html.mainGraph(GraphData.formatGraphData(graphDataSeq), id))
            )
        ).getOrElse(Future(NotFound))
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
                articleDAO.saveArticle(article).flatMap(
                  {
                      case None => Future(Ok(""))
                      case Some(article) => graphDataDAO.saveArticle(article).map(
                        _=>
                          Ok("Id:" + article.id.getOrElse(""))
                      ).recover{
                        case exception =>
                          BadRequest(exception.getLocalizedMessage)
                      }
                  }
                ).recover{
                  case exception => BadRequest(exception.getLocalizedMessage)
                }
            }
          )
        case _ => Future(NotFound("Нет JSON тела!"))
      }
  }

  def deleteArticle(id: Int, noteId: Int) = Action.async {
    articleDAO.getById(id).flatMap(
      {
        case None => Future(0)
        case Some(article) => graphDataDAO.deleteGraphData(article)
      }
    ).flatMap(
      _ => articleDAO.deleteArticleById(id)
    ).map(
      _ => Redirect(routes.Application.mainGraph(noteId))
    ).recover { case exception => BadRequest(exception.getLocalizedMessage) }
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
                          (newArticle: Article) => {

                            val noteSaving = noteDAO.saveNote(
                              Note(
                                newNote.uid,
                                newArticle.uid,
                                newNote.id
                              ))

                            val GraphDataSaving = graphDataDAO.saveArticle(newArticle)

                            (for {
                              _ <- noteSaving
                              _ <- GraphDataSaving
                            } yield Ok(""))
                              .recover{case exception => BadRequest(exception.getLocalizedMessage)}

                          }

                        ).getOrElse(Future(BadRequest(s"Статья с uid:${article.uid} уже существует!")))
                    ).recover{
                      case exception => BadRequest(exception.getLocalizedMessage)
                    }
                  }
                ).recover{
                  case exception => BadRequest(exception.getLocalizedMessage)
                }
            }
          )
        case _ => Future(NotFound("Нет JSON тела!"))
      }
  )

  def saveFullGraph(noteId:Int) = Action.async {
    request =>
      import GraphData._

      request.body.asJson match {
        case jsObj: Some[JsValue] =>
          jsObj.get.validate[Seq[graphElement]].fold(
            invalid = {
              fieldErrors =>
                Future(BadRequest(
                  (for (err <- fieldErrors)
                    yield "field: " + err._1 + ", errors: " + err._2).toString
                ))
            },
            valid = {
              seqGraphElement =>
                graphDataDAO.saveFullGraphDataByNoteId(noteId, seqGraphElement).map(
                  _=> Ok("")
                ).recover{
                  case exception => BadRequest(exception.getLocalizedMessage)
                }
            }
          )
        case _ => Future(NotFound("Нет JSON тела!"))
      }
  }

  def removeGraphData(noteId:Int) = Action.async {
    request =>
      graphDataDAO.deleteGraphDataByNoteId(noteId).map(
        _=>Ok("")
      ).recover{
        case exception => BadRequest(exception.getLocalizedMessage)
      }
  }

  def saveGraphElement(noteId:Int) = Action.async {
    request =>
      import GraphData._

      request.body.asJson match {
        case jsObj: Some[JsValue] =>
          jsObj.get.validate[graphElement].fold(
            invalid = {
              fieldErrors =>
                Future(BadRequest(
                  (for (err <- fieldErrors)
                    yield "field: " + err._1 + ", errors: " + err._2).toString
                ))
            },
            valid = {
              graphElement =>
                graphDataDAO.saveGraphDataElementByNoteId(noteId, graphElement).map(
                  _=> Ok("")
                ).recover{
                  case exception => BadRequest(exception.getLocalizedMessage)
                }
            }
          )
        case _ => Future(NotFound("Нет JSON тела!"))
      }
  }

  //  def article(id: Int) = Action{
  //    Article.findByID(id).map(
  //      art => Ok(views.html.post(art))
  //    ).getOrElse(BadRequest)
  //  }

}