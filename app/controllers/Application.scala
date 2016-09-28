package controllers

import play.api._
import play.api.mvc._
import play.twirl.api.Html
import models.Article
import play.api.libs.json.{JsValue, Json}

class Application extends Controller {

  implicit val ArticleWriter = Json.writes[Article]
  implicit val ArticleReader = Json.reads[Article]

  def index = Action {
    Ok(views.html.index("Your new application is ready.", Article.findAllMainArticles))
  }

  def article(id: Int) = Action{
    Article.findByID(id).map(
      art => Ok(views.html.post(art))
    ).getOrElse(BadRequest)
  }

  def addArticle() = Action{
    request =>
      print(request.body.toString)
      request.body.asJson match {
        case jsObj:Some[JsValue] =>
          jsObj.get.validate[Article].fold(
            invalid = {
              fieldErrors =>
                BadRequest(
                (for (err <- fieldErrors)
                  yield "field: " + err._1 + ", errors: " + err._2).toString
                )
            },
            valid = {
              obj =>
                Article.add(obj)
                Ok("")
            }
          )
        case _ => NotFound("Нет JSON тела!")
      }
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

  def md_test = Action{
    Ok(views.html.mdtest())
  }

  def newArticle = Action{
    Ok(
      views.html.articleEdit(
        Article(Article.getNewId,None,"","","")
      )
    )
  }

  def editArticle(id: Int) = Action {
    Article.findByID(id) match {
      case Some(article) => Ok(views.html.articleEdit(article))
      case None => NotFound
    }
  }

}