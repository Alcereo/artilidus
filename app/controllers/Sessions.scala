package controllers

import java.util.UUID
import javax.inject.Inject

import DAO.UserDAO
import jp.t2v.lab.play2.auth.LoginLogout
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
import play.api.mvc.{Action, Controller}
import views.html

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
  * Created by alcereo on 22.02.17.
  */
class Sessions @Inject()(userDAO: UserDAO)
  extends Controller with LoginLogout with AuthConfigImpl{

  override val auth_userDAO: UserDAO = userDAO

  val loginForm = Form {
    mapping(
      "login" -> nonEmptyText,
      "password" -> text)(auth_userDAO.authentificate)(_.map(u => (u.login, u.password))
    ).verifying("Invalid email or password", result => result.isDefined)
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"
    ))
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.login(formWithErrors))),
      { user =>
        Logger.info(s"$user in authentificates")
        gotoLoginSucceeded(user.get.uuid.get)(request, defaultContext)
      }
    )
  }

}
