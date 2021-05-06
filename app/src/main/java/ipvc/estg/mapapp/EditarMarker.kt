package ipvc.estg.mapapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import ipvc.estg.mapapp.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarMarker : AppCompatActivity() {
    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_marker)

        val idM = getIntent().getIntExtra("id", 0)

        val request = ServiceBuilder.buildService(EndPoints::class.java)

        val getM = request.getMarkerById(idM)

        val titE = findViewById<TextView>(R.id.tituloe)
        val descE = findViewById<TextView>(R.id.descricaoe)


        getM.enqueue(object : Callback<marker> {
            override fun onResponse(call: Call<marker>, response: Response<marker>) {
                if (response.isSuccessful) {

                    val aux: marker = response.body()!!

                    titE.text = aux.titulo
                    descE.text = aux.descricao
                }
            }

            override fun onFailure(call: Call<marker>, t: Throwable) {
                Toast.makeText(this@EditarMarker, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })



        val guardar = findViewById<Button>(R.id.guardar1e)

        guardar.setOnClickListener() {

            var ola = "ola"

            val spinner: Spinner = findViewById(R.id.spinner2e)
            // Create an ArrayAdapter using the string array and a default spinner layout


            ola = spinner.getSelectedItem().toString()


            val titulo = findViewById<TextView>(R.id.tituloe)
            val tituloText = titulo.text.toString()

            val descricao = findViewById<TextView>(R.id.descricaoe)
            val descricaoText = descricao.text.toString()


            val call = request.updateMarker(tituloText, descricaoText, ola)

            call.enqueue(object : Callback<EditM> {
                override fun onResponse(call: Call<EditM>, response: Response<EditM>) {
                    if (response.isSuccessful) {

                        val c: EditM = response.body()!!
                        Toast.makeText(this@EditarMarker,c.MSG, Toast.LENGTH_SHORT).show()



                    }
                }

                override fun onFailure(call: Call<EditM>, t: Throwable) {
                    Toast.makeText(this@EditarMarker, "${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }

    }
}