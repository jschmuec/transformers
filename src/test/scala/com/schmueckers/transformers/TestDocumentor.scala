package com.schmueckers.transformers

import org.scalatest.{FunSpec, GivenWhenThen, Matchers}
import com.schmueckers.jstools.xml
import com.schmueckers.jstools.xml.XmlMatcher._


class TestDocumentor extends FunSpec with GivenWhenThen with Matchers
  with XMLExtractors {

  import Documentor._

  describe("Documentor") {
    it("should create table rows when called on a SetEntry") {
      val t = generateHtmlDocumentation(new SetEntry("abc", Const(1)))
      t should have size 1
      t.head should beXml(
        <tr>
          <td>abc</td>
          <td>=1</td>
        </tr>)
    }
    it("should generate multiple rows when called with a chain") {
      val t = generateHtmlDocumentation(SetEntry("k1", Const(1)) + SetEntry("k2", Const(2)))
      t should have size 2
      t.head should beXml(<tr>
        <td>k1</td> <td>=1</td>
      </tr>)
      t(1) should beXml(<tr>
        <td>k2</td> <td>=2</td>
      </tr>
      )
    }
  }
}