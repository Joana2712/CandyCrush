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
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class GameView  : SurfaceView, Runnable {

    var playing = false
    var gameThread : Thread? = null

    var surfaceHolder : SurfaceHolder? = null
    var canvas : Canvas? = null
    var paint : Paint = Paint()

    var meta = 1900
    var score = 0
    var moves = 10

    private val candies = arrayOf(
        R.drawable.bluecandy, R.drawable.greencandy,
        R.drawable.purplecandy, R.drawable.orangecandy, R.drawable.yellowcandy,
        R.drawable.redcandy
    )

    private  fun init(sizeX : Int, sizeY : Int) {
        // Set variables
        val board : GridLayout = findViewById(R.id.board)
        val numberBlocks = 10
        val blockSize = sizeX / numberBlocks

        // Create the board
        board.rowCount = numberBlocks
        board.columnCount = numberBlocks
        board.layoutParams.height = sizeX
        board.layoutParams.width = sizeY

        // Fill the board
        for(i in 1..numberBlocks * numberBlocks) {
            // Create the image view
            val image = ImageView(board.context)
            image.id = i
            image.layoutParams.width = blockSize
            image.layoutParams.height = blockSize

            // Set a random image
            image.setImageResource(candies[Math.random().toInt() * candies.size])

            // Add the image to the board
            board.addView(image)
        }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

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

        /*when(chronometer.stop()) {

            gameOver()
        }*/
        //chama o gameover quando o tempo chega ao fim, movimentos =0 e quando os movimentos = 0 score < meta

    }


    fun gameOver(){

        playing = false

        canvas = surfaceHolder?.lockCanvas()
        paint.color = Color.BLUE
        canvas?.drawText("Game Over", 100F,50F, paint)

    }
}