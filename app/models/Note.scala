package models

import java.util.UUID

case class Note (
                uid: Option[UUID],
                mainArticleUid: Option[UUID],
                id: Option[Int]
                )
