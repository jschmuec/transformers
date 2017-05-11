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


  class SideEffect[T]( v : => Expression[T] ) extends Expression[T] {
    override def eval(ns: NS): T = v.eval(ns)

    override def humanForm: String = s"SideEfect(${v.humanForm})"

    override def resolved(ns: NS): Expression[T] = {
      val r: Expression[T] = v.resolved( ns )
      new SideEffect(r)
    }

    override def exps: List[Expression[Any]] = v.exps
  }

  class Comment[T]( comment: String, exp : Expression[T] ) extends Expression[T] {override def eval(ns: NS): T = ???

    override def humanForm: String =
      s"""
         |// ${comment}
         |${exp.humanForm}
       """.stripMargin

    override def resolved(ns: NS): Expression[T] = new Comment( comment, exp.resolved((ns)))

    override def exps: List[Expression[Any]] = List(exp)
  }

  describe("If") {
    it("Should eval only the first expression if true") {
      var i = 0
      var j = 0
      new If( true, new SideEffect( Const( {
        i = i + 1
        i
      })),  Const( {
        j = j + 1
        j
      })  ).eval(NS()) should equal (1)
      i should equal (1)
      j should equal (0)
    }
    it("Should only eval the second condition if false") {
      var i = 0
      var j = 3
      new If( false, new SideEffect( Const( {
        i = i + 1
        i
      })),  Const( {
        j = j + 1
        j
      })  ).eval(NS()) should equal (4)
      i should equal (0)
      j should equal (4)
    }
    it( "Should not eval any of the conditions if not evaluated") {
      var i = 0
      var j = 0
      val iif = new If(true, new SideEffect(Const({
        i = i + 1
        i
      })), Const({
        j = j + 1
        j
      }))
      i should equal(0)
      j should equal(0)
    }
  }
}