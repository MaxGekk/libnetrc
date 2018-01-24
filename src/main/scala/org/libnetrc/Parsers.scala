package org.libnetrc

import scala.util.parsing.combinator.{JavaTokenParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

// Parser-combinators for the .netrc syntax
trait Parsers extends RegexParsers with JavaTokenParsers {
  def value = "" ~> // handle whitespace
    rep1(elem("name part", !Character.isSpace(_: Char))) ^^ (_.mkString)

  def machineValue = value
  def loginValue = value
  def passwordValue = value
  def accountPassword = value

  def machine: Parser[Machine] = positioned {
    "machine" ~>  machineValue ~ "login" ~ loginValue ~ "password" ~ passwordValue ~ (("account" ~> accountPassword)?) ^^ {
    case (machineName ~ _ ~ loginName ~ _ ~ password ~ account) => Machine(machineName, loginName, password, account)
  }}

  def default: Parser[Default] = positioned {
    "default" ~>  "login" ~ loginValue ~ "password" ~ passwordValue ~ (("account" ~> accountPassword)?)  ^^ {
      case (_ ~ loginName ~ _ ~ password ~ account) => Default(loginName, password, account)
  }}

  def item: Parser[Item] = positioned(machine | default | "" ~> failure("expected items: machine"))
  // Netrc is just repeated items
  def netrc = rep(item) ^^ {NetRc(_)}
}

object Parsers extends Parsers {
  def parse(s: CharSequence): Either[org.libnetrc.Error, NetRc] = {
    parse(new CharSequenceReader(s)) match {
      case Success(res, _) => Right(res)
      case NoSuccess(msg, next) =>
        Left(org.libnetrc.Error(pos = next.pos, msg))
    }
  }
  def parse(input: CharSequenceReader): ParseResult[NetRc] = parsePhrase(input)

  def parsePhrase(input: CharSequenceReader): ParseResult[NetRc] = {
    phrase(netrc)(input)
  }
}
