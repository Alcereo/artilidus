package models

import java.util.UUID

import play.api.libs.json.Json


case class Article(
                    uid: Option[UUID],
                    id: Option[Int],
                    parentUid: Option[UUID],
                    title: String,
                    contentHtml: String,
                    contentMarkdown: String,
                    noteUid: UUID
                  )


//  def findAllMainArticles = konspects.toList.map(konsp => Article.findByID(konsp.mainArticleId).getOrElse(Article(0,None,"ERROR","ERROR","ERROR",konsp.id))).sortBy(_.id)
//
//  def findAll = articles.toList.sortBy(_.id)
//
//  def findChildrens(id: Int) = articles.toList.filter(_.parentId.contains(id)).sortBy(_.id)
//
//  def findByID(id: Int) = articles.find(_.id == id)
//

//
//  def add(article: Article) = {
//    articles = articles.filterNot(_.id == article.id)
//    articles += article
//  }
//
//  def deleteById(id: Int) = {
//    articles = articles.filterNot(_.id == id)
//  }
//
//  def getNewId = {
//    maxId += 1
//    print(maxId)
//    maxId
//  }
//
//}
