package autobc.elements

import autobc.util.Resources
import org.sikuli.script.Image

open class Element(imagePath: String) {

    val name: String = imagePath.split("/").last()

    val image: Image by lazy { Resources.findSikuliImage(imagePath) }

    fun getBounds() = arrayOf(image.w, image.h)
}
