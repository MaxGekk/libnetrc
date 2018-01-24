package org.libnetrc

import org.scalatest._

class ParserTests extends FlatSpec with Matchers {
  it should "parse the machine item" in {
    val item = "machine host1 login root password 123"
    Parsers.parse(item) shouldBe Right(NetRc(Seq(Machine("host1", "root", "123"))))
  }

  it should "parse a multi line machine item" in {
    val (m, l, p) = ("shard", "root1", "123")
    val item =
      s"""
        | machine $m
        | login $l
        | password $p""".stripMargin
    Parsers.parse(item) shouldBe Right(NetRc(Seq(
      Machine(m, l, p)
    )))
  }

  it should "parse the default item" in {
    val item = "default login root password 123"
    Parsers.parse(item) shouldBe Right(NetRc(Seq(Default("root", "123"))))
  }

  it should "parse a multi line default item" in {
    val (l, p) = ("anonymous", "123")
    val item =
      s"""
         | default
         | login $l
         | password $p""".stripMargin
    Parsers.parse(item) shouldBe Right(NetRc(Seq(
      Default(l, p)
    )))
  }

  it should "parse machine with stronger name" in {
    val m = "shard1.databricks.com"
    val item = s"machine $m login anonymous password 123"
    Parsers.parse(item) shouldBe Right(NetRc(Seq(
      Machine(m, "anonymous", "123")
    )))
  }

  it should "parse default with strong password" in {
    val p = "a1!L2*sD3_+~?.9-=Zn"
    val item = s"default login anonymous password $p"
    Parsers.parse(item) shouldBe Right(NetRc(Seq(
      Default("anonymous", p)
    )))
  }

  it should "parse default with email as login" in {
    val l = "User.name@mail.com"
    val item = s"default login $l password _"
    Parsers.parse(item) shouldBe Right(NetRc(Seq(
      Default(l, "_")
    )))
  }

  it should "parse account in the machine item" in {
    val item = "machine host1.com login root@mail.com password 123 account 456"
    Parsers.parse(item) shouldBe Right(NetRc(Seq(
      Machine("host1.com", "root@mail.com", "123", Some("456"))
    )))
  }

  it should "parse account in the default item" in {
    val item = "default login root@mail.com password hello123 account acc456"
    Parsers.parse(item) shouldBe Right(NetRc(Seq(
      Default("root@mail.com", "hello123", Some("acc456"))
    )))
  }

  it should "parse many items" in {
    val items =
      s"""
         | machine shard1.cloud.databricks.com login user@mail.com password 123
         | default login root@mail.com password hello123 account acc456
         |""".stripMargin
    Parsers.parse(items) shouldBe Right(NetRc(Seq(
      Machine("shard1.cloud.databricks.com", "user@mail.com", "123"),
      Default("root@mail.com", "hello123", Some("acc456"))
    )))
  }

  it should "parse many multi line items" in {
    val items =
      s"""
         | machine shard1.cloud.databricks.com
         | login user@mail.com
         | password 123
         |
         | machine shard2.cloud.databricks.com
         | login user2@mail.com
         | password helloworld
         |
         | default login root@mail.com password hello123 account acc456
         |""".stripMargin
    Parsers.parse(items) shouldBe Right(NetRc(Seq(
      Machine("shard1.cloud.databricks.com", "user@mail.com", "123"),
      Machine("shard2.cloud.databricks.com", "user2@mail.com", "helloworld"),
      Default("root@mail.com", "hello123", Some("acc456"))
    )))
  }

  it should "parse macdef" in {
    val macdef =
      s"""
         | macdef init
         | cp a.txt b.txt
         |
       """.stripMargin
    Parsers.parse(macdef).isLeft shouldBe true
  }
}

