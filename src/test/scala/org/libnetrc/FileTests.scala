package org.libnetrc

import org.scalatest._
import java.io.{File, FileWriter}

class FileTests extends FlatSpec with Matchers with BeforeAndAfter {
  val tmpDir = System.getProperty("java.io.tmpdir")
  val tmpFile = s"$tmpDir/.netrc_a1nkj3"

  def deleteNetRcFile(): Unit = {
    val file = new File(tmpFile)
    file.delete()
  }

  before {
    deleteNetRcFile()
  }

  it should "write .netrc" in {
    val netrc = NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("A", "B", "C", Some("D")),
      Default("w", "x")
    ))

    netrc.save(tmpFile)

    assert(new File(tmpFile).exists())
  }

  it should "read .netrc" in {
    val fileBody =
      """
        | machine a login b password c
        | machine AA login BB password CC account DD
        | default login ZZZ password XXX
      """.stripMargin
    val fw = new FileWriter(tmpFile)
    try { fw.write(fileBody) }
    finally { fw.close() }

    val netrc = NetRcFile.read(tmpFile)
    netrc shouldBe NetRc(Seq(
      Machine("a", "b", "c"),
      Machine("AA", "BB", "CC", Some("DD")),
      Default("ZZZ", "XXX")
    ))
  }

  it should "return non-empty path to .netrc" in {
    val name = NetRcFile.name

    name.contains("netrc") shouldBe true
  }

  after {
    deleteNetRcFile()
  }
}