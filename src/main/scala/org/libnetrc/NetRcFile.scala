package org.libnetrc

import scala.io.Source

object NetRcFile {
  def read(file: String): NetRc = {
    val str = Source.fromFile(file).mkString
    Parsers.parse(str)
  }

  def read: NetRc = read(name)

  def name = {
    val osName = System.getProperty("os.name")
    val prefix = if (osName.toLowerCase.contains("windows")) "_" else "."
    val home = System.getProperty("user.home")

    s"${home}/${prefix}netrc"
  }
}