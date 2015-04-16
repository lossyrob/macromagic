package macromagic

import scala.reflect._

trait IntFunc {
  def apply(x: Int, y: Int, z: Int): Int
}

trait IntFunc2[T] {
  def apply(x: Int, y: Int, z: Int): T
}

class ArrayWrapper(arr: Array[Int]) {
  val size = arr.size

  def map(mapper: IntMapper): Array[Int] = {
    val result = Array.ofDim[Int](size)
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = mapper(x, x, x)
      i += 1
    }
    result
  }


  def function1MapToInt(f: Int => Int): Array[Int] = {
    val result = arr.clone
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = f(x)
      i += 1
    }
    result
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

  def function3MapToInt(f: (Int, Int, Int) => Int): Array[Int] = {
    val result = arr.clone
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = f(x, x, x)
      i += 1
    }
    result
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

  def traitMapToInt(f: IntFunc): Array[Int] = {
    val result = arr.clone
    var i = 0
    while(i < size) {
      val x = arr(i)
      result(i) = f(x, x, x)
      i += 1
    }
    result
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
