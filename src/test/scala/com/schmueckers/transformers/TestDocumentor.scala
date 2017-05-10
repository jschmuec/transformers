package com.schmueckers.transformers

import org.scalatest.{FunSpec, GivenWhenThen, Matchers}
import com.schmueckers.jstools.xml
import com.schmueckers.jstools.xml.XmlMatcher._

trait Documentor {
    def generateHtmlDocumentation[T](expression: Expression[T]) : List[ scala.xml.Node ] = expression match {
      case SetEntry( key, exp ) => List( <tr><td>{key}</td><td>{exp.humanForm}</td></tr> )
      case Chained( left, right ) => generateHtmlDocumentation( left ) ++ generateHtmlDocumentation( right )
    }
}

class TestDocumentor extends FunSpec with GivenWhenThen with Matchers
  with XMLExtractors

  with Documentor {
  describe("Documentor") {
    it("should create an empty table for an empty rule set") {
      val t = generateHtmlDocumentation( new SetEntry( "abc", Const(1) ) )
      t.head should beXml (
        <tr>
        <td>abc</td>
        <td>1</td>
        </tr>)
    }
  }
}