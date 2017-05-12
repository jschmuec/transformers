package com.schmueckers

/**
  * Created by js on 08/05/2017.
  */
package object transformers extends BasicExpressions {

  class NS(theMap: Map[String, Any]) {
    def apply(name: String) = theMap(name)

    def get[T](n: String) : Option[T] = theMap.get(n).map(_.asInstanceOf[T])
  }

  object NS {
    def apply(init: (String, Any)*) = new NS(Map(init: _*))
  }

  type MapSetter = (Map[String, Any] => Map[String, Any] )
  type MapGetter[T] = (Map[String, T] ) => T

  /**
    * A convenience conversion from constant values to
    * {{#Const}}
    *
    * @param t The value to be wrapped in a {{#Const}}
    * @tparam T The type of the value and the type parameter to Const[T]
    * @return A Const[T] wrapping the value t
    */
  implicit def valueToConst[T <: AnyVal](t: T) = Const[T](t)
  implicit def stringToConst( s : String ) = Const( s )
}
