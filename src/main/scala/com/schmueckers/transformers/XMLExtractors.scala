package com.schmueckers.transformers

import scala.xml.{Node, NodeSeq}

/**
  * Created by js on 06/05/2017.
  */
trait XMLExtractors {

  class \\[N <: Node](val exp: Expression[N], val path: String) extends Expression[NodeSeq] {
    override def eval(ns: NS): NodeSeq = exp.eval(ns) match {
      case n: scala.xml.Node => n \\ path
    }

    override def exps: List[Expression[Any]] = List(exp)
    override def humanForm: String = s"""${exp.humanForm} \\\\ "${path}""""

    override def dependencies: Set[Expression[Any]] = exp.dependencies

    override def resolved(ns: NS): Expression[NodeSeq] = new \\(exp.resolved(ns), path)

    override def equals(obj: scala.Any): Boolean =
      obj match {
        case t: \\[N] => (t canEqual this) && this.exp == t.exp && this.path == t.path
        case _ => false
      }

    def canEqual(other: AnyRef) = other.isInstanceOf[\\[N]]

    override def toString = humanForm
  }

  object \\ {
    def apply[N <: Node](exp: Expression[N], path: String) = new \\(exp, path)
  }


  class Text( val exp : Expression[Node] ) extends Expression[String] with PrefixExpressionT[String] {
    override def eval(ns: NS): String = exp.eval( ns ) match {
      case n : scala.xml.Node => n.text
    }

    override val name: String = "Text"

    override def exps: List[Expression[Any]] = List( exp )

    override def resolved(ns: NS): Expression[String] = Text( exp.resolved((ns)))

    override def equals(obj: scala.Any): Boolean = obj match {
      case Text( e ) => e == this.exp
      case _ => false
    }

    override def toString: String = s"Text(${exp})"

    override def humanForm: String = s"Text(${exp})"
  }
  object Text {
    def apply( exp : Expression[Node] ) = new Text( exp )
    def unapply( t : Text ) =  Some(t.exp)
  }
}
