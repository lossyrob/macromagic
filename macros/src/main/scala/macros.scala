package macromagic

import scala.reflect.macros.Context
import spire.macros.InlineUtil

trait Mapper[T] {
  def mapper(mapper: IntMapper): T
}

trait IntMapper {
  def apply(col: Int, row: Int, z: Int): Int
}

object Macros {
  def intMapper_impl2[T](c: Context)(f: c.Expr[(Int, Int, Int) => Int]): c.Expr[T] = {
    import c.universe._
    val self = c.Expr[Mapper[T]](c.prefix.tree)
    val tree = q"""$self.mapper(new IntMapper { def apply(col: Int, row: Int, z: Int): Int = $f(col, row, z) })"""
    new InlineUtil[c.type](c).inlineAndReset[T](tree)
  }

  def intMapper_impl(c: Context)(f: c.Expr[(Int, Int, Int) => Int]): c.Expr[IntMapper] = {
    import c.universe._
    val tree = q"""new IntMapper { def apply(col: Int, row: Int, z: Int): Int = $f(col, row, z) }"""
    new InlineUtil[c.type](c).inlineAndReset[IntMapper](tree)
  }
}
