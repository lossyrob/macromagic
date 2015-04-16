import scala.language.experimental.macros

import macromagic.Macros._

package object macromagic {
  implicit def convertToMapper(f: (Int, Int, Int) => Int): IntMapper = macro intMapper_impl

  def I(f: (Int, Int, Int) => Int): IntMapper = macro intMapper_impl
}
