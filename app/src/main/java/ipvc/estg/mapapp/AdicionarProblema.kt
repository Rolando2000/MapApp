package ipvc.estg.mapapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentProviderClient
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import ipvc.estg.mapapp.api.EndPoints
import ipvc.estg.mapapp.api.OutputPost
import ipvc.estg.mapapp.api.Outputmarker
import ipvc.estg.mapapp.api.ServiceBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class AdicionarProblema : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_problema)

        val idUser = getIntent().getStringExtra("idUser")
        val id_user: Int = idUser!!.toInt()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val butCorde = findViewById<Button>(R.id.coordenadas)

        butCorde.setOnClickListener() {
            if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            } else {

                fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                    if (location != null) {
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
            post()
            finish()

        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                val loc = LatLng(lastLocation.latitude, lastLocation.longitude)

            }
        }
        createLocationRequest()
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }



    fun post(){
        var ola = "ola"

        val idUser = getIntent().getStringExtra("idUser")
        val id_user: Int = idUser!!.toInt()

        val spinner: Spinner = findViewById(R.id.spinner2)
        // Create an ArrayAdapter using the string array and a default spinner layout

        ola = spinner.getSelectedItem().toString()

        val request = ServiceBuilder.buildService(EndPoints::class.java)

        val titulo = findViewById<TextView>(R.id.titulo)
        val tituloText = titulo.text.toString()

        val imgBitmap: Bitmap = findViewById<ImageView>(R.id.imagemProb).drawable.toBitmap()
        val imageFile: File = convertBitmapToFile("file", imgBitmap)
        val imgFileRequest: RequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val imagem: MultipartBody.Part = MultipartBody.Part.createFormData("image", imageFile.name, imgFileRequest)


        val descricao = findViewById<TextView>(R.id.descricao)
        val descricaoText = descricao.text.toString()

        val longitude = findViewById<TextView>(R.id.longitude)
        val longitudeText = longitude.text.toString()
        val longDou = longitudeText.toDouble()

        val latitude = findViewById<TextView>(R.id.latitude)
        val latitudeText = latitude.text.toString()
        val latDou = latitudeText.toDouble()

        val call = request.postMarker(tituloText, descricaoText, longDou, latDou, imagem, ola, id_user)

        call.enqueue(object : Callback<Outputmarker> {
            override fun onResponse(call: Call<Outputmarker>, response: Response<Outputmarker>) {
                if (response.isSuccessful) {

                    val c: Outputmarker = response.body()!!
                    Toast.makeText(this@AdicionarProblema,c.MSG,Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<Outputmarker>, t: Throwable) {
                Toast.makeText(this@AdicionarProblema, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

     fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

     fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        parent.getItemAtPosition(pos)
        Log.d("SPINNER", pos.toString())

    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("**** cm", "onPause - removeLocationUpdates")
    }

    public override fun onResume() {
        super.onResume()
        startLocationUpdates()
        Log.d("**** cm", "onResume - startLocationUpdates")
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(this@AdicionarProblema.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
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
            val image = findViewById<ImageView>(R.id.imagemProb)

            image.setImageURI(data?.data)
        }
    }




}