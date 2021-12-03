package autobc.util

import java.awt.Point
import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage

object ScreenUtil {

    fun printScreen(screenRect: Rectangle): BufferedImage = Robot().createScreenCapture(screenRect)!!

    fun BufferedImage.findDifference(image: BufferedImage): Point? {
        assert(width == image.width && height == image.height) { "Images Must Be The Same Size" }

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (getRGB(x, y) != image.getRGB(x, y)) {
                    return Point(x, y)
                }
            }
        }
        return null
    }

    fun BufferedImage.findDifferencePercentage(image: BufferedImage): Double {
        assert(width == image.width && height == image.height) { "Images Must Be The Same Size" }

        val pixels = width * height
        var notMatchPixels = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (getRGB(x, y) != image.getRGB(x, y)) {
                    notMatchPixels++
                }
            }
        }

        return (notMatchPixels * 100.0) / pixels
    }

}