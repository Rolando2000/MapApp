package ipvc.estg.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class custom_info_window : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_info_window)

        val img = getIntent().getStringExtra("img")
        val tit = getIntent().getStringExtra("tit")
        val desc = getIntent().getStringExtra("desc")

        val titV = findViewById<TextView>(R.id.titV)
        val descV = findViewById<TextView>(R.id.descV)
        val imgV = findViewById<ImageView>(R.id.imgV)

        titV.text = tit
        descV.text = desc

        Picasso.with(this@custom_info_window)
            .load("https://mapapp1.000webhostapp.com/myslim/api/uploads/" + img + ".png").into(imgV)

    }
}