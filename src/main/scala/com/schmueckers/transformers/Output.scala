package com.schmueckers.transformers

/**
  * A marker interface for output, I'm sure I'll figure out what to do with it.
  */
trait Output[T] extends Expression[T => T] {
  def +[S <: T]( right : Output[S]) = Chained( this, right )
}

class Chained[+T]( left : Expression[T => T], right : Expression[T => T]) extends Output[T] {
  override def eval(ns: NS): (T) => T =
    (t : T) => right.eval(ns)(left.eval(ns)(t))

  override def humanForm: String = ???

  override def resolved(ns: NS): Expression[(T) => T] = new Chained( left.resolved(ns), right.resolved(ns))
}

object Chained {
  def apply[T]( left : Expression[T => T], right : Expression[T => T] ) = new Chained( left, right )
}