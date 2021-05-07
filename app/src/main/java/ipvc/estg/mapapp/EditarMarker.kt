package ipvc.estg.mapapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
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
        val id_user = getIntent().getIntExtra("idUser", 0)

        val request = ServiceBuilder.buildService(EndPoints::class.java)

        val getM = request.getMarkerById(idM)

        val titE = findViewById<TextView>(R.id.tituloe)
        val descE = findViewById<TextView>(R.id.descricaoe)
        val img = findViewById<ImageView>(R.id.imgPr)


        getM.enqueue(object : Callback<marker> {
            override fun onResponse(call: Call<marker>, response: Response<marker>) {
                if (response.isSuccessful) {

                    val aux: marker = response.body()!!

                    titE.text = aux.titulo
                    descE.text = aux.descricao
                    Picasso.with(this@EditarMarker)
                        .load("https://mapapp1.000webhostapp.com/myslim/api/uploads/" + aux.imagem + ".png").into(img)
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



            if(tituloText == "") {
                Toast.makeText(
                        applicationContext,
                        getString(R.string.TitVazioE),
                        Toast.LENGTH_SHORT).show()

            }
            else if(descricaoText == "") {
                Toast.makeText(
                        applicationContext,
                        getString(R.string.DescVaziaE),
                        Toast.LENGTH_SHORT).show()
            }
            else {
                val call = request.updateMarker(idM, tituloText, descricaoText, ola)

                call.enqueue(object : Callback<EditM> {
                    override fun onResponse(call: Call<EditM>, response: Response<EditM>) {
                        if (response.isSuccessful) {


                            val c: EditM = response.body()!!
                            Toast.makeText(this@EditarMarker,c.MSG, Toast.LENGTH_SHORT).show()
                            markerInicio(id_user.toString())

                        }
                    }

                    override fun onFailure(call: Call<EditM>, t: Throwable) {
                        Toast.makeText(this@EditarMarker, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }

        val Apagar = findViewById<FloatingActionButton>(R.id.delete)
        Apagar.setOnClickListener {
            val AlertaApagar = AlertDialog.Builder(this)
            AlertaApagar.setTitle(getString(R.string.apagar_markerTit))
            AlertaApagar.setMessage(getString(R.string.apagar_marker))
            AlertaApagar.setPositiveButton(getString(R.string.sim)){ dialog: DialogInterface?, which: Int ->
                val call = request.DeleteMarker(idM)

                call.enqueue(object : Callback<Outputmarker> {
                    override fun onResponse(call: Call<Outputmarker>, response: Response<Outputmarker>) {
                        if (response.isSuccessful) {

                            val c: Outputmarker = response.body()!!
                            Toast.makeText(this@EditarMarker,c.MSG, Toast.LENGTH_SHORT).show()
                            markerInicio(id_user.toString())
                        }
                    }

                    override fun onFailure(call: Call<Outputmarker>, t: Throwable) {
                        Toast.makeText(this@EditarMarker, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })

                finish()
            }

            AlertaApagar.setNegativeButton(getString(R.string.nao)){ dialog, id ->
                dialog.dismiss()
            }
            AlertaApagar.show()

        }

    }

    fun markerInicio(user: String) {
        val intent = Intent(this, Marker::class.java)
        intent.putExtra("idUser", user)
        startActivity(intent)
        finish()
    }
}