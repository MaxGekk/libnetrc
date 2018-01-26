package org.libnetrc

import java.io._
import java.nio.CharBuffer

import scala.io.Source
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

case class NetRc(items: Seq[Item]) {
  override def toString: String = {
    items.map(_.toString).mkString("\n")
  }

  def save(file: String, append: Boolean = false): Unit = {
    val fw = new FileWriter(file)
    try {
      fw.write(toString)
    } finally {
      fw.close()
    }
  }
}

object NetRcFile {
  def read(file: String): NetRc = {
    val str = Source.fromFile(file).mkString
    Parsers.parse(str) match {
      case Left(error) => throw error
      case Right(netc) => netc
    }
  }
}
