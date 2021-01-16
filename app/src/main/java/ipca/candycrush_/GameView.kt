package ipca.candycrush_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi

class GameView  : SurfaceView,Runnable{

    var playing = false
    var gameThread : Thread? = null

    var surfaceHolder : SurfaceHolder? = null
    var canvas : Canvas? = null
    var paint : Paint = Paint()

    val candies = arrayOf(
        R.drawable.bluecandy, R.drawable.greencandy,
        R.drawable.purplecandy, R.drawable.orangecandy, R.drawable.yellowcandy,
        R.drawable.redcandy
    )

    override fun run() {
        while (playing) {
            update()
            draw()
            control()
        }
    }

    private fun update(){

    }

    private fun draw() {
        surfaceHolder?.let {
            if (it.surface.isValid) {
                canvas = surfaceHolder?.lockCanvas()
                canvas?.drawColor(Color.BLACK)

                paint.color = Color.WHITE

                for (candies in candies) {

                }
            }
        }
    }

    private fun control() {
        Thread.sleep(17L)
    }


   fun pause(){
        playing = false
        gameThread?.join()
    }

    fun resume(){
        playing = true
        gameThread = Thread(this)
        gameThread!!.start()
    }
}