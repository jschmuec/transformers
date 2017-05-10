package com.schmueckers.transformers

/**
  * A marker interface for output, I'm sure I'll figure out what to do with it.
  */
trait Output[T] extends Expression[T => T] {
  def +( right : Expression[T => T]) = Chained[T]( this, right )
}

class Chained[T]( val left : Expression[T => T], val right : Expression[T => T]) extends Output[T] {
  override def eval(ns: NS): (T) => T =
    (t : T) => right.eval(ns)(left.eval(ns)(t))

  override def humanForm: String = ???

  override def exps: List[Expression[Any]] = left :: right :: Nil

  override def resolved(ns: NS): Expression[(T) => T]  = new Chained( left.resolved(ns), right.resolved(ns))
}

object Chained {
  def apply[T]( left : Expression[T => T], right : Expression[T => T] ) = new Chained( left, right )
  def unapply[T]( c : Chained[T] ) = Some( c.left, c.right )
}