package ipca.candycrush_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.SystemClock
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class GameView  : SurfaceView,Runnable{

    var playing = false
    var gameThread : Thread? = null

    var surfaceHolder : SurfaceHolder? = null
    var canvas : Canvas? = null
    var paint : Paint = Paint()

    var meta = 1900
    var score = 0
    var moves = 10

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
            gameOver()
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

                canvas?.drawText("Moves :${moves}", 0f, 50f, paint)
                canvas?.drawText("Meta :${meta}", 0f, 100f, paint)
                canvas?.drawText("Score :${score}", 0f, 80f, paint)

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

    @RequiresApi(Build.VERSION_CODES.N)
    fun resume(){
        playing = true

        chronometer.isCountDown = true
        chronometer.base= SystemClock.elapsedRealtime() + 300000
        chronometer.start()
        //por margem a 0.00 para o fim

        gameThread = Thread(this)
        gameThread!!.start()
    }

    fun WhenIsGameOver() {

        when(chronometer.stop()) {

            gameOver()
        }
        //chama o gameover quando o tempo chega ao fim, movimentos =0 e quando os movimentos = 0 score < meta

    }




    fun gameOver(){

        playing = false

        canvas = surfaceHolder?.lockCanvas()
        paint.color = Color.BLUE
        canvas?.drawText("Game Over", 100F,50F, paint)

       }


    }
}