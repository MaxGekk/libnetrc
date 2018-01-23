package org.libnetrc

import org.scalatest._

class ParserTests extends FlatSpec with Matchers {
  it should "parse the machine item " in {
    val item = "machine host1 login root password 123"
    Parsers.parse(item) shouldBe Right(NetRc(Seq(Machine("host1", "root", "123"))))
  }

  it should "parse the default item " in {
    val item = "default login root password 123"
    Parsers.parse(item) shouldBe Right(NetRc(Seq(Default("root", "123"))))
  }
}

