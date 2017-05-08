package com.schmueckers.transformers

import org.scalatest.{FunSpec, GivenWhenThen, Matchers}

import scala.xml.NodeSeq

class TestXMLExtractors extends FunSpec with GivenWhenThen with Matchers with XMLExtractors {

  describe("XML extractor \\\\") {
    it("should get the right node out of the XML") {
      \\(Const(<test><a>Hello</a></test>), "a").eval(NS()) should be(NodeSeq.fromSeq(Seq(<a>Hello</a>)))
    }
    it("should create nice human readable form") {
      \\(Const(<test><a>Hello</a></test>), "a").humanForm should be(""""<test><a>Hello</a></test>" \\ "a"""")
    }

    it("should show the right dependencies") {
      \\(Variable("a"), "b").dependencies should be(Set(Variable("a")))
    }
    it("should resolve correctly") {
      \\(Variable("a"), "b").resolved(NS("a" -> <test/>)) should be(\\(Const(<test/>), "b"))
    }
    it("should implement equals correctlky") {
      \\(Const(<test/>), "b") should be(\\(Const(<test/>), "b"))
    }
  }

  describe("Xml extractor Text") {
    it("should allow to get the Text of a node") {
      Text( Const(<test>TEST</test>)).eval( NS() ) should be ("TEST")
    }
    it("should render properly") {
      Text(Const(<test/>)).humanForm should be ("Text(Const(<test/>))")
    }
    it("should show dependencies" ) {
      Text(Variable("a")).dependencies should be (Set(Variable("a")))
    }
    it("shoudl resolve correctly") {
      val r = Text(Variable("b")).resolved( NS( "b" -> <test>a</test> ) )
      r should be (Text(Const(<test>a</test>)))
    }
    it("should compare correctly") {
      Text(Const(<test/>)) should be (Text(Const(<test/>)))
    }
  }
}
