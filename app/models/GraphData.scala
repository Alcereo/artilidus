package models

import java.util.UUID

import play.api.libs.json.Json

case class GraphDataRow(
                    article_uid: UUID,
                    note_uid: UUID,
                    x: Double,
                    y: Double
                    )

object GraphData {

  case class graphData(
                        id: Option[String],
                        model_id: Option[String],
                        name: Option[String],
                        score: Option[Double],
                        source: Option[String],
                        target: Option[String],
                        content: Option[String]
                      )

  case class graphPosition(
                            x: Double,
                            y: Double
                          )

  case class graphElement(
                           data: graphData,
                           position: Option[graphPosition]
                         )

  implicit val graphDataWriter = Json.writes[graphData]
  implicit val graphPositionWriter = Json.writes[graphPosition]
  implicit val graphElementWriter = Json.writes[graphElement]

  implicit val graphDataReader = Json.reads[graphData]
  implicit val graphPositionReader = Json.reads[graphPosition]
  implicit val graphElementReader = Json.reads[graphElement]

  def formatGraphData(articlesData: Seq[(Article,GraphDataRow)]) = {

    Json.toJson(
      articlesData.map {
        case (art: Article, data: GraphDataRow) =>
          graphElement(
            graphData(
              id = Some(art.uid.getOrElse("0").toString),
              model_id = art.id.map(_.toString),
              name = Some(art.title),
              score = Some(0),
              source = None,
              target = None,
              content = Some(art.contentHtml.toString)
            ),
            Some(graphPosition(
              data.x,
              data.y
            ))
          )
      } ++ articlesData.filter(_._1.parentUid.isDefined).map {
        case (art: Article, row: GraphDataRow) =>
          graphElement(
            graphData(
              None,
              None,
              None,
              None,
              art.parentUid.map(id => id.toString),
              Some(art.uid.getOrElse("0").toString),
              None
            ),
            None
          )
      }
    ).toString()
  }

}
