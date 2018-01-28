package org.libnetrc

import org.scalatest._

class ItemsTests extends FlatSpec with Matchers {
  it should "delete all machine items equal to the given name" in {
    val netrc = NetRc(Seq(
      Machine("host.cloud.org", "b", "c"),
      Machine("machine.databricks.com", "d", "e"),
      Machine("host2.int.cloud.org", "g", "h"),
      Machine("localhost", "y", "z")
    ))
    val newNetRc = netrc.delete("machine.databricks.com")

    newNetRc shouldBe NetRc(Seq(
      Machine("host.cloud.org", "b", "c"),
      Machine("host2.int.cloud.org", "g", "h"),
      Machine("localhost", "y", "z")
    ))
  }

  it should "delete all machine items matched to the given regex" in {
    val netrc = NetRc(Seq(
      Machine("host.cloud.org", "b", "c"),
      Machine("machine.databricks.com", "d", "e"),
      Machine("host2.int.cloud.org", "g", "h"),
      Machine("localhost", "y", "z")
    ))
    val newNetRc = netrc.delete(""".*\.org""".r)

    newNetRc shouldBe NetRc(Seq(
      Machine("machine.databricks.com", "d", "e"),
      Machine("localhost", "y", "z")
    ))
  }

  it should "delete all defaults" in {
    val netrc = NetRc(Seq(
      Machine("a", "b", "c"),
      Default("d", "e"),
      Machine("f", "g", "h"),
      Default("x", "y")
    ))
    val newNetRc = netrc.deleteDefault

    newNetRc shouldBe NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("f", "g", "h")
    ))
  }
}