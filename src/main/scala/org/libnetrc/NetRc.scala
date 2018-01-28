package org.libnetrc

import java.io._
import scala.util.matching.Regex

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

  def upsert(machine: Machine): NetRc = {
    val newItems = items.map {
      case m:Machine if m.name == machine.name => machine
      case item => item
    }
    val updated = this.copy(items = if (!newItems.exists(_ == machine)) {
        items :+ machine
      } else {
        newItems
      }
    )
    updated.withFixedDefaults
  }

  def upsert(default: Default): NetRc = {
    val withoutDefaults = this.deleteDefault

    withoutDefaults.copy(items = withoutDefaults.items :+ default)
  }

  def find(name: String): Option[Machine] = items.collect {
      case m: Machine if m.name == name => m
  }.headOption

  def find(regex: Regex): Iterable[Machine] = items.collect {
    case m: Machine if regex.findFirstIn(m.name).isDefined => m
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

  def withFixedDefaults: NetRc = {
    val firstDefault = items.collectFirst {case d: Default => d}
    val withoutDefaults = deleteDefault

    withoutDefaults.copy(items = withoutDefaults.items ++ firstDefault)
  }
}