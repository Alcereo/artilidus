package controllers

// Example
import java.util.UUID
import javax.inject.Inject

import DAO.UserDAO
import jp.t2v.lab.play2.auth._
import models.{Administrator, NormalUser, Role}
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.{ClassTag, classTag}

trait AuthConfigImpl extends AuthConfig {

  val auth_userDAO:UserDAO

  /**
    * A type that is used to identify a user.
    * `String`, `Int`, `Long` and so on.
    */
  type Id = UUID

  /**
    * A type that represents a user in your application.
    * `User`, `Account` and so on.
    */
  type User = models.User

  /**
    * A type that is defined by every action for authorization.
    * This sample uses the following trait:
    *
    * sealed trait Role
    * case object Administrator extends Role
    * case object NormalUser extends Role
    */
  type Authority = Role

  /**
    * A `ClassTag` is used to retrieve an id from the Cache API.
    * Use something like this:
    */
  val idTag: ClassTag[Id] = classTag[Id]

  /**
    * The session timeout in seconds
    */
  val sessionTimeoutInSeconds: Int = 3600

  /**
    * A function that returns a `User` object from an `Id`.
    * You can alter the procedure to suit your application.
    */
  def resolveUser(id: UUID)(implicit ctx: ExecutionContext): Future[Option[User]] = {
    Logger.info("Into resolve user with id: "+id)
    auth_userDAO.getUserByUID(id)
  }

  /**
    * If authorization failed (usually incorrect password) redirect the user as follows:
    */
  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])
                                  (implicit context: ExecutionContext): Future[Result] = {
    Future.successful(Forbidden("no permission"))
  }

  /**
    * A function that determines what `Authority` a user has.
    * You should alter this procedure to suit your application.
    */
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
//    ((user.role, authority) match {
//      case (Administrator, _)       => true
//      case (NormalUser, NormalUser) => true
//      case _                        => false
//    }
    true
  }

  /**
    * Where to redirect the user after a successful login.
    */
  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Logger.info("Success login ")
    Future.successful(Redirect(routes.Application.index))
  }

  /**
    * Where to redirect the user after logging out
    */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Sessions.login))

  /**
    * If the user is not logged in and tries to access a protected resource then redirect them as follows:
    */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Sessions.login))



  /**
    * (Optional)
    * You can custom SessionID Token handler.
    * Default implementation use Cookie.
    */
  override lazy val tokenAccessor = new CookieTokenAccessor(
    /*
     * Whether use the secure option or not use it in the cookie.
     * Following code is default.
     */
    cookieSecureOption = false,
    cookieMaxAge       = Some(sessionTimeoutInSeconds)
  )

}