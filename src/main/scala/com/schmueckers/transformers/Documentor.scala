package com.schmueckers.transformers

/**
  * Created by js on 10/05/2017.
  */
object Documentor {
  def generateHtmlDocumentation[T](expression: Expression[T]) : List[ scala.xml.Node ] = expression match {
    case SetEntry( key, exp ) => List( <tr><td>{key}</td><td>{exp.humanForm}</td></tr> )
    case Chained( left, right ) => generateHtmlDocumentation( left ) ++ generateHtmlDocumentation( right )
  }
}
