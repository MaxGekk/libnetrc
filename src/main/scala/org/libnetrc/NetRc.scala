package org.libnetrc

import java.io._

import scala.io.Source
import scala.util.matching.Regex
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
  def deleteDefault: NetRc = {
    this.copy(items = items.filter(!_.isInstanceOf[Default]))
  }

  def delete(f: Machine => Boolean): NetRc = {
    this.copy(items = items.filterNot {
      case machine: Machine => f(machine)
      case _ => false
    })
  }

  def delete(name: String): NetRc = delete(m => m.name == name)
  def delete(regex: Regex): NetRc = delete(m => regex.findFirstIn(m.name).isDefined)

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

  def save(append: Boolean): Unit = save(NetRcFile.name, append)
}

object NetRcFile {
  def read(file: String): NetRc = {
    val str = Source.fromFile(file).mkString
    Parsers.parse(str) match {
      case Left(error) => throw error
      case Right(netc) => netc
    }
  }

  def read: NetRc = read(name)

  def name = {
    val osName = System.getProperty("os.name")
    val prefix = if (osName.toLowerCase.contains("windows")) "_" else "."
    val home = System.getProperty("user.home")

    s"${home}/${prefix}netrc"
  }
}
