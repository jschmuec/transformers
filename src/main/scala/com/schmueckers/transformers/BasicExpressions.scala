package com.schmueckers.transformers

/**
  * Created by js on 06/05/2017.
  */
trait BasicExpressions {

  def Mult[T](exps: Expression[T]*) = new InfixExpression(mult, 1, "*", exps.toList)

  private val mult: (Any, Any) => Any = {
    case (i: java.lang.Number, j: java.lang.Number) => i.doubleValue() * j.doubleValue()
  }

  def Add[T](exps: Expression[T]*) =
    new InfixExpression(add, 0, "+", exps.toList)

  private val add: (Any,Any) => Any = {
    case (i: java.lang.Number, j: java.lang.Number) => i.doubleValue() + j.doubleValue()
  }

  def Concat[T](exps: Expression[T]*) =
    new PrefixExpression(concat,
      "", "Concat", exps.toList)

  private val concat: (Any, Any) => Any = {
    case (x: Any, y: Any) => x.toString + y.toString
  }

  case class Switch[R,T](exp: Expression[R], cases: (Expression[R], Expression[T])*) extends Expression[T] {
    override def eval(ns: NS): T = {
      val value: R = exp.eval(ns)
      def rec(cases: List[(Expression[R], Expression[T])]): T =
        cases match {
          case Nil => throw new MatchError(s"Can't find match for ${value} in ${humanForm}")
          case (p, v) :: tail if p.eval(ns) == value => v.eval(ns)
          case h :: tail => rec(tail)
        }
      rec(cases.toList)
    }

    override def exps: List[Expression[Any]] = exp :: cases.flatMap( x => List(x._1,x._2) ).toList

    override def humanForm =
      s"""
         |${exp.humanForm} match {
         ${
        cases.map {
          case (p, v) => s"|  case ${p.humanForm} => ${v.humanForm}"
        }.mkString("\n")
      }
         |}
         | """.stripMargin

    //override def dependencies: Set[Expression] = exp.dependencies ++ cases.flatMap(x => Set(x._1, x._2))

    override def resolved(ns: NS): Expression[T] =
      Switch(exp.resolved(ns), cases map (x => (x._1.resolved(ns), x._2.resolved(ns))): _*)
  }
}
