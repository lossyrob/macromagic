package macromagic

import macromagic.Macros._

import scala.reflect._
import scala.language.experimental.macros


trait IntFunc {
  def apply(x: Int, y: Int, z: Int): Int
}

trait IntFunc2[T] {
  def apply(x: Int, y: Int, z: Int): T
}

class ArrayWrapper(arr: Array[Int]) extends Mapper[ArrayWrapper]{
  val size = arr.size

  def map(f: (Int, Int, Int) => Int): ArrayWrapper = 
    macro intMapper_impl2[ArrayWrapper]

  def mapper(mapper: IntMapper): ArrayWrapper = {
    val result = Array.ofDim[Int](size)
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = mapper(x, x, x)
      i += 1
    }
    new ArrayWrapper(result)
  }


  def function1MapToInt(f: Int => Int): ArrayWrapper = {
    val result = arr.clone
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = f(x)
      i += 1
    }
    new ArrayWrapper(result)
  }

  def function1MapToT[T: ClassTag](f: Int => T): Array[T] = {
    val result = Array.ofDim[T](size)
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = f(x)
      i += 1
    }
    result
  }

  def function3MapToInt(f: (Int, Int, Int) => Int): ArrayWrapper = {
    val result = arr.clone
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = f(x, x, x)
      i += 1
    }
    new ArrayWrapper(result)
  }

  def function3MapToT[T: ClassTag](f: (Int, Int, Int) => T): Array[T] = {
    val result = Array.ofDim[T](size)
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = f(x, x, x)
      i += 1
    }
    result
  }

  def traitMapToInt(f: IntFunc): ArrayWrapper = {
    val result = arr.clone
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = f(x, x, x)
      i += 1
    }
    new ArrayWrapper(result)
  }

  def traitMapToT[T: ClassTag](f: IntFunc2[T]): Array[T] = {
    val result = Array.ofDim[T](size)
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = f(x, x, x)
      i += 1
    }

    result
  }

}
