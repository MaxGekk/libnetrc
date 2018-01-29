package org.libnetrc

import java.io._
import scala.util.matching.Regex

case class NetRc(items: Seq[Item]) {
  /**
    * Deletes all default items. For instance:
    * {{{
    *   val netRc = NetRc(Seq(
    *     Machine("host1", "user1", "123"),
    *     Default("anonymous", "password"),
    *     Default("default", "1234")
    *   ))
    *   netRc.deleteDefault.toString
    *   "machine host1 login user1 password 123"
    * }}}
    * @return
    */
  def deleteDefault: NetRc = {
    this.copy(items = items.filter(!_.isInstanceOf[Default]))
  }

  /**
    * Deletes all machine items that are satisfied to given condition
    * @param f - the function indicates which machine item should be deleted
    * @return new NetRc instance without deleted machine items
    */
  def delete(f: Machine => Boolean): NetRc = {
    this.copy(items = items.filterNot {
      case machine: Machine => f(machine)
      case _ => false
    })
  }

  /**
    * Deletes the machine item with given name. For example:
    * {{{
    *   val nameToDelete = "shard1.cloud.databricks.com"
    *   val netRc = NetRc(Seq(
    *     Machine("localhost", "user1", "123"),
    *     Machine(nameToDelete, "user.name@gmail.com", "pass123"),
    *     Default("anonymous", "12345678")
    *   ))
    *   netrc.delete(nameToDelete).toString
    *   """machine localhost login user1 password 123
    *      default login anonymous password 12345678"""
    * }}}
    * @param name - the name of machine to delete
    * @return new instance of NetRc without the machine items with given name
    */
  def delete(name: String): NetRc = delete(m => m.name == name)

  /**
    * Deletes all machine items that are matched to specified regex. For example:
    * {{{
    *   val namesToDelete = """.*\.com""".r
    *   val netRc = NetRc(Seq(
    *     Machine("shard2.provider.com", "user1", "123"),
    *     Machine("shard1.cloud.databricks.com", "user.name@gmail.com", "pass123"),
    *     Default("anonymous", "12345678")
    *   ))
    *   netrc.delete(namesToDelete).toString
    *   """default login anonymous password 12345678"""
    * }}}
    * @param regex a regular expression used by the [[java.util.regex]] package to find matches
    * @return new NetRc instance with items that are not matched to the given regex
    */
  def delete(regex: Regex): NetRc = delete(m => regex.findFirstIn(m.name).isDefined)

  /**
    * Serializes a NetRc instance to a string. For example:
    * {{{
    *   NetRc(Seq(
    *     Machine(
    *       name = "myshard.cloud.databricks.com",
    *       login = "myname@gmail.com", password = "simple"
    *     ),
    *     Default(
    *       login = "anonymous", password = "12345678",
    *       account = "123"
    *     )
    *   )).toString
    *   // It is serialized to the string:
    *   "machine myshard.cloud.databricks.com login myname@gmail.com password simple
    *    default login anonymous password 12345678 account 123"
    * }}}
    * @return a string that consists of many lines per each item
    */
  override def toString: String = {
    items.map(_.toString).mkString("\n")
  }

  /**
    * Appends the machine item before any default items it the item doesn't exist in the NetRc
    * or replace existing items with the same name as the given one has. For example:
    * {{{
    *   val netRc = NetRc(Seq(
    *     Machine("host1", "username", "pass123"),
    *     Default("anonymous", "123")
    *   ))
    *   netRc.upsert(Machine("host2", "user", "123")).toString
    *     "machine host1 login username password pass123
    *      machine host2 login user password 123
    *      default login anonymous password 123"
    *   netRc.upsert(Machine("host1", "root", "qwerty"))
    *     "machine host1 login root password qwerty
    *      machine host2 login user password 123
    *      default login anonymous password 123"
    * }}}
    * Note: If the NetRc has multiple default items, only the first default item
    * will be still presented in new NetRc instance after the upsert operation.
    * @param machine - the machine item to append or replace existing one
    * @return new instance of NetRc with appended/replaced by the given machine item.
    */
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

  /**
    * Updates/appends a default item into a NetRc instance. For example, if a NetRc
    * has wrong structure with multiple default items like:
    * {{{
    *   val netRc = NetRc(Seq(
    *     Default(login = "anonymous", password = "123"),
    *     Machine("host1", "user", "321"),
    *     Default(login = "default_user", password = "qwerty")
    *   ))
    *   netRc.upsert(Default("anonymous", "Hello")).toString
    *      "machine host1 login user password 321
    *       default login anonymous password Hello"
    * }}}
    * @param default - the default item to append/replace
    * @return new NetRc instance with the given default item
    */
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