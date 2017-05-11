package com.schmueckers.transformers

import org.scalatest.{FunSpec, GivenWhenThen, Matchers}

class TestBasicExpressions extends FunSpec with GivenWhenThen with Matchers with BasicExpressions {

  describe("Concat") {
    it("Should concat three strings") {
      Concat(Const("A"), Const("B")).eval(NS()) should be("AB")
    }
    it("should look nice in human form") {
      Concat(Variable("a"), Variable("b")).humanForm should be("Concat(a,b)")
    }
    it("should use associativity when printing") {
      Concat(Variable("a"), Concat(Variable("b"), Variable("c"))).humanForm should be("Concat(a,b,c)")
    }
    it("should have the right dependencies") {
      Concat(Variable("a"), Variable("b")).dependencies should be(Set(Variable("a"), Variable("b")))
    }
    it("should resolve its components correctly") {
      Concat(Variable("a"), Variable("b")).resolved(NS("a" -> 1)) should be(
        Concat(Const(1), Variable("b"))
      )
    }
  }
  describe("FoldableExpression") {
    it("It should resolve correclty") {
      val sum = Add(Variable("a"), Variable("b"))
      val r = sum.resolved(NS("a" -> 1, "b" -> 2))
      r should be(Add(Const(1), Const(2)))
    }
  }

  describe("Add") {
    it("should add the values") {
      Add(Const(1), Const(2)).eval(NS()) should be(3)
    }
    it("should print in infix notation") {
      Add(Const(1), Const(2)).humanForm should be("1+2")
    }
  }
  describe("Mult") {
    it("should multiply") {
      Mult(Const(2), Const(3)).eval(NS()) should be(6)
    }
    it("should print in infix notation") {
      Mult(Const(1), Const(2)).humanForm should be("1*2")
    }
    it("should resolve correctly") {
      Mult(Variable("a"), Variable("b")).resolved(NS("a" -> 1, "b" -> 2)) should be(Mult(Const(1), Const(2)))
    }
  }
  describe("Switch") {
    it("should find the right value") {
      Switch(Const(1),
        (Const(2), Const("Wrong")),
        (Const(1), Const("Right"))
      ).eval(NS()) should be("Right")
    }

    it("should print nicely") {
      Switch(Const(1), Const(2) -> Const("Wrong"), Const(1) -> Const("Right")).humanForm should be(
        """
          |1 match {
          |  case 2 => "Wrong"
          |  case 1 => "Right"
          |}
          | """.stripMargin)
    }
    it("should show the right dependencies") {
      Switch(Variable("1"), Variable("2") -> Concat(Variable("Wrong"))).dependencies should be(
        Set(Variable("1"), Variable("2"), Variable("Wrong"))
      )
    }
    it("should resolve correctly") {

      Switch(Variable("a"), Variable("b") -> Variable("c")).resolved(NS("a" -> 1, "b" -> 2, "c" -> 3)) should be(
        Switch(Const(1), Const(2) -> Const(3))
      )
    }
  }



  describe("If") {
    it("Should eval only the first expression if true") {
      var i = 0
      var j = 0
      new If(true, {
        i = i + 1
        i
      }, {
        j = j + 1
        j
      }).eval(NS()) should equal(1)
      i should equal(1)
      j should equal(0)
    }
    it("Should only eval the second condition if false") {
      var i = 0
      var j = 3
      new If(false, {
        i = i + 1
        i
      }, {
        j = j + 1
        j
      }).eval(NS()) should equal(4)
      i should equal(0)
      j should equal(4)
    }
    it("Should not eval any of the conditions if not evaluated") {
      var i = 0
      var j = 0
      val iif = new If(true, Const({
        i = i + 1
        i
      }), Const({
        j = j + 1
        j
      }))
      i should equal(0)
      j should equal(0)
    }
    it("return the correct dependencies") {
      If(Variable("a"), Variable("b"), Variable("c")).dependencies should equal(Set(Variable("a"),Variable("b"),Variable("c")))
    }

    it("should return a good humanForm ") {
      // TODO find a way that I can pass in constants directly
      If(true, Const("Yes"), Const("No")).humanForm should equal(
        s"""|
           |if ( true ) {
           |  "Yes"
           |} else {
           |  "No"
           |}""".stripMargin)
    }
  }
  describe("Comment") {
    it("should return the same value as the expression inside") {
      Comment("dummy", 1).eval(NS()) should be(1)
    }
    it("Should pass the right human form") {
      Comment("hello", 1).humanForm should equal(
        """ |// hello
            |1""".stripMargin)
    }
  }
}
