package ipvc.estg.mapapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipvc.estg.mapapp.adapters.markerAdapter
import ipvc.estg.mapapp.api.EndPoints
import ipvc.estg.mapapp.api.OutputPost
import ipvc.estg.mapapp.api.ServiceBuilder
import ipvc.estg.mapapp.api.marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Marker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker)

        val request = ServiceBuilder.buildService(EndPoints::class.java)

        val idUser = getIntent().getStringExtra("idUser")
        val id_user: Int = idUser!!.toInt()

        val call = request.getMarkerByIdUser(id_user)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclermarker)

        call.enqueue(object : Callback<List<marker>> {
            override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                if(response.isSuccessful) {
                    recyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager (this@Marker)
                        adapter = markerAdapter(response.body()!!)
                    }
                }
            }

            override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                Toast.makeText(this@Marker, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        val add = findViewById<FloatingActionButton>(R.id.fab)

        add.setOnClickListener() {
            markerInicio(idUser)
        }

        val map = findViewById<FloatingActionButton>(R.id.mapa)

        map.setOnClickListener() {
            val intent = Intent(this, mapa::class.java)
            startActivity(intent)
        }
    }

    fun markerInicio(marker: String) {
        val intent = Intent(this, AdicionarProblema::class.java)
        intent.putExtra("idUser", marker)
        startActivity(intent)
    }


}