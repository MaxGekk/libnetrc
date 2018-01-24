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
}

