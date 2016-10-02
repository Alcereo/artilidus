package models

import play.api.libs.json.Json


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

  def formatGraphData(articles: Seq[Article]) = {

    Json.toJson(
      articles.map(
        art =>
          graphElement(
            graphData(
              id        = Some(art.uid.getOrElse("0").toString),
              model_id  = art.id.map(_.toString),
              name      = Some(art.title),
              score     = Some(0),
              source    = None,
              target    = None,
              content   = Some(art.contentHtml.toString)
            ),
            Some(graphPosition(
              0,
              10 * art.id.getOrElse(0)
            ))
          )
      ) ++ articles.filter(_.parentUid.isDefined).map(
        art =>
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
      )
    ).toString()

  }

}
