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

  it should "keep only one default" in {
    val netrc = NetRc(Seq(
      Default("g", "h"),
      Machine("a", "b", "c"),
      Default("x", "y")
    ))
    val newNetRc = netrc.withFixedDefaults

    newNetRc shouldBe NetRc(Seq(
      Machine("a", "b", "c"),
      Default("g", "h")
    ))
  }

  it should "upsert a machine item" in {
    val netrc = NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("f", "g", "h"),
      Default("x", "y")
    ))
    val newNetRc = netrc.upsert(Machine("f", "1", "2"))

    newNetRc shouldBe NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("f", "1", "2"),
      Default("x", "y")
    ))
  }

  it should "upsert new machine item" in {
    val netrc = NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("f", "g", "h"),
      Default("x", "y")
    ))
    val newNetRc = netrc.upsert(Machine("host", "log", "pass"))

    newNetRc shouldBe NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("f", "g", "h"),
      Machine("host", "log", "pass"),
      Default("x", "y")
    ))
  }

  it should "upsert a default item" in {
    val netrc = NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("f", "g", "h"),
      Default("x", "y")
    ))
    val newNetRc = netrc.upsert(Default("log", "pass"))

    newNetRc shouldBe NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("f", "g", "h"),
      Default("log", "pass")
    ))
  }

  it should "find a machine by name" in {
    val netrc = NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("f", "g", "h"),
      Default("x", "y")
    ))
    val machine = netrc.find(name = "a")

    machine.get shouldBe Machine("a", "b", "c")
  }

  it should "find a machine by regex" in {
    val netrc = NetRc(Seq(
      Machine("a.b.com", "b", "c"),
      Machine("cloud.b.com", "g", "h"),
      Default("x", "y")
    ))
    val machine = netrc.find("""^a.*""".r)

    machine.toList shouldBe List(Machine("a.b.com", "b", "c"))
  }
}