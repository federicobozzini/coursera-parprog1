package reductions

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory

@RunWith(classOf[JUnitRunner]) 
class LineOfSightSuite extends FunSuite {
  import LineOfSight._
  test("lineOfSight should correctly handle an array of size 4") {
    val output = new Array[Float](4)
    lineOfSight(Array[Float](0f, 1f, 8f, 9f), output)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }


  test("upsweepSequential should correctly handle the chunk 1 until 4 of an array of 4 elements") {
    val res = upsweepSequential(Array[Float](0f, 1f, 8f, 9f), 1, 4)
    assert(res == 4f)
  }


  test("downsweepSequential should correctly handle a 4 element array when the starting angle is zero") {
    val output = new Array[Float](4)
    downsweepSequential(Array[Float](0f, 1f, 8f, 9f), output, 0f, 1, 4)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }

  val terrain = Array[Float](0f, 2f, 6f, 3f, 2, 8, 21, 19, 32)
  val threshold = 3
  val upsweepTree = Node(Node(Leaf(0, 2, 2f), Leaf(2, 4, 3f)), Node(Leaf(4, 6, 1.6f), Leaf(6,9, 4f)))

  test("upsweepSequential 1") {
    val res = upsweepSequential(terrain, 0, 2)
    assert(res == 2f)
  }

  test("upsweepSequential 2") {
    val res = upsweepSequential(terrain, 2, 4)
    assert(res == 3f)
  }

  test("upsweepSequential 3") {
    val res = upsweepSequential(terrain, 4, 6)
    assert(res == 1.6f)
  }

  test("upsweepSequential 4") {
    val res = upsweepSequential(terrain, 6, 9)
    assert(res == 4f)
  }

  test("upsweep") {
    val res = upsweep(terrain, 0, terrain.length, threshold)
    val expected = upsweepTree
    assert(res == expected)
  }

  test("downsweepSequential 1") {
    val output = new Array[Float](terrain.length)
    downsweepSequential(terrain, output, 0, 0, 2)
    assert(output(0) == 0f)
    assert(output(1) == 2f)
  }

  test("downsweepSequential 2") {
    val output = new Array[Float](terrain.length)
    downsweepSequential(terrain, output, 0f, 2, 4)
    assert(output(2) == 3f)
    assert(output(3) == 3f)
  }

  test("downsweepSequential 3") {
    val output = new Array[Float](terrain.length)
    downsweepSequential(terrain, output, 3f, 4, 6)
    assert(output(4) == 3f)
    assert(output(5) == 3f)
  }

  test("downsweepSequential 4") {
    val output = new Array[Float](terrain.length)
    downsweepSequential(terrain, output, 3f, 6, 9)
    assert(output(6) == 3.5f)
    assert(output(7) == 3.5f)
    assert(output(8) == 4f)
  }

  test("downsweep") {
    val output = new Array[Float](terrain.length)
    downsweep(terrain, output, 0, upsweepTree)
    assert(output.toList == List(0f, 2f, 3f, 3f, 3f, 3f, 3.5f, 3.5f, 4f))
  }

  test("parLineOfSight") {
    val t = Array[Float](0.0f, 8.0f, 14.0f, 33.0f, 48.0f)
    val expected = List[Float](0f, 8f, 8f, 11f, 12f)
    val output = new Array[Float](t.length)
    parLineOfSight(t, output, 3)
    assert(output.toList == expected)
  }

}

