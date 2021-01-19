package ipca.candycrush_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet

import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import kotlin.math.abs

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

    var blockSize : Float = 0f

    private val candies = arrayOf(
        R.drawable.bluecandy, R.drawable.greencandy,
        R.drawable.purplecandy, R.drawable.orangecandy, R.drawable.yellowcandy,
        R.drawable.redcandy
    )

    private  fun init(context: Context?, sizeX : Int, sizeY : Int) {
        // Set variables
        surfaceHolder = holder

        blockSize = (sizeX / numberBlocks).toFloat()
        this.sizeX = sizeX
        this.sizeY = sizeY

        // Fill the board
        for(row in 0 until numberBlocks) {
            for (column in 0 until  numberBlocks) {
                // Choose a random candy
                val candy = candies[(Math.random() * candies.size).toInt()]

                // Set a random image
                board.add(Candy(context!!, column, row, candy, blockSize))
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
                        candy.column.toFloat() * candy.width,
                        candy.row.toFloat() * candy.height + sizeY / 3,
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

    /**
     * @param position The position to swap to
     * @param candyIndex the candy index to swap
     */
    private fun canSwap(position: Int, candyIndex: Int) : Boolean {

        var row = board[position].row
        var column = board[position].column
        val candy = board[candyIndex]
        var counter = 0

        // Helper functions
        fun test(index: Int) : Boolean {
            return if(candy.equals(board[index])) {
                counter++
                true
            } else
                false
        }

        fun computeIndex(row: Int, column: Int) : Int {
            return column + row * numberBlocks
        }

        // Test rows
        for(i in 0 until numberBlocks){
            val nextIndex = computeIndex(i, column)

            // Don't test if the same candy
            if (candyIndex != nextIndex) {
                if (test(position)) {
                    if (counter == 3) {
                        return true
                    }
                }
                else
                    break
            }
        }

        // Test columns
        for(i in 0 until numberBlocks ){
            val nextIndex = computeIndex(row, i)

            // Don't test if the same candy
            if (position != nextIndex) {
                if (test(nextIndex)) {
                    if (counter == 3) {
                        return true
                    }
                }
                else
                    break
            }
        }

        Log.d("tag", "$counter")
        return false
    }

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

                val column = (xDown / blockSize).toInt()
                val row = ((yDown - sizeY / 3) / blockSize).toInt()
                val index = column + row * numberBlocks
                Log.d("tag", "${row}, $column, $index, ${board[index].type}")

                if (deltaX > deltaY && deltaX > minDist) {
                    if (xUp > xDown) {
                        Log.d("tag", "Swipe right")
                        if (column < numberBlocks - 1 &&
                            (canSwap(index, index + 1) || canSwap(index + 1, index))) {
                            Log.d("tag", "Can swap")
                        }
                    }
                    else if (xUp < xDown) {
                        Log.d("tag", "Swipe left")
                        if (column > 0 &&
                            (canSwap(index, index - 1) || canSwap(index - 1, index))) {
                            Log.d("tag", "Can swap")
                        }
                    }
                }
                else if (deltaX < deltaY && deltaY > minDist) {
                    if (yUp > yDown) {
                        Log.d("tag", "Swipe Down")
                        if (row < numberBlocks - 1 &&
                            (canSwap(index, index + 1) || canSwap(index + 1, index))) {
                            Log.d("tag", "Can swap")
                        }
                    }
                    else if (yUp < yDown) {
                        Log.d("tag", "Swipe Up")
                        if (column > 0 &&
                            (canSwap(index, index - 1) || canSwap(index - 1, index))) {
                            Log.d("tag", "Can swap")
                        }
                    }
                }
            }
        }
        return true
    }

}

