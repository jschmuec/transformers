package com.schmueckers.transformers

import org.scalatest.{FunSpec, GivenWhenThen, Matchers}



class TestTransformers extends FunSpec with GivenWhenThen with Matchers {
  describe("A Const") {
    it("should return the right value") {
      Const("a").eval(NS()) should be("a")
    }
    it("should print nicely") {
      Const("a").humanForm should be(""""a"""")
      Const(1).humanForm should be("1")
    }
    it("should not have dependencies") {
      Const("a").dependencies should be(Set.empty[Expression[String]])
    }
    it("should resolve to itself") {
      Const("a").resolved(NS("a" -> 1)) should be(Const("a"))
    }
  }

  describe("A variable") {
    it("should pull the value from the namespace") {
      Variable[String]("a").eval(NS("a" -> "A", "b" -> "B")) should be("A")
    }
    it("should look nice in human form") {
      Variable("A").humanForm should be("A")
    }
    it("Should have itself as dependency") {
      val v = Variable("v")
      v.dependencies should be(Set(v))
    }
    it("Should resolve to the right value if available") {
      Variable("a").resolved(NS("a" -> 1)) should be(Const(1))
    }
    it("Should resolve to itself if not in NS") {
      Variable("a").resolved(NS()) should be(Variable("a"))
    }
  }
}
