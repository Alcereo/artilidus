package DAO

import java.util.UUID
import javax.inject.Inject

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by alcereo on 22.02.17.
  */
class UserDAO @Inject()(noteDAO: NoteDAO,protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]{

  import driver.api._

  val Users = TableQuery[UsersTable]


  def getUserByUID(id: UUID):Future[Option[User]] = db run Users.filter(_.uid === id).result.headOption

  def authentificate(login:String, password: String):Option[User] = {
    Await.result(
      db run
      Users
        .filter(_.login === login)
        .filter(_.password === password)
        .result.headOption,
      Duration.Inf)
  }

  class UsersTable(tag:Tag) extends Table[User](tag, "users"){

    def uid = column[UUID]("uid", O.PrimaryKey, O.AutoInc)
    def login = column[String]("login")
    def password = column[String]("password")


    def * = (uid.?, login, password) <> (User.tupled, User.unapply)

  }


}
