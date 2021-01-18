package ipca.candycrush_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
<<<<<<< HEAD
=======
import android.util.Log
>>>>>>> 20dad345a69f943c5a7a7af6ef885af9abfc2ba5
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.annotation.RequiresApi
<<<<<<< HEAD
import java.util.Collections.swap
=======
import kotlin.math.abs
>>>>>>> 20dad345a69f943c5a7a7af6ef885af9abfc2ba5

class GameView  : SurfaceView, Runnable {

    var playing = true
    var gameThread : Thread? = null

    var surfaceHolder : SurfaceHolder? = null
    var canvas : Canvas? = null
    var paint : Paint = Paint()
    var sizeX : Int = 0
    var sizeY : Int = 0

    val board = ArrayList<Candy>()
    val numberBlocks = 10
    var xDown : Float = 0f
    var xUp : Float = 0f
    var yDown : Float = 0f
    var yUp : Float = 0f

    var meta = 1900
    var score = 0
    var moves = 10

    val numberBlocks = 10
    val blockSize : Float = (sizeX / numberBlocks).toFloat()

    private val candies = arrayOf(
        R.drawable.bluecandy, R.drawable.greencandy,
        R.drawable.purplecandy, R.drawable.orangecandy, R.drawable.yellowcandy,
        R.drawable.redcandy
    )

    private  fun init(context: Context?, sizeX : Int, sizeY : Int) {
        // Set variables
        surfaceHolder = holder
<<<<<<< HEAD

=======
        val blockSize : Float = (sizeX / numberBlocks).toFloat()
>>>>>>> 20dad345a69f943c5a7a7af6ef885af9abfc2ba5
        this.sizeX = sizeX
        this.sizeY = sizeY

        // Fill the board
        for(i in 0 until numberBlocks) {
            for (j in 0 until  numberBlocks) {
                // Choose a random candy
                val candy = candies[(Math.random() * candies.size).toInt()]

                // Set a random image
                board.add(Candy(context!!, i, j, candy, blockSize))
            }
        }
    }

    constructor(context: Context?, sizeX : Int, sizeY : Int) : super(context) {
        init(context, sizeX, sizeY)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, 0, 0)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, 0,0)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init (context, 0, 0)
    }

    override fun run() {
        while (playing) {
            update()
            draw()
            control()
            WhenIsGameOver()
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
                paint.textSize = 30f

                canvas?.drawText("Moves :${moves}", 0f, 50f, paint)
                canvas?.drawText("Meta :${meta}", 0f, 130f, paint)
                canvas?.drawText("Score :${score}", 0f, 90f, paint)

                for (candy in board) {
                    canvas?.drawBitmap(
                        candy.bitmap,
                        candy.row.toFloat() * candy.width,
                        candy.column.toFloat() * candy.height + sizeY / 3,
                        paint)
                }

                surfaceHolder?.unlockCanvasAndPost(canvas)
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

        /*chronometer.isCountDown = true
        chronometer.base= SystemClock.elapsedRealtime() + 300000
        chronometer.start()*/
        //por margem a 0.00 para o fim

        gameThread = Thread(this)
        gameThread!!.start()
    }

    fun WhenIsGameOver() {

        if(score < meta && moves == 0)
            gameOver()
    }


    fun gameOver(){

        playing = false

        canvas = surfaceHolder?.lockCanvas()
        paint.color = Color.BLUE
        canvas?.drawText("Game Over", 100F,50F, paint)

    }

<<<<<<< HEAD
    fun canSwap(index: Int, candy: Candy) {

        var row = board[index].row
        var column = board[index].column
        var counter = 0

        for(i in row - 1 downTo 0){

           if(equals(equalCandies(board[index], board[i]))){

               counter++;
           }

            if (counter == 3){

            }
        }

        for(i in row + 1 until 9){

            if(equals(equalCandies(board[index], board[i]))){

                counter++;
            }

            if (counter == 3){

            }
        }

        for(i in column - 10 downTo 0 ){

            if(equals(equalCandies(board[index], board[i]))){

                counter++;
            }

            if (counter == 3){

            }
        }

        for(i in column + 10 until 9 ){

            if(equals(equalCandies(board[index], board[i]))){

                counter++;
            }

            if (counter == 3){

            }
        }

    }

    fun equalCandies(candy1: Candy, candy2: Candy): Boolean{
      return candy1.drawable == candy2.drawable
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action){
            MotionEvent.ACTION_UP ->{

            }
            MotionEvent.ACTION_DOWN ->{



            }
        }



        return true
    }
}
=======
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action){
            MotionEvent.ACTION_DOWN -> {
                xDown = event.x
                yDown = event.y
            }
            MotionEvent.ACTION_UP -> {
                xUp = event.x
                yUp = event.y

                val deltaX = abs(xUp - xDown)
                val deltaY = abs(yUp - yDown)
                val minDist = 15

                val row = xDown / board[0].width
                val column = (yDown - sizeY / 3) / board[0].height
                val indexFirst = row.toInt() + column.toInt() * numberBlocks
                Log.d("tag", "${row}, $column, $indexFirst")

                if (deltaX > deltaY && deltaX > minDist) {
                    if (xUp > xDown) {
                        Log.d("tag", "Swipe right")
                    }
                    else if (xUp < xDown) {
                        Log.d("tag", "Swipe left")
                    }
                }
                else if (deltaX < deltaY && deltaY > minDist) {
                    if (yUp > yDown) {
                        Log.d("tag", "Swipe Down")
                    }
                    else if (yUp < yDown) {
                        Log.d("tag", "Swipe Up")
                    }
                }
            }
        }
        return true
    }

}
>>>>>>> 20dad345a69f943c5a7a7af6ef885af9abfc2ba5
