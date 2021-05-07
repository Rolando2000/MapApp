package ipvc.estg.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import ipvc.estg.mapapp.api.EndPoints
import ipvc.estg.mapapp.api.ServiceBuilder
import ipvc.estg.mapapp.api.marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class custom_info_window : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_info_window)

        val idmarker = getIntent().getStringExtra("id")
        val id: Int = idmarker!!.toInt()

        val img = getIntent().getStringExtra("img")
        val tit = getIntent().getStringExtra("tit")
        val desc = getIntent().getStringExtra("desc")

        val titV = findViewById<TextView>(R.id.titV)
        val descV = findViewById<TextView>(R.id.descV)
        val imgV = findViewById<ImageView>(R.id.imgV)
        val tipoProb = findViewById<TextView>(R.id.tpE)

        val request = ServiceBuilder.buildService(EndPoints::class.java)

        val getM = request.getMarkerById(id)


        getM.enqueue(object : Callback<marker> {
            override fun onResponse(call: Call<marker>, response: Response<marker>) {
                if (response.isSuccessful) {

                    val aux: marker = response.body()!!

                    titV.text = aux.titulo
                    descV.text = aux.descricao
                    tipoProb.text = aux.tipoProb


                    Picasso.with(this@custom_info_window)
                        .load("https://mapapp1.000webhostapp.com/myslim/api/uploads/" + aux.imagem + ".png")
                        .into(imgV)
                }
            }

            override fun onFailure(call: Call<marker>, t: Throwable) {
                Toast.makeText(this@custom_info_window, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}