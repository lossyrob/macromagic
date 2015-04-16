package macromagic

import com.google.caliper.Benchmark
import com.google.caliper.Runner
import com.google.caliper.SimpleBenchmark

import scala.reflect._
import scala.language.experimental.macros

import Macros._

abstract class BenchmarkRunner(cls: java.lang.Class[_ <: Benchmark]) {
  def main(args: Array[String]): Unit = Runner.main(cls, args: _*)
}

abstract class BenchmarkUtils extends SimpleBenchmark {

  /**
    * Sugar for building arrays using a per-cell init function.
    */
  def init[A: Manifest](size: Int)(init: => A) = {
    val data = Array.ofDim[A](size)
    for (i <- 0 until size) data(i) = init
    data
  }

  /**
    * Sugar to run 'f' for 'reps' number of times.
    */
  def run(reps: Int)(f: => Unit) = {
    var i = 0
    while (i < reps) { f; i += 1 }
  }

}
