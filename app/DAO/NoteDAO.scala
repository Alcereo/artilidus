package DAO

import java.util.UUID
import javax.inject.Inject

import models.Note

import scala.concurrent.Future  // Это для тех футур которые будут выходить из db.run

// следующее для загрузки конфигов и драйвера
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

// Это контекст
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class NoteDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]{

  import driver.api._

  val Notes = TableQuery[NotesTable]

  def createTable = db run Notes.schema.create

  def getAll:Future[Seq[Note]] = db run Notes.result

  def getByUid(uid:UUID):Future[Option[Note]] = db run Notes.filter(_.uid === uid).result.headOption

  def getById(id: Int):Future[Option[Note]] = db run Notes.filter(_.id === id).result.headOption

  def insertNote(note: Note):Future[Note] = db run {
    (Notes returning Notes) += note
  }

  def saveNote(note: Note):Future[Option[Note]] = db run {
    (Notes returning Notes) insertOrUpdate note
  }

  def some(): (Note) => Int = (note: Note) => {
    3
  }

  class NotesTable(tag:Tag) extends Table[Note](tag, "notes"){

    def uid = column[UUID]("uid", O.PrimaryKey, O.AutoInc)
    def mainArticleUid = column[Option[UUID]]("mainarticleuid")
    def id = column[Option[Int]]("id", O.AutoInc)
    def owneruid = column[Option[UUID]]("owneruid")

    def * = (uid.?, mainArticleUid, id, owneruid) <> (Note.tupled, Note.unapply)

  }
}
