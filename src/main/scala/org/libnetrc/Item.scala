package org.libnetrc

import scala.util.parsing.input.Positional

/**
  * A .netrc file has 2 main items - machine and default.
  * See https://linux.die.net/man/5/netrc
  */
sealed trait Item extends Positional

/**
  * The info required for auto-login process to the remote machine
  * @param name - a remote machine name
  * @param login - a user on the remote machine
  * @param password - a password required as a part of login process
  * @param account - an additional account password
  */
case class Machine(name: String,
                   login: String,
                   password: String,
                   account: Option[String] = None) extends Item {
  override def toString: String = {
    val item = s"machine $name login $login password $password"
    account.map(acc => item + s" account $acc").getOrElse(item)
  }
}

/**
  * This is the same as machine name except that default matches any name
  * @param login - the auto-login process will initiate a login using the specified name
  * @param password - user's password required for the login password
  * @param account - an additional password
  */
case class Default(login: String,
                   password: String,
                   account: Option[String] = None) extends Item {
  override def toString: String = {
    val item = s"default login $login password $password"
    account.map(acc => item + s" account $acc").getOrElse(item)
  }
}

/**
  * It defines a macro. Not supported by the library at the moment.
  * @param name - the name of the macro
  * @param commands - the list of commands. It begins with the next .netrc
  *                 line and continue until a null line
  *                 (consecutive new-line characters) is encountered
  */
case class MacDef(name: String, commands: String) extends Item {
  override def toString: String = {
    s"""|macdef $name
        |$commands\n\n""".stripMargin
  }
}
