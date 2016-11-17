package DAO

import java.util
import java.util.UUID
import javax.inject.Inject

import models.{Article, GraphData, GraphDataRow}

import scala.concurrent.Future  // Это для тех футур которые будут выходить из db.run

// следующее для загрузки конфигов и драйвера
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

// Это контекст
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class GraphDataDAO @Inject()(articleDAO: ArticleDAO, noteDAO: NoteDAO, protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]{

  import driver.api._

  val GraphDataTableQuery = TableQuery[GraphDataTable]

  def getGraphDataByNoteUid(uid: UUID):Future[Seq[(Article, GraphDataRow)]] = db run {
    (for {
      article <- articleDAO.Articles
      data <- GraphDataTableQuery
        .filter(_.article_uid === article.uid)
        .filter(_.note_uid === uid)
    } yield (article, data)).result
  }

  def saveFullGraphDataByNoteId(noteId: Int, seqGraphElement: Seq[GraphData.graphElement]) = db run {
    noteDAO.Notes.filter(_.id === noteId).map(_.uid).result.head.flatMap(
      noteUid =>
        GraphDataTableQuery.filter(_.note_uid === noteUid).delete.flatMap(
          _ =>
            GraphDataTableQuery ++=
              seqGraphElement.map(
                graphElement =>
                  GraphDataRow(
                    article_uid = UUID.fromString(graphElement.data.id.getOrElse("0")),
                    note_uid = noteUid,
                    graphElement.position.getOrElse(GraphData.graphPosition(0, 0)).x,
                    graphElement.position.getOrElse(GraphData.graphPosition(0, 0)).y
                  )
              )
        )
    )
  }

  def deleteGraphDataByNoteId(NoteId: Int) = db run {
    noteDAO.Notes.filter(_.id === NoteId).map(_.uid).result.head.flatMap(
      noteUid =>
        GraphDataTableQuery.filter(_.note_uid === noteUid).delete
    )
  }

  def saveGraphDataElementByNoteId(NoteId: Int, graphElement: GraphData.graphElement) = db run {
    noteDAO.Notes.filter(_.id === NoteId).map(_.uid).result.head.flatMap(
      noteUid =>
        GraphDataTableQuery insertOrUpdate
          GraphDataRow(
            article_uid = UUID.fromString(graphElement.data.id.getOrElse("0")),
            note_uid = noteUid,
            graphElement.position.getOrElse(GraphData.graphPosition(0, 0)).x,
            graphElement.position.getOrElse(GraphData.graphPosition(0, 0)).y
          )
    )
  }

  def saveArticle(article: Article) = db run {
    GraphDataTableQuery insertOrUpdate GraphDataRow(
      article.uid.getOrElse(new UUID(0,0)), article.noteUid, 0,0
    )
  }

  def deleteGraphData(article: Article) = db run
    GraphDataTableQuery
      .filter(_.note_uid === article.noteUid)
      .filter(_.article_uid === article.uid)
      .delete

  class GraphDataTable(tag:Tag) extends Table[GraphDataRow](tag, "graph_data"){

    def article_uid = column[UUID]("article_uid", O.PrimaryKey)
    def note_uid = column[UUID]("note_uid", O.PrimaryKey)
    def x = column[Double]("x")
    def y = column[Double]("y")

    def * = (article_uid, note_uid, x, y) <> (GraphDataRow.tupled, GraphDataRow.unapply)

  }

}
