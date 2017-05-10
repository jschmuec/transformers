import com.schmueckers.transformers.{SetEntry, Variable, Documentor }

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

val chain = SetEntry("FirstName", Variable("GivenName")) +
  SetEntry("Name", Variable("LastName"))


saveToHtmlFile( "test.html", <html>
  <head>
  </head>
<body><table>
  {for (r <- Documentor.generateHtmlDocumentation(chain))
    yield <tr>
      {r}
    </tr>}</table>
</body></html> )

