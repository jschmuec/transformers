package com.schmueckers

/**
  * Created by js on 08/05/2017.
  */
package object transformers extends BasicTransformers {

  class NS(theMap: Map[String, Any]) {
    def apply(name: String) = theMap(name)

    def get[T](n: String) : Option[T] = theMap.get(n).map(_.asInstanceOf[T])
  }

  object NS {
    def apply(init: (String, Any)*) = new NS(Map(init: _*))
  }

  type MapSetter = (Map[String, Any] => Map[String, Any] )
  type MapGetter[T] = (Map[String, T] ) => T
}
