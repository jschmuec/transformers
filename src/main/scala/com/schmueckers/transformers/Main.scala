package com.schmueckers.transformers

import scala.xml.Node

/**
  * Created by js on 10/05/2017.
  */
object Main extends App {

  import com.schmueckers.transformers.{SetEntry, Variable, Documentor}

  val doctype = scala.xml.dtd.DocType(
    "html",
    scala.xml.dtd.PublicID(
      "-//W3C//DTD XHTML 1.0 Strict//EN",
      "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    ),
    Nil
  )

  def saveToHtmlFile(fileName: String, n: scala.xml.Node) =
    scala.xml.XML.save(fileName, n, "UTF-8", true, doctype)

  val chain =
    SetEntry("FirstName", Variable("GivenName")) +
    SetEntry("Name", Variable("LastName")) +
    SetEntry("Age", Add(Const(1), Const(42)))


  val doc: List[Node] = Documentor.generateHtmlDocumentation(chain)

  println(doc)
  saveToHtmlFile("FunnyTest.html", <html>
    <head>
      <style type="text/css">
        th {{
        text-align: left;
        }}

        table, th, td {{
        border: 1px solid black;
        }}
      </style>
    </head>
    <body>
      <table>
        <thead>
          <tr>
            <th>Target</th>
            <th>Source</th>
          </tr>
        </thead>
        <tobdy>
          {for (r <- Documentor.generateHtmlDocumentation(chain))
          yield <tr>
            {println(s"outputting $r")
            r}
          </tr>}
        </tobdy>
      </table>
    </body>
  </html>)

}
