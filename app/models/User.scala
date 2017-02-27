package models

import java.util.UUID

/**
  * Created by alcereo on 22.02.17.
  */
case class User(
               uuid: Option[UUID],
               login: String,
               password: String
               )
