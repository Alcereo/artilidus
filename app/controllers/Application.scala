package controllers

import play.api._
import play.api.mvc._
import play.twirl.api.Html
import models.Article

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready.", Article.findAllMainArticles))
  }

  def article(id: Int) = Action{
    Article.findByID(id).map(
      art => Ok(views.html.post(art))
    ).getOrElse(BadRequest)
  }

  def graphtest = Action{
    Ok(views.html.graphtest())
  }

  def graphTest2 = Action{
    Ok(views.html.graphtest2())
  }

  def writetest = Action{
    Ok(Article.toJsonTest)
  }

}