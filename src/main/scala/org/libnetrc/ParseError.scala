package org.libnetrc

import scala.util.parsing.input.Position

/**
  * The exception could be thrown during parsing of .netrc
  * @param pos - the position in the file where the parsing error occurred.
  * @param msg - the description of the error
  * */
case class ParseError(pos: Position, msg: String = "No error") extends Exception {
  override def toString: String = s"at [${pos.line},${pos.column}]: $msg"
}
