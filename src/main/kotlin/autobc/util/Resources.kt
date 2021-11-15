package autobc.util

import autobc.AutoBC
import org.sikuli.script.Image
import java.io.File
import javax.imageio.ImageIO

object Resources {

    fun findSikuliImage(path: String): Image {
        try {
            return Image(ImageIO.read(File(AutoBC::class.java.classLoader.getResource(path)!!.toURI())))
        } catch (e: Exception) {
        }
        return Image(ImageIO.read(File(path)))
    }

    fun findFile(path: String): File {
        try {
            return File(AutoBC::class.java.classLoader.getResource(path)!!.toURI())
        } catch (e: Exception) {
        }
        return File(path)
    }

}