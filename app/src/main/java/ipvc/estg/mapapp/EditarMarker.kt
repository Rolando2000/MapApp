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
import ipvc.estg.mapapp.api.EndPoints
import ipvc.estg.mapapp.api.Outputmarker
import ipvc.estg.mapapp.api.ServiceBuilder
import ipvc.estg.mapapp.api.marker
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

        val titE = findViewById<TextView>(R.id.titulo)
        val descE = findViewById<TextView>(R.id.descricao)
        val longE = findViewById<TextView>(R.id.longitude)
        val latE = findViewById<TextView>(R.id.latitude)


        getM.enqueue(object : Callback<marker> {
            override fun onResponse(call: Call<marker>, response: Response<marker>) {
                if (response.isSuccessful) {

                    val aux: marker = response.body()!!

                    titE.text = aux.titulo
                    descE.text = aux.descricao
                    longE.text = aux.longitude
                    latE.text = aux.latitude
                }
            }

            override fun onFailure(call: Call<marker>, t: Throwable) {
                Toast.makeText(this@EditarMarker, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val butCorde = findViewById<Button>(R.id.coordenadas)

        butCorde.setOnClickListener() {
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            } else {

                fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                    if(location != null) {
                        lastLocation = location

                        val lat = findViewById<TextView>(R.id.latitude)
                        val long = findViewById<TextView>(R.id.longitude)

                        lat.text = location.latitude.toString()
                        long.text = location.longitude.toString()
                    }
                }
            }
        }

        val imageButton = findViewById<Button>(R.id.imagem)
        imageButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    pickImageFromGallery();
                }
            }else {
                pickImageFromGallery()
            }
        }

        val guardar = findViewById<Button>(R.id.guardar1)

        guardar.setOnClickListener() {

            var ola = "ola"

            val spinner: Spinner = findViewById(R.id.spinner2)
            // Create an ArrayAdapter using the string array and a default spinner layout


            ola = spinner.getSelectedItem().toString()


            val titulo = findViewById<TextView>(R.id.titulo)
            val tituloText = titulo.text.toString()

            val descricao = findViewById<TextView>(R.id.descricao)
            val descricaoText = descricao.text.toString()

            val longitude = findViewById<TextView>(R.id.longitude)
            val longitudeText = longitude.text.toString()
            val longDou = longitudeText.toDouble()

            val latitude = findViewById<TextView>(R.id.latitude)
            val latitudeText = latitude.text.toString()
            val latDou = latitudeText.toDouble()

            val imagem = "teste"

            val call = request.updateMarker(tituloText, descricaoText, longDou, latDou, imagem, ola)

            call.enqueue(object : Callback<Outputmarker> {
                override fun onResponse(call: Call<Outputmarker>, response: Response<Outputmarker>) {
                    if (response.isSuccessful) {

                        val c: Outputmarker = response.body()!!
                        Toast.makeText(this@EditarMarker,c.MSG, Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<Outputmarker>, t: Throwable) {
                    Toast.makeText(this@EditarMarker, "${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }

    }
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object{
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val image = findViewById<ImageView>(R.id.imageView)

            image.setImageURI(data?.data)
        }
    }
}