package com.schmueckers.transformers

/**
  * Created by js on 08/05/2017.
  */
trait Expression[+T] {
  def eval(ns: NS): T

  def humanForm: String

  def resolved(ns: NS): Expression[T]

  def exps: List[Expression[Any]]

  def dependencies: Set[Expression[Any]] = Set(exps.flatMap(_.dependencies): _*)
}

case class Variable[T](name: String) extends Expression[T] {
  override def eval(ns: NS): T = ns(name).asInstanceOf[T]

  override def humanForm: String = name

  override def exps = Nil

  override def dependencies: Set[Expression[Any]] = Set(this)

  override def resolved(ns: NS): Expression[T] = ns.get[T](name).map(Const[T](_)).getOrElse(this)
}

class If[T](condition: Expression[Boolean], e1: => Expression[T], e2: => Expression[T]) extends Expression[T] {
  override def eval(ns: NS): T =
    if (condition.eval(ns))
      e1.eval(ns)
    else
      e2.eval(ns)

  override def humanForm: String =
    s"""
       |if ( ${condition.humanForm} ) {
       |  ${e1.humanForm}
       |} else {
       |  ${e2.humanForm}
       |}
     """.stripMargin

  override def exps: List[Expression[Any]] = List(condition, e1, e2)

  override def resolved(ns: NS): Expression[T] = new If(condition.resolved(ns), e1.resolved(ns), e2.resolved(ns))
}

case class Const[T](value: T) extends Expression[T] {
  override def eval(ns: NS): T = value

  override def humanForm: String = value match {
    case i: Int => i.toString
    case other => s""""${other.toString}""""
  }

  override def exps: List[Expression[Any]] = Nil

  override def resolved(ns: NS): Expression[T] = this
}


trait FoldableExpression[T] extends Expression[T] {
  def name: String

  def exps: List[Expression[T]]

  override def equals(that: Any): Boolean = that match {
    case t: FoldableExpression[_] => {
      (t canEqual this) &&
        op == t.op && seed == t.seed && name == t.name && exps == t.exps
    }
    case _ => false
  }

  def canEqual(other: Any) = other.isInstanceOf[FoldableExpression[T]]

  override def eval(ns: NS): T = {
    val t = exps.map(_.eval(ns))
    t.fold(seed)(op)
  }

  def op: (T, T) => T

  def seed: T

  def clone(exps: List[Expression[T]]): FoldableExpression[T]

  override def resolved(ns: NS): Expression[T] = {
    val t: List[Expression[T]] = exps map (_.resolved(ns))
    clone(t)
  }
}

final class InfixExpression[T](val op: (T, T) => T,
                               val seed: T,
                               val name: String,
                               val exps: List[Expression[T]])
  extends FoldableExpression[T] {
  def humanForm = {
    val t: List[String] = for {
      exp <- exps
      flattened <- exp match {
        case ie: InfixExpression[_] if ie.op == op => ie.exps
        case other => List(other)
      }
    } yield flattened.humanForm
    t.mkString(name)
  }

  override def equals(that: Any): Boolean =
    that match {
      case ie: InfixExpression[_] => ie.op == op && ie.seed == seed && ie.name == name && ie.exps == exps
      case _ => false
    }

  override def canEqual(other: Any) = other.isInstanceOf[InfixExpression[T]]

  override def clone(exps: List[Expression[T]]): FoldableExpression[T] = new InfixExpression(op, seed, name, exps)
}

trait PrefixExpressionT[T] {
  def name: String

  def exps: List[Expression[Any]]

  def humanForm = {
    val t: List[String] = for {
      exp <- exps
      flattened <- exp match {
        case pe: PrefixExpression[_] => pe.exps
        case other => List(other)
      }
    } yield flattened.humanForm
    s"${name}(${t.mkString(",")})"
  }
}

final case class PrefixExpression[T](val op: (T, T) => T,
                                     val seed: T,
                                     val name: String,
                                     val exps: List[Expression[T]])
  extends FoldableExpression[T] with PrefixExpressionT[T] {

  override def equals(that: Any): Boolean =
    that match {
      case pe: PrefixExpression[_] => pe.op == op && pe.seed == seed && pe.name == name && pe.exps == exps
      case _ => false
    }

  override def canEqual(other: Any) = other.isInstanceOf[InfixExpression[T]]

  override def clone(exps: List[Expression[T]]): FoldableExpression[T] = new PrefixExpression(op, seed, name, exps)
}

