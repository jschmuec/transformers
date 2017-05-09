package com.schmueckers.transformers

import org.scalatest.{FunSpec, GivenWhenThen, Matchers}

import scala.xml.NodeSeq

class TestMapExpressions
  extends FunSpec with GivenWhenThen with Matchers
    with BasicTransformers
{
  describe("MapSetter") {
    it("should return a function which sets the value in a map") {
      val f: MapSetter = new SetEntry("key", Const(1)).eval(NS())
      f(Map.empty[String, Any]) should be(Map("key" -> 1))
    }
    it("should have proper humanform") {
      new SetEntry("key", Const(1)).humanForm should be("(Set key to 1")
    }
    it("should resolve correctly") {
      new SetEntry("a", Variable("a")).resolved(NS("a" -> 1)) should be(new SetEntry("a", Const(1)))
    }
  }

  describe("MapGetter") {
    it("should allow to retrieve a value from a Map") {
      val f: MapGetter[Int] = new GetEntry("key").eval(NS())
      f(Map("key" -> 1)) should be(1)
    }
  }
}
