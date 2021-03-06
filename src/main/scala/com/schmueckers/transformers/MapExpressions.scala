package com.schmueckers.transformers

/**
  * @param key
  * @param exp
  */
class SetEntry(val key: String, val exp: Expression[Any])
  extends Output[Map[String,Any]]
    with Expression[MapSetter] {
  override def eval(ns: NS): MapSetter =
    (map: Map[String, Any]) =>
      map + (key -> exp.eval(ns))

  override def humanForm: String = s"(Set ${
    key
  } to ${
    exp.humanForm
  }"

  override def resolveT(ns: NS): Output[Map[String,Any]] =
    if (exp == exp.resolved(ns))
      this
    else
      new SetEntry(key, exp.resolved(ns))

  override def resolved( ns : NS ) = resolveT(ns)

  override def exps: List[Expression[Any]] = List(exp)

  override def equals(that: Any) = that match {
    case se: SetEntry => (se canEquals this) && key == se.key && exp == se.exp
  }

  def canEquals(other: Any) = other.isInstanceOf[SetEntry]
}

object SetEntry {
  def apply(key: String, exp: Expression[Any]) = new SetEntry(key, exp)

  def unapply(se: SetEntry) = Some(se.key, se.exp)
}

/**
  * A class that allows to take data from an inserted map
  *
  * This deprecated because it isn't really required. It would
  * just mean that we can pick up data which we have calculated in
  * the output before. This will not work as it will mean that
  * we return completely the wrong type.
  *
  * @param key
  * @tparam T
  */
@deprecated("This doesn't really make sense")
class GetEntry[T](val key: String) extends Expression[MapGetter[T]] {
  override def eval(ns: NS): MapGetter[T] = (m: Map[String, Any]) =>
    m.get(key).map(_.asInstanceOf[T]).get

  override def humanForm: String = s"Get(${key})"

  override def resolved(ns: NS): Expression[MapGetter[T]] = this

  override def exps: List[Expression[Any]] = List.empty
}

object GetEntry {
  def apply[T](key: String) = new GetEntry[T](key)
}
