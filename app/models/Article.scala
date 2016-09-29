package models

import play.api.libs.json.{JsPath, Json, Writes}
import play.twirl.api.Html
import play.api.libs.functional.syntax._

import scala.util.parsing.json.{JSONArray, JSONObject}


case class Article (
                     id: Int,
                     parentId: Option[Int],
                     title: String,
                     contentHtml: String,
                     contentMarkdown: String
                   )

case class graphData(
                    id: Option[String],
                    name: Option[String],
                    score: Option[Double],
                    source: Option[String],
                    target: Option[String],
                    content: Option[String]
                    )

case class graphPosition(
                        x:Double,
                        y:Double
                        )

case class graphElement(
                       data: graphData,
                       position: Option[graphPosition]
                       )

object Article{

  var maxId = 9

  var articles = Set(
    Article(
      8,
      None,
      "Scala. Общий конспект",
      "",
      ""
    ),
    Article(
      1,
      Some(8),
      "Объекты",
      "",
      ""
    ),
    Article(
      2,
      Some(1),
      "Типы",
      "<p>Все примитывныфе классы Java имеют отображения в примитивные классы Scala  и\nкомпилятор Scala использует типы Java, где возможно, чтобы выйграть в\nпроизводительности.</p>\n\n<p>Обычно типы определяет компилятор, но в случае, если функция рекурсивная, тип\nкоторый она возвращает, нужно прописывать.</p>\n\n<p>Возврат типа <code class=\"highlighter-rouge\">Unit</code> в Scala аналогичен <code class=\"highlighter-rouge\">void</code> в Java, т.е. путой, или не значимый.\nМетоды, с типом результата <code class=\"highlighter-rouge\">Unit</code> используются, только ради побочного эфекта.</p>",
      "Все примитывныфе классы Java имеют отображения в примитивные классы Scala  и\nкомпилятор Scala использует типы Java, где возможно, чтобы выйграть в\nпроизводительности.  \n\nОбычно типы определяет компилятор, но в случае, если функция рекурсивная, тип\nкоторый она возвращает, нужно прописывать.\n\nВозврат типа `Unit` в Scala аналогичен `void` в Java, т.е. путой, или не значимый.\nМетоды, с типом результата `Unit` используются, только ради побочного эфекта."
    ),
    Article(
      3,
      Some(2),
      "Коллекции",
      "<p>Коллекции делятся на мутабельные и немутабельные. Все расположены в пакете\n<code class=\"highlighter-rouge\">scala.collection</code>. Далее <code class=\"highlighter-rouge\">scala.collection.mutable</code> и <code class=\"highlighter-rouge\">scala.collection.immutable</code>.\nПо умолчанию берутся мутабельные.</p>\n\n<p>Доступ к элементам через круглые скобки.</p>\n\n<p>Когда эксземпляр используется как функция, то вызыввается метод <code class=\"highlighter-rouge\">apply()</code> этого\nкласса. Например в массивах, при получении элемента.\nПри изменении элемента массива - так же метод <code class=\"highlighter-rouge\">update(x:Int, arg:Any)</code></p>\n\n<p>Объявление возможно через переменное число аргументов <code class=\"highlighter-rouge\">apply</code>, т.е.:</p>\n<div class=\"language-scala highlighter-rouge\"><pre class=\"highlight\"><code><span class=\"k\">val</span> <span class=\"n\">numNames</span> <span class=\"k\">=</span> <span class=\"nc\">Array</span><span class=\"o\">(</span><span class=\"s\">\"zero\"</span><span class=\"o\">,</span> <span class=\"s\">\"one\"</span><span class=\"o\">,</span> <span class=\"s\">\"two\"</span><span class=\"o\">)</span>\n</code></pre>\n</div>",
      "Коллекции делятся на мутабельные и немутабельные. Все расположены в пакете\n`scala.collection`. Далее `scala.collection.mutable` и `scala.collection.immutable`.\nПо умолчанию берутся мутабельные.\n\nДоступ к элементам через круглые скобки.\n\nКогда эксземпляр используется как функция, то вызыввается метод `apply()` этого\nкласса. Например в массивах, при получении элемента.\nПри изменении элемента массива - так же метод `update(x:Int, arg:Any)`\n\nОбъявление возможно через переменное число аргументов `apply`, т.е.:\n```scala\nval numNames = Array(\"zero\", \"one\", \"two\")\n```"
    ),
    Article(
      4,
      Some(3),
      "Array - Массив",
      "<p>Переменная-массив объявленная через <code class=\"highlighter-rouge\">val</code> не может поменять тип, но элементы\nмассива можно изменить, так что массив сам по себе мутабелен.</p>",
      "Переменная-массив объявленная через `val` не может поменять тип, но элементы\nмассива можно изменить, так что массив сам по себе мутабелен."
    ),
    Article(
      5,
      Some(3),
      "List - Список",
      "<div class=\"language-scala highlighter-rouge\"><pre class=\"highlight\"><code><span class=\"k\">val</span> <span class=\"n\">oneTwoThree</span> <span class=\"k\">=</span> <span class=\"nc\">List</span><span class=\"o\">(</span><span class=\"mi\">1</span><span class=\"o\">,</span> <span class=\"mi\">2</span><span class=\"o\">,</span> <span class=\"mi\">3</span><span class=\"o\">)</span>\n</code></pre>\n</div>\n<p>Имутабельная версия массива - элементы менять нельзя. Используется в\nфункциональщине в основном. Предназначен для персистентного и структурного\nобмена данными , в котором обеспечивает значительную производительность, если\nправильно используется.</p>\n\n<p>В плане производительности оптимален для LIFO. Stack - паттернам использования.\nЕсли требуется FIFO или случайный доступ, лучше использовать другие классы.</p>\n\n<p>Не смотря на это, для операций  LIFO: конкатенации, и получения последнего -\nпрактически нулевая или <code class=\"highlighter-rouge\">O(1)</code> по сложности.</p>\n\n<p>Производительность: <code class=\"highlighter-rouge\">O(1)</code> для head/tail доступа. Другие операции, <code class=\"highlighter-rouge\">O(n)</code>, где\n<code class=\"highlighter-rouge\">n</code> - число элемнтов, даже такие как <code class=\"highlighter-rouge\">length</code>, <code class=\"highlighter-rouge\">append</code>, <code class=\"highlighter-rouge\">reverse</code>.</p>",
      "```scala\nval oneTwoThree = List(1, 2, 3)\n```\nИмутабельная версия массива - элементы менять нельзя. Используется в\nфункциональщине в основном. Предназначен для персистентного и структурного\nобмена данными , в котором обеспечивает значительную производительность, если\nправильно используется.\n\nВ плане производительности оптимален для LIFO. Stack - паттернам использования.\nЕсли требуется FIFO или случайный доступ, лучше использовать другие классы.\n\nНе смотря на это, для операций  LIFO: конкатенации, и получения последнего -\nпрактически нулевая или `O(1)` по сложности.\n\nПроизводительность: `O(1)` для head/tail доступа. Другие операции, `O(n)`, где\n`n` - число элемнтов, даже такие как `length`, `append`, `reverse`."
    ),
    Article(
      6,
      Some(3),
      "Tuple - кортеж",
      "<p>Похож на лист, только элементы могут быть разных типов.</p>\n\n<p>Доступ к элементам через <code class=\"highlighter-rouge\">*._n</code> - где n - номер элемента.</p>",
      "Похож на лист, только элементы могут быть разных типов.\n\nДоступ к элементам через `*._n` - где n - номер элемента."
    ),
    Article(
      7,
      Some(2),
      "Классы",
      "<p>Можно создавать объекты и классы с <code class=\"highlighter-rouge\">new</code> параметризируя переменными и типами.\nПеременными в круглых скобках - типами в квадратных. Типы всегда первыми.</p>\n<div class=\"language-scala highlighter-rouge\"><pre class=\"highlight\"><code><span class=\"k\">val</span> <span class=\"n\">greetStrings</span> <span class=\"k\">=</span> <span class=\"k\">new</span> <span class=\"nc\">Array</span><span class=\"o\">[</span><span class=\"kt\">String</span><span class=\"o\">](</span><span class=\"mi\">3</span><span class=\"o\">)</span>\n</code></pre>\n</div>\n\n<p>Когда эксземпляр используется как функция, то вызыввается метод <code class=\"highlighter-rouge\">apply()</code> этого\nкласса. Например в массивах, при получении элемента.\nПри изменении элемента массива - так же метод <code class=\"highlighter-rouge\">update(x:Int, arg:Any)</code></p>",
      "Можно создавать объекты и классы с `new` параметризируя переменными и типами.\nПеременными в круглых скобках - типами в квадратных. Типы всегда первыми.\n```scala\nval greetStrings = new Array[String](3)\n```\n\nКогда эксземпляр используется как функция, то вызыввается метод `apply()` этого\nкласса. Например в массивах, при получении элемента.\nПри изменении элемента массива - так же метод `update(x:Int, arg:Any)`"
    )
  )

