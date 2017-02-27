package models

/**
  * Created by alcereo on 21.02.17.
  */
sealed trait Role

case object Administrator extends Role
case object NormalUser extends Role