package DAO

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future  // Это для тех футур которые будут выходить из db.run

// следующее для загрузки конфигов и драйвера
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

// Это контекст
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models.Article

class ArticleDAO @Inject()(noteDAO: NoteDAO,protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]{

  import driver.api._

  val Articles = TableQuery[ArticlesTable]

  def getByUid(uid:UUID):Future[Option[Article]] = db run Articles.filter(_.uid === uid).result.headOption

  def getById(id: Int):Future[Option[Article]] = db run Articles.filter(_.id === id).result.headOption

  def getAllByNoteUid(uid:UUID):Future[Seq[Article]] = db run Articles.filter(_.noteUid === uid).result

  def getAllByNoteId(id:Int):Future[Seq[Article]] = db run
    (for {
      note <- noteDAO.Notes.filter(_.id === id)
      article <- Articles if note.uid === article.noteUid
    } yield article
      ).result

  def getAllNotesMainArticles:Future[Seq[(Article, Int)]] = db run
    (for {
      notes <- noteDAO.Notes
      articles <- Articles if notes.mainArticleUid === articles.uid
    } yield (articles, notes.id.get)
      ).result

  def saveArticle(article: Article):Future[Option[Article]] = db run {
    (Articles returning Articles) insertOrUpdate article
  }

  def deleteArticleById(id: Int) = db run Articles.filter(_.id===id).delete

  class ArticlesTable(tag:Tag) extends Table[Article](tag, "articles"){

    def uid = column[UUID]("uid", O.PrimaryKey, O.AutoInc)
    def parentUid = column[Option[UUID]]("parentuid")
    def id = column[Option[Int]]("id", O.AutoInc)
    def noteUid = column[UUID]("noteuid")
    def title = column[String]("title")
    def contentHtml = column[String]("contenthtml")
    def contentMarkdown = column[String]("contentmarkdown")

    def * = (uid.?, id, parentUid, title, contentHtml, contentMarkdown, noteUid) <> (Article.tupled, Article.unapply)

  }

}