  implicit val graphDataWriter = Json.writes[graphData]
  implicit val graphPositionWriter = Json.writes[graphPosition]
  implicit val graphElementWriter = Json.writes[graphElement]

  def findAllMainArticles = articles.toList.filter(_.parentId.isEmpty).sortBy(_.id)

  def findAll = articles.toList.sortBy(_.id)

  def findChildrens(id:Int) = articles.toList.filter(_.parentId.contains(id)).sortBy(_.id)

  def findByID(id:Int) = articles.find(_.id == id)

  def toJsonTest = {

    Json.toJson(
      articles.toSeq.map(
        art =>
          graphElement(
            graphData(
              Some(art.id.toString),
              Some(art.title),
              Some(art.id),
              None,
              None,
              Some(art.contentHtml.toString)
            ),
            Some(graphPosition(
              0,
              10 * art.id
            ))
          )
      ) ++ articles.toSeq.filter(_.parentId.isDefined).map(
        art =>
          graphElement(
            graphData(
              None,
              None,
              None,
              art.parentId.map(id => id.toString),
              Some(art.id.toString),
              None
            ),
            None
            )
          )
    ).toString()

  }

  def add(article:Article) = {
    articles = articles.filterNot(_.id == article.id)
    articles += article
  }

  def deleteById(id:Int) = {
    articles = articles.filterNot(_.id == id)
  }

  def getNewId = {
    maxId += 1
    print(maxId)
    maxId
  }

}
