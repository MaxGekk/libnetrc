package org.libnetrc

import scala.util.parsing.input.Positional

sealed trait Item extends Positional
case class Machine(name: String, login: String, password: String) extends Item
case class Default(login: String, password: String) extends Item

case class NetRc(items: Seq[Item])
