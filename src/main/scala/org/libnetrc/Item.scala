package org.libnetrc

import scala.util.parsing.input.Positional

sealed trait Item extends Positional
case class Machine(name: String,
                   login: String,
                   password: String,
                   account: Option[String] = None) extends Item {
  override def toString: String = {
    val item = s"machine $name login $login password $password"
    account.map(acc => item + s" account $acc").getOrElse(item)
  }
}

case class Default(login: String,
                   password: String,
                   account: Option[String] = None) extends Item {
  override def toString: String = {
    val item = s"default login $login password $password"
    account.map(acc => item + s" account $acc").getOrElse(item)
  }
}
case class MacDef(name: String, commands: String) extends Item {
  override def toString: String = {
    s"""|macdef $name
        |$commands\n\n""".stripMargin
  }
}