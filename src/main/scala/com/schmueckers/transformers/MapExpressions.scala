package com.schmueckers.transformers

/**
  * Created by js on 08/05/2017.
  */
trait MapExpressions {
  type MapSetter = (Map[String, Any] => Map[String, Any])

  class SetEntry(val key: String, val exp: Expression[Any]) extends Expression[MapSetter] {
    override def eval(ns: NS): MapSetter =
      ( map : Map[String,Any] ) =>
        map + ( key -> exp.eval( ns ) )

    override def humanForm: String = s"(Set ${key} to ${exp.humanForm}"

    override def resolved(ns: NS): Expression[MapSetter] =
      if (exp == exp.resolved(ns))
        this
      else
        new SetEntry(key, exp.resolved(ns))

    override def exps: List[Expression[Any]] = List(exp)

    override def equals( that : Any ) = that match {
      case se : SetEntry => ( se canEquals this) && key == se.key && exp == se.exp
    }

    def canEquals( other : Any ) = other.isInstanceOf[SetEntry]
  }

  type MapGetter[T] = (Map[String,T]) => T

  class GetEntry[T]( val key : String  ) extends Expression[MapGetter[T]] {
    override def eval(ns: NS): MapGetter[T] = (m : Map[String,Any] ) =>
      m.get( key ).map( _.asInstanceOf[T] ).get

    override def humanForm: String = s"Get(${key})"

    override def resolved(ns: NS): Expression[MapGetter[T]] = this

    override def exps: List[Expression[Any]] = List.empty
  }
}
