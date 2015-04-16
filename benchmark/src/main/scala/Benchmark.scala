package macromagic

import com.google.caliper.Param

import scala.util.Random

object BoxingBenchmark extends BenchmarkRunner(classOf[BoxingBenchmark])
class BoxingBenchmark extends BenchmarkUtils {
//  @Param(Array("2048"))
  @Param(Array("1024"))
  var size: Int = 0

  var ints: Array[Int] = null

  override def setUp() {
    val len = size * size
    ints = init(len)(Random.nextInt)
  }

  def funk(col: Int, row: Int, z: Int): Int = col + row + z

  def timeMacroMapperAnonymous(reps: Int) = run(reps)(macroMapperAnonymous)
  def macroMapperAnonymous = {
    val goal = ints.clone
    val w = new ArrayWrapper(goal)
    val x = w.map(I { (col, row, z) => z + 1 })
//    val x = w.map { (col: Int, row: Int, z: Int) => z + 1 }
//    val x = w.map { (col, row, z) => z + 1 }
    x
  }

  def timeMacroMapperMethod(reps: Int) = run(reps)(macroMapperMethod)
  def macroMapperMethod = {
    val goal = ints.clone
    val w = new ArrayWrapper(goal)
    val x = w.map(funk _)
    x
  }

  def timeFunction1MapToInt(reps: Int) = run(reps)(function1MapToInt)
  def function1MapToInt = {
    val goal = ints.clone
    val w = new ArrayWrapper(goal)
    val x = w.function1MapToInt{ z => z + 1 }
    x
  }

  // def timeFunction1MapToT(reps: Int) = run(reps)(function1MapToT)
  // def function1MapToT = {
  //   val goal = ints.clone
  //   val w = new ArrayWrapper(goal)
  //   val x = w.function1MapToT[Option[Int]] { z => Some(z) }
  //   x
  // }

  def timeFunction3MapToInt(reps: Int) = run(reps)(function3MapToInt)
  def function3MapToInt = {
    val goal = ints.clone
    val w = new ArrayWrapper(goal)
    val x = w.function3MapToInt { (x, y, z) => z + 1 }
    x
  }

  // def timeFunction3MapToT(reps: Int) = run(reps)(function3MapToT)
  // def function3MapToT = {
  //   val goal = ints.clone
  //   val w = new ArrayWrapper(goal)
  //   val x = w.function3MapToT[Option[Int]] { (x, y, z) => Some(z) }
  //   x
  // }

  // def timeTraitMapToInt(reps: Int) = run(reps)(traitMapToInt)
  // def traitMapToInt = {
  //   val goal = ints.clone
  //   val w = new ArrayWrapper(goal)
  //   val x = w.traitMapToInt( 
  //     new IntFunc { 
  //       def apply(x: Int, y: Int, z: Int): Int = z + 1 
  //     }
  //   )
  //   x
  // }

  // def timeTraitMapToT(reps: Int) = run(reps)(traitMapToT)
  // def traitMapToT = {
  //   val goal = ints.clone
  //   val w = new ArrayWrapper(goal)
  //   val x = w.traitMapToT[Option[Int]](
  //     new IntFunc2[Option[Int]] {
  //       def apply(x: Int, y: Int, z:Int): Option[Int] = Some(z)
  //     }
  //   )
  //   x
  // }
}
