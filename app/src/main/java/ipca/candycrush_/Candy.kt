package ipca.candycrush_

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix

class Candy {

    var row : Int = 0
    var column : Int = 0
    val width : Int
    val height : Int
    val drawable : Int
    var bitmap : Bitmap

    constructor(context: Context, posX: Int, posY: Int, candy: Int, scale : Float) {
        row = posX
        column = posY
        drawable = candy
        bitmap = BitmapFactory.decodeResource(context.resources, candy)

        val width : Int = bitmap.width
        val height : Int = bitmap.height
        val scaleWidth = scale / width
        val scaleHeight = scale / height

        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()

        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, width, height, matrix, false
        )
        bitmap.recycle()
        bitmap = resizedBitmap
        this.width = bitmap.width
        this.height = bitmap.height
    }
}