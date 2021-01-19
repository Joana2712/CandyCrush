package ipca.candycrush_

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Database
        val database = FirebaseDatabase.getInstance()
        val scoresData = database.getReference("Scores")

        val imageButtonPlay = findViewById<ImageButton>(R.id.imageButtonPlayNow)
        imageButtonPlay.setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.imageButtonHighScore).setOnClickListener {
            scoresData.orderByValue()
        }
    }


}