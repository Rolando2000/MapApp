package ipvc.estg.mapapp

import android.content.Context
import com.google.android.gms.maps.model.Marker
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import ipvc.estg.mapapp.adapters.markerAdapter
import ipvc.estg.mapapp.api.EndPoints
import ipvc.estg.mapapp.api.ServiceBuilder
import ipvc.estg.mapapp.api.marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class mapa : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private lateinit var FiltroDist: ExtendedFloatingActionButton
    private lateinit var FiltroTipo: ExtendedFloatingActionButton

    private lateinit var foto: ImageView

    private lateinit var mMap: GoogleMap
    private lateinit var aMarker: List<marker>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        val request = ServiceBuilder.buildService(EndPoints::class.java)


        val idUser = getIntent().getStringExtra("idUser")
        val us = getIntent().getStringExtra("username")
        val id_user: Int = idUser!!.toInt()

        val quinhentos =findViewById<Button>(R.id.quinhentos)
        val mil =findViewById<Button>(R.id.mil)
        val milQuinhentos =findViewById<Button>(R.id.milquinhentos)

        val engarrafamento =findViewById<Button>(R.id.btnEngarrafamento)
        val obra =findViewById<Button>(R.id.btnObras)
        val acidente = findViewById<Button>(R.id.btnAcidente)
        val estradafechada = findViewById<Button>(R.id.btnEstradaFechada)
        val buraco = findViewById<Button>(R.id.btnBuracos)
        val noFiltro = findViewById<Button>(R.id.noFiltro)

        FiltroDist = findViewById(R.id.distFloatBtn)
        FiltroTipo = findViewById(R.id.fabTipoProb)

        quinhentos.visibility = View.GONE
        mil.visibility = View.GONE
        milQuinhentos.visibility = View.GONE
        engarrafamento.visibility = View.GONE
        obra.visibility = View.GONE
        acidente.visibility = View.GONE
        estradafechada.visibility = View.GONE
        buraco.visibility = View.GONE

        var btnDist=false
        var btnTipo=false

        FiltroDist.shrink()
        FiltroTipo.shrink()

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        val imgMenu = findViewById<ImageView>(R.id.imgmenu)

        val navView = findViewById<NavigationView>(R.id.navDrawer)
        navView.itemIconTintList=null
        val headerview = navView.getHeaderView(0)
        val headertxt = headerview.findViewById<TextView>(R.id.menuIntro)
        headertxt.setText(getString(R.string.Bemvindo) + us)

       navView.setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.home ->{

                }
                R.id.Problemas ->{
                    val intent = Intent(this, ipvc.estg.mapapp.Marker::class.java)
                    intent.putExtra("idUser", idUser)
                    startActivity(intent)
                }
                R.id.Notas ->{
                    val intent = Intent(this, Note::class.java)
                    startActivity(intent)
                }
                R.id.logout ->{
                    val sharedPref: SharedPreferences =getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE)

                    val editor: SharedPreferences.Editor= sharedPref.edit()
                    editor.clear()
                    editor.commit()
                    editor.apply()

                    val intent = Intent(this, retrofitLogin::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            // Close the drawer
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        imgMenu.setOnClickListener{
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                var loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                //mMap.addMarker(MarkerOptions().position(loc).title("Marker"))
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))
            }
        }

        // request creation
        createLocationRequest()

        val call = request.getMarker()
        var position: LatLng

        call.enqueue(object : Callback<List<marker>> {
            override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                if(response.isSuccessful) {

                    aMarker = response.body()!!
                    for(marker in aMarker) {
                        if(marker.idUser == id_user){
                            position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())

                            mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_BLUE)).alpha(0.7f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))



                        }
                        else {
                            position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                            mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                            //mMap.setOnInfoWindowClickListener(this)
                            //CustomInfoWindowForGoogleMap(marker)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        FiltroTipo.setOnClickListener(){

            if(!btnTipo){
                engarrafamento.visibility = View.VISIBLE
                obra.visibility = View.VISIBLE
                acidente.visibility = View.VISIBLE
                estradafechada.visibility = View.VISIBLE
                buraco.visibility = View.VISIBLE

                FiltroTipo.extend()
                btnTipo=true
            }
            else{
                engarrafamento.visibility = View.GONE
                obra.visibility = View.GONE
                acidente.visibility = View.GONE
                estradafechada.visibility = View.GONE
                buraco.visibility = View.GONE

                FiltroTipo.shrink()
                btnTipo=false
            }
        }

        FiltroDist.setOnClickListener(){

            if(!btnDist){
                quinhentos.visibility = View.VISIBLE
                mil.visibility = View.VISIBLE
                milQuinhentos.visibility = View.VISIBLE

                FiltroDist.extend()
                btnDist=true
            }
            else{
                quinhentos.visibility = View.GONE
                mil.visibility = View.GONE
                milQuinhentos.visibility = View.GONE

                FiltroDist.shrink()
                btnDist=false
            }
        }

        quinhentos.setOnClickListener(){

            mMap.clear()
            val call = request.getMarker()
            var position: LatLng

            call.enqueue(object : Callback<List<marker>> {
                override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                    if(response.isSuccessful) {

                        aMarker = response.body()!!
                        for(marker in aMarker) {
                            var dist= calculateDistance(marker.latitude.toDouble(), marker.longitude.toDouble(), lastLocation.latitude, lastLocation.longitude)
                            val centro = LatLng(lastLocation.latitude,lastLocation.longitude)
                            mMap.addCircle(
                                    CircleOptions()
                                            .center(centro)
                                            .radius(500.0)
                                            .strokeWidth(3f)
                                            .strokeColor(Color.BLUE)
                                            .fillColor(Color.argb(20,50,50,150))
                            )
                            if(dist <= 500){
                                if(marker.idUser==id_user){
                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)).alpha(1f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                                else {

                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                    Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }
        mil.setOnClickListener(){
            mMap.clear()
            val call = request.getMarker()
            var position: LatLng

            call.enqueue(object : Callback<List<marker>> {
                override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                    if(response.isSuccessful) {

                        aMarker = response.body()!!
                        for(marker in aMarker) {
                            var dist= calculateDistance(marker.latitude.toDouble(), marker.longitude.toDouble(), lastLocation.latitude, lastLocation.longitude)
                            val centro = LatLng(lastLocation.latitude,lastLocation.longitude)
                            mMap.addCircle(
                                    CircleOptions()
                                            .center(centro)
                                            .radius(1000.0)
                                            .strokeWidth(3f)
                                            .strokeColor(Color.BLUE)
                                            .fillColor(Color.argb(20,50,50,150))
                            )
                            if(dist <= 1000){
                                if(marker.idUser==id_user){
                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)).alpha(1f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                                else {

                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                    Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        milQuinhentos.setOnClickListener(){
            mMap.clear()
            val call = request.getMarker()
            var position: LatLng

            call.enqueue(object : Callback<List<marker>> {
                override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                    if(response.isSuccessful) {

                        aMarker = response.body()!!
                        for(marker in aMarker) {
                            var dist= calculateDistance(marker.latitude.toDouble(), marker.longitude.toDouble(), lastLocation.latitude, lastLocation.longitude)
                            val centro = LatLng(lastLocation.latitude,lastLocation.longitude)
                            mMap.addCircle(
                                    CircleOptions()
                                            .center(centro)
                                            .radius(1500.0)
                                            .strokeWidth(3f)
                                            .strokeColor(Color.BLUE)
                                            .fillColor(Color.argb(20,50,50,150))
                            )
                            if(dist <= 1500){
                                if(marker.idUser==id_user){
                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)).alpha(1f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                                else {

                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                    Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        noFiltro.setOnClickListener() {
            mMap.clear()
            val call = request.getMarker()
            var position: LatLng

            call.enqueue(object : Callback<List<marker>> {
                override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                    if(response.isSuccessful) {

                        aMarker = response.body()!!
                        for(marker in aMarker) {

                            if(marker.idUser==id_user){
                                position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_BLUE)).alpha(0.7f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                            }
                            else {

                                position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                    Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        engarrafamento.setOnClickListener(){
            mMap.clear()
            val call = request.getMarker()
            var position: LatLng

            call.enqueue(object : Callback<List<marker>> {
                override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                    if(response.isSuccessful) {

                        aMarker = response.body()!!
                        for(marker in aMarker) {
                            if (marker.tipoProb == "engarrafamento") {
                                if (marker.idUser == id_user) {
                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)).alpha(1f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                } else {

                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                    Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }
        obra.setOnClickListener(){
            mMap.clear()
            val call = request.getMarker()
            var position: LatLng

            call.enqueue(object : Callback<List<marker>> {
                override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                    if(response.isSuccessful) {

                        aMarker = response.body()!!
                        for(marker in aMarker) {
                            if (marker.tipoProb == "obras") {
                                if (marker.idUser == id_user) {
                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)).alpha(1f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                } else {

                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                    Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        acidente.setOnClickListener(){
            mMap.clear()
            val call = request.getMarker()
            var position: LatLng

            call.enqueue(object : Callback<List<marker>> {
                override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                    if(response.isSuccessful) {

                        aMarker = response.body()!!
                        for(marker in aMarker) {
                            if (marker.tipoProb == "acidente") {
                                if (marker.idUser == id_user) {
                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)).alpha(1f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                } else {

                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                    Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        estradafechada.setOnClickListener(){
            mMap.clear()
            val call = request.getMarker()
            var position: LatLng

            call.enqueue(object : Callback<List<marker>> {
                override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                    if(response.isSuccessful) {

                        aMarker = response.body()!!
                        for(marker in aMarker) {
                            if (marker.tipoProb == "estrada fechada") {
                                if (marker.idUser == id_user) {
                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)).alpha(1f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                } else {

                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))

                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                    Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        buraco.setOnClickListener(){
            mMap.clear()
            val call = request.getMarker()
            var position: LatLng

            call.enqueue(object : Callback<List<marker>> {
                override fun onResponse(call: Call<List<marker>>, response: Response<List<marker>>) {
                    if(response.isSuccessful) {

                        aMarker = response.body()!!
                        for(marker in aMarker) {
                            if (marker.tipoProb == "buracos") {
                                if (marker.idUser == id_user) {
                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)).alpha(1f).position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                } else {

                                    position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                                    mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb).snippet(marker.id.toString()))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                    Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.setOnInfoWindowClickListener(this)
        setUpMap()
        //mMap.setOnInfoWindowClickListener { marker: Marker -> onMarkerClick(marker) }


    }

    fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

            return
        } else {
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    Toast.makeText(this@mapa, lastLocation.toString(), Toast.LENGTH_SHORT).show()
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                }
            }
        }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
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

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        // interval specifies the rate at which your app will like to receive updates.
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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

    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        // distance in meter
        return results[0]
    }

    override fun onInfoWindowClick(marker: Marker) {
        val intent = Intent(this, custom_info_window::class.java)
        intent.putExtra("id", marker.snippet)
        startActivity(intent)
    }

}