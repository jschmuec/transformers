package com.schmueckers.transformers

/**
  * A marker interface for output, I'm sure I'll figure out what to do with it.
  */
trait Output[T] extends Expression[T => T] {
  def +( right : Output[T]) : Output[T] = Chained[T]( this, right )
  def resolveT( ns : NS ) : Output[T]
}

class Chained[T]( val left : Output[T], val right : Output[T]) extends Output[T] {
  override def eval(ns: NS): (T) => T =
    (t : T) => right.eval(ns)(left.eval(ns)(t))

  override def humanForm: String = s"Chained( ${left.humanForm}, ${right.humanForm} )"

  override def exps: List[Expression[Any]] = left :: right :: Nil

  override def resolved(ns: NS): Expression[(T) => T]  = new Chained( left.resolveT(ns), right.resolveT(ns))
  override def resolveT( ns : NS ) = new Chained( left.resolveT(ns), right.resolveT(ns) )
}

object Chained {
  def apply[T]( left : Output[T], right : Output[T] ) = new Chained( left, right )
  def unapply[T]( c : Chained[T] ) = Some( c.left, c.right )
}