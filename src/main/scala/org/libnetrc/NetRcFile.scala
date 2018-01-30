package org.libnetrc

import java.io.FileNotFoundException
import scala.io.Source

/**
  * Methods for accessing to .netrc - reading/parsing
  */
object NetRcFile {
  /**
    * Reads and parses a .netrc file
    * @param file - the path to a .netrc
    * @return parsed representation of the .netrc. See [[NetRc]]
    * @throws FileNotFoundException  if the file does not exist,
    *         is a directory rather than a regular file,
    *         or for some other reason cannot be opened for reading.
    */
  @throws(classOf[FileNotFoundException])
  def read(file: String): NetRc = {
    val str = Source.fromFile(file).mkString
    Parsers.parse(str)
  }

  /**
    * Reads and parses the standard netrc file in the home directory
    * @return an instance of [[NetRc]]
    */
  @throws(classOf[FileNotFoundException])
  def read: NetRc = read(name)

  /**
    * Forms a path to standard .netrc. It is OS dependent path.
    * @return the absolute path to the .netrc file (in Linux and MacOS) or
    *         to _netrc in the case of Windows
    */
  def name = {
    val osName = System.getProperty("os.name")
    val prefix = if (osName.toLowerCase.contains("windows")) "_" else "."
    val home = System.getProperty("user.home")

    s"${home}/${prefix}netrc"
  }
}