package autobc.util

import autobc.AutoBC
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs

object ImageFinder {

    fun imageLocation(imagePath: String): Point? {
        val imageToFind = readImage(imagePath) ?: return null
        val printScreen = Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize))
        for (x in 0 until printScreen.width) {
            for (y in 0 until printScreen.height) {
                var invalid = false
                var screenPixelX = x
                var screenPixelY = y
                for (pixelX in 0 until imageToFind.width) {
                    screenPixelY = y
                    for (pixelY in 0 until imageToFind.height) {
                        if (imageToFind.getRGB(pixelX, pixelY) != printScreen.getRGB(screenPixelX, screenPixelY)) {
                            invalid = true
                            break
                        } else {
                            screenPixelY++
                        }
                    }
                    if (invalid) {
                        break
                    } else {
                        screenPixelX++
                    }
                }
                if (!invalid) return Point(screenPixelX - imageToFind.width / 2, screenPixelY - imageToFind.height / 2)
            }
        }
        return null
    }

    fun isOnScreen(imagePath: String): Boolean = imageLocation(imagePath) != null

    private fun compareImages(a: BufferedImage, b: BufferedImage): Boolean {
        if ((a.width != b.width) || (a.height != b.height)) {
            println("Both images should have same dimensions")
        } else {
            var diff = 0
            for (y in 0 until a.height) {
                for (x in 0 until a.width) {
                    val colorA = Color(a.getRGB(x, y), true)
                    val colorB = Color(b.getRGB(x, y), true)
                    val data =
                        abs(colorA.red - colorB.red) + abs(colorA.green - colorB.green) + abs(colorA.blue - colorB.blue)
                    diff += data
                }
            }
            val avg = diff / (a.width * a.height * 3)
            val percentage = (avg / 255) * 100
            return percentage >= 70
        }
        return false
    }

    private fun readImage(path: String): BufferedImage? =
        ImageIO.read(File(AutoBC::class.java.classLoader.getResource(path)!!.toURI()))

}
