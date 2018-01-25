package org.libnetrc

import org.scalatest._

class WriteTests extends FlatSpec with Matchers {
  it should "write the machine item" in {
    val machine = Machine("host1", "root", "123")
    val item = "machine host1 login root password 123"
    machine.toString shouldBe item
  }

  it should "write the machine item with account" in {
    val machine = Machine("host1", "root", "123", Some("456"))
    val item = "machine host1 login root password 123 account 456"
    machine.toString shouldBe item
  }

  it should "write the default item" in {
    val default = Default("root", "123")
    val item = "default login root password 123"
    default.toString shouldBe item
  }

  it should "write the default item with account" in {
    val default = Default("root", "123", Some("456"))
    val item = "default login root password 123 account 456"
    default.toString shouldBe item
  }

  it should "write the macdef item" in {
    val md = MacDef("copy",
      """|cp 1.txt 2.txt
         |rm -rf /""".stripMargin)
    val res =
      s"""|macdef copy
          |cp 1.txt 2.txt
          |rm -rf /\n\n""".stripMargin
    md.toString shouldBe res
  }

  it should "write whole netrc" in {
    val (p1, p2) = ("_jkldsj$#", "jhkdsah8793#")
    val netc = NetRc(Seq(
      Machine("shard.cloud.databricks.com", "anonymous", p1),
      Machine("shard2.cloud.databricks.com", "root", p2, Some("123")),
      Default("anonymous", "12345678")
    ))
    val res =
      s"""|machine shard.cloud.databricks.com login anonymous password $p1
          |machine shard2.cloud.databricks.com login root password $p2 account 123
          |default login anonymous password 12345678""".stripMargin
    netc.toString shouldBe res
  }
}