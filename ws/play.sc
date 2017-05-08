import java.security.Timestamp

class User private(val userName: String,
                   val email: String,
                   val timeStamp: Timestamp =
                   new Timestamp(System.currentTimeMillis)) {

  def copy(uName: String = userName,
           eMail: String = email) =
    new User(uName, eMail, timeStamp)
}

object User {
  def apply(userName: String, email: String) =
    new User(userName, email)

  def unapply(u: User) = Some((u.userName, u.email, u.timeStamp))
}