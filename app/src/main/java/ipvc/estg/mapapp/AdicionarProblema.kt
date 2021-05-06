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
import android.text.TextUtils
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
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class AdicionarProblema : AppCompatActivity() {

    private lateinit var image: ImageView
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var latP: EditText
    private lateinit var longP: EditText

    private lateinit var location: LatLng

    private val newOcorrActivityRequestCode = 1

    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val pickImage = 100
    private var imageUri: Uri? = null

    private lateinit var button: Button
    private lateinit var buttonBack: Button
    private lateinit var buttonAdd: Button
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_problema)

        val idUser = getIntent().getStringExtra("idUser")
        val id_user: Int = idUser!!.toInt()

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

        title = findViewById(R.id.titulo)
        description = findViewById(R.id.descricao)
        latP = findViewById(R.id.latitude)
        longP = findViewById(R.id.longitude)

        image = findViewById(R.id.imagemProb)

        button = findViewById(R.id.imagem)

        button.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            startActivityForResult(gallery, pickImage)
        }
        buttonAdd = findViewById(R.id.guardar1)
        buttonAdd.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(title.text) && TextUtils.isEmpty(description.text)){
                setResult(Activity.RESULT_CANCELED, replyIntent)
                startActivityForResult(intent, newOcorrActivityRequestCode)
                Toast.makeText(applicationContext, R.string.empty_not_saved, Toast.LENGTH_LONG).show()
            } else {
                post()
                val intent = Intent(this, Marker::class.java)
                intent.putExtra("idUser", id_user)
                startActivity(intent)
                finish()
            }
        }

        spinner = findViewById(R.id.spinner2)
        ArrayAdapter.createFromResource(this@AdicionarProblema, R.array.tipoProb,android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            if (data != null){
                image.setImageURI(data?.data)
            }
        }
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

        val titulo: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), title.text.toString())
        val descicao: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), description.text.toString())
        val type: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), spinner.selectedItem.toString())
        val latitude: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), longP.text.toString())
        val longitude: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), latP.text.toString())
        val tiP: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), ola)

        val imgBitmap: Bitmap = findViewById<ImageView>(R.id.imagemProb).drawable.toBitmap()
        val imageFile: File = convertBitmapToFile("file", imgBitmap)
        val imgFileRequest: RequestBody = RequestBody.create(MediaType.parse("imagem/*"), imageFile)
        val imagem: MultipartBody.Part = MultipartBody.Part.createFormData("imagem", imageFile.name, imgFileRequest)


        val call = request.postMarker(titulo, descicao, latitude, longitude, imagem, tiP, id_user)

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
        Log.d("**** CM", "onPause - removeLocationUpdates")
    }

    public override fun onResume() {
        super.onResume()
        startLocationUpdates()
        Log.d("**** CM", "onResume - startLocationUpdates")
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(this@AdicionarProblema.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
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

}