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
import com.google.firebase.database.FirebaseDatabase
import java.sql.Time
import java.util.*
import kotlin.math.abs

class GameView  : SurfaceView, Runnable {

    var playing = true
    var gameThread : Thread? = null
    lateinit var database : FirebaseDatabase

    var surfaceHolder : SurfaceHolder? = null
    var canvas : Canvas? = null
    var paint : Paint = Paint()
    var sizeX : Int = 0
    var sizeY : Int = 0

    private val candies = arrayOf(
        R.drawable.bluecandy, R.drawable.greencandy,
        R.drawable.purplecandy, R.drawable.orangecandy, R.drawable.yellowcandy,
        R.drawable.redcandy
    )

    private val numberBlocks = 10
    private lateinit var board : Array<Candy>
    var xDown : Float = 0f
    var xUp : Float = 0f
    var yDown : Float = 0f
    var yUp : Float = 0f

    var meta = 1900
    var score = 0
    var moves = 3
    var isGameOver = false

    var blockSize : Float = 0f

    private  fun init(context: Context?, sizeX : Int, sizeY : Int) {
        // Set variables
        surfaceHolder = holder

        database = FirebaseDatabase.getInstance()

        blockSize = (sizeX / numberBlocks).toFloat()
        this.sizeX = sizeX
        this.sizeY = sizeY

        // Fill the board
        board = Array(numberBlocks * numberBlocks) { _ -> randomCandy() }

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
            draw()
            control()
        }
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

                // Draw the candies
                for (row in 0 until numberBlocks) {
                    for (column in 0 until numberBlocks) {
                        val candy = board[computeIndex(row, column)]
                        canvas?.drawBitmap(
                            candy.bitmap,
                            column.toFloat() * candy.width,
                            row.toFloat() * candy.height + sizeY / 3,
                            paint)
                    }
                }

                if (isGameOver) {
                    paint.textSize = 100f
                    paint.color = Color.RED
                    canvas?.drawText("GAME OVER", sizeX * .5f - 300,sizeY * .5f, paint)
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

        gameThread = Thread(this)
        gameThread!!.start()
    }

    private fun checkGameOver() {

        if(score < meta && moves == 0)
            gameOver()
    }


    private fun gameOver(){

        isGameOver = true
        val date = Date()
        database.getReference("Scores").child(date.year.toString())
            .child(date.month.toString())
            .child(date.day.toString())
            .child(date.hours.toString())
            .child(date.minutes.toString())
            .setValue(score)
    }

    /**
     * @param position The position to swap to
     * @param candyIndex the candy index to swap
     */
    private fun canSwap(position: Int, candyIndex: Int) : Boolean {

        var row = position / numberBlocks
        var column = position % numberBlocks
        val candy = board[candyIndex]
        var counter = 1

        // Temporarily swap
        swap(position, candyIndex)

        //test rows
        for(i in row - 1 downTo 0){

            val nextIndex = computeIndex(i, column)

            if (candy.equals(board[nextIndex])) {
                counter++
                if (counter == 3) {
                    swap(position, candyIndex)
                    return true
                }
            }
            else
                break
        }

        //test rows
        for(i in row + 1 until 9){

            val nextIndex = computeIndex(i, column)

            if (candy.equals(board[nextIndex])) {
                counter++
                if (counter == 3) {
                    swap(position, candyIndex)
                    return true
                }
            }
            else
                break
        }

        //test columns
        for(i in column - 1 downTo 0 ){

            val nextIndex = computeIndex(row, i)

            if (candy.equals(board[nextIndex])) {
                counter++
                if (counter == 3) {
                    swap(position, candyIndex)
                    return true
                }
            }
            else
                break
        }

        //test columns
        for(i in column + 1 until 9 ){

            val nextIndex = computeIndex(row, i)

            if (candy.equals(board[nextIndex])) {
                counter++
                if (counter == 3) {
                    swap(position, candyIndex)
                    return true
                }
            }
            else
                break
        }

        swap(position, candyIndex)
        return false
    }

    private fun onSwap(index1: Int, index2: Int){

        swap(index1, index2)

        // First candy
        var row1 = index1 / numberBlocks
        var column1 = index1 % numberBlocks
        val candy = board[index1]

        fun delete(index: Int) {
            if (index - numberBlocks >= 0) {
                board[index] = board[index - numberBlocks]
                delete(index - numberBlocks)
            }
        }

        if (row1 > 0) {
            while (candy.equals(board[index1])) {
                delete(index1)
                // Create a new one
                board[column1] = randomCandy()
                score += 10
            }
        }
        else
            board[column1] = randomCandy()

        // Down first candy
        for(i in row1 + 1 until 9){
            val index = computeIndex(i, column1)

            if (board[index].equals(candy)) {
                delete(index)
                // Create a new one
                board[column1] = randomCandy()
                score += 10
            }
            else
                break
        }

        // Left first candy
        for(i in column1 - 1 downTo 0){
            val index = computeIndex(row1, i)

            if (board[index].equals(candy)) {
                delete(index)
                // Create a new one
                board[i] = randomCandy()
                score += 10
            }
            else
                break
        }

        // Right first candy
        for(i in column1 + 1 until 9){
            val index = computeIndex(row1, i)

            if (board[index].equals(candy)) {
                delete(index)
                // Create a new one
                board[i] = randomCandy()
                score += 10
            }
            else
                break
        }
    }

    private fun computeIndex(row: Int, column: Int) : Int {
        return column + row * numberBlocks
    }

    private fun swap(index1: Int, index2: Int) {
        val temp = board[index1]
        board[index1] = board[index2]
        board[index2] = temp
    }

    private fun randomCandy() : Candy {
        // Choose a random candy
        val candy = candies[(Math.random() * candies.size).toInt()]

        // Set a random image
        return Candy(context!!, candy, blockSize)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (moves <= 0) {
            return true
        }

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
                val minDist = blockSize

                val column = (xDown / blockSize).toInt()
                val row = ((yDown - sizeY / 3) / blockSize).toInt()
                if (row < 0)
                    return true
                val index = computeIndex(row, column)
                var moved = false

                if (deltaX > deltaY && deltaX > minDist) {
                    if (xUp > xDown) {
                        // Right swipe
                        if (column < numberBlocks - 1) {
                            if (canSwap(index, index + 1)) {
                                onSwap(index, index + 1)
                                moved = true
                            }
                            if (canSwap(index + 1, index)) {
                                onSwap(index + 1, index)
                                moved = true
                            }
                        }
                    }
                    else if (xUp < xDown) {
                        // Left swipe
                        if (column > 0) {
                            if (canSwap(index, index - 1)) {
                                onSwap(index, index - 1)
                                moved = true
                            }
                            if (canSwap(index - 1, index)) {
                                onSwap(index - 1, index)
                                moved = true
                            }
                        }
                    }
                }
                else if (deltaX < deltaY && deltaY > minDist) {
                    if (yUp > yDown) {
                        // Down swipe
                        if (row < numberBlocks - 1) {
                            if (canSwap(index, index + numberBlocks)) {
                                onSwap(index, index + numberBlocks)
                                moved = true
                            }
                            if (canSwap(index + numberBlocks, index)) {
                                onSwap(index + numberBlocks, index)
                                moved = true
                            }
                        }
                    }
                    else if (yUp < yDown) {
                        // Up swipe
                        if (column > 0) {
                            if (canSwap(index, index - numberBlocks)) {
                                onSwap(index, index - numberBlocks)
                                moved = true
                            }
                            if (canSwap(index - numberBlocks, index)) {
                                onSwap(index - numberBlocks, index)
                                moved = true
                            }
                        }
                    }
                }

                if (moved) {
                    moves--
                    checkGameOver()
                }
            }
        }
        return true
    }

}

