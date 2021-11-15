package autobc.elements

import autobc.util.Resources
import org.sikuli.script.Image

open class Element(imagePath: String) {
    var image: Image = Resources.findSikuliImage(imagePath)

    fun getBounds() = arrayOf(image.w, image.h)
}
