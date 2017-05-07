package scalashop

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._

@RunWith(classOf[JUnitRunner])
class BlurSuite extends FunSuite {

  test ("alpha") {
    assert(alpha(0) == 0)
    assert(alpha(1) == 1)
    assert(alpha(255) == 255)
    assert(alpha(256) == 0)
    assert(alpha(257) == 1)
    assert(alpha(6681) == 25)
    assert(alpha(65537) == 1)
  }

  test("blue") {
    assert(blue(0) == 0)
    assert(blue(45) == 0)
    assert(blue(256) == 1)
    assert(blue(300) == 1)
    assert(blue(3000) == 11)
    assert(blue(65536) == 0)

  }
  test("green and red") {
    assert(green(0) == 0)
    assert(red(0) == 0)
  }


  test("boxBlurKernel should correctly handle radius 0") {
    val src = new Img(5, 5)

    for (x <- 0 until 5; y <- 0 until 5)
      src(x, y) = rgba(x, y, x + y, math.abs(x - y))

    for (x <- 0 until 5; y <- 0 until 5)
      assert(boxBlurKernel(src, x, y, 0) === rgba(x, y, x + y, math.abs(x - y)),
        "boxBlurKernel(_,_,0) should be identity.")
  }


  test("boxBlurKernel should correctly handle radius 0 - rewritten") {
    val dim = 3
    val src = new Img(dim, dim)
    for (x <- 0 until dim; y <- 0 until dim)
      src(x, y) = rgba(x, y, x + y, math.abs(x - y))

    for (x <- 0 until dim; y <- 0 until dim)
      assert(boxBlurKernel(src, x, y, 0) === rgba(x, y, x + y, math.abs(x - y)),
        "boxBlurKernel(_,_,0) should be identity.")
  }


  test("boxBlurKernel should correctly handle radius 0 with all equal pixels") {
    val dim = 3
    val src = new Img(dim, dim)
    for (x <- 0 until dim; y <- 0 until dim)
      src(x, y) = rgba(x, y, 7, 1)

    for (x <- 0 until dim; y <- 0 until dim)
      assert(boxBlurKernel(src, x, y, 0) === rgba(x, y, 7, 1),
        "boxBlurKernel(_,_,1) should be identity.")
  }

  test("boxBlurKernel should return the correct value on an interior pixel " +
    "of a 3x4 image with radius 1") {

    val src = new Img(3, 4)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8
    src(0, 3) = 50; src(1, 3) = 11; src(2, 3) = 16

    def check(x: Int, y: Int, expected: Int) = {
      val res = boxBlurKernel(src, x, y, 1)
      assert(res == expected,
        s"(boxBlurKernel(${x}, ${y}, 1) should be ${expected}, " +
          s"but it's ${res})")
    }
    assert(boxBlurKernel(src, 1, 2, 1) === 12,
      s"(boxBlurKernel(1, 2, 1) should be 12, " +
        s"but it's ${boxBlurKernel(src, 1, 2, 1)})")

    check(1, 0, 2)
    check(1, 1, 4)
    check(0, 0, 2)
  }

  test("HorizontalBoxBlur.blur with radius 1 should correctly blur the entire 3x3 image") {
    val w = 3
    val h = 3
    val src = new Img(w, h)
    val dst = new Img(w, h)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8

    HorizontalBoxBlur.blur(src, dst, 0, 2, 1)

    def check(x: Int, y: Int, expected: Int) =
      assert(dst(x, y) == expected,
        s"(destination($x, $y) should be $expected)")

    check(0, 0, 2)
    check(1, 0, 2)
    check(2, 0, 3)
    check(0, 1, 3)
    check(1, 1, 4)
    check(2, 1, 4)
    check(0, 2, 0)
    check(1, 2, 0)
    check(2, 2, 0)
  }

  test("VerticalBoxBlur.blur with radius 2 should correctly blur the entire " +
    "4x3 image") {
    val w = 4
    val h = 3
    val src = new Img(w, h)
    val dst = new Img(w, h)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2; src(3, 0) = 9
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5; src(3, 1) = 10
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8; src(3, 2) = 11

    VerticalBoxBlur.blur(src, dst, 0, 4, 2)

    def check(x: Int, y: Int, expected: Int) =
      assert(dst(x, y) == expected,
        s"(destination($x, $y) should be $expected)")

    check(0, 0, 4)
    check(1, 0, 5)
    check(2, 0, 5)
    check(3, 0, 6)
    check(0, 1, 4)
    check(1, 1, 5)
    check(2, 1, 5)
    check(3, 1, 6)
    check(0, 2, 4)
    check(1, 2, 5)
    check(2, 2, 5)
    check(3, 2, 6)
  }


}
