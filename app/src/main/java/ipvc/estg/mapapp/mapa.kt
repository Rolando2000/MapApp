package ipvc.estg.mapapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import ipvc.estg.mapapp.api.EndPoints
import ipvc.estg.mapapp.api.ServiceBuilder
import ipvc.estg.mapapp.api.marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class   mapa : AppCompatActivity(), OnMapReadyCallback {



    private lateinit var FiltroDist: ExtendedFloatingActionButton
    private lateinit var FiltroTipo: ExtendedFloatingActionButton

    private lateinit var mMap: GoogleMap
    private lateinit var aMarker: List<marker>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        val quinhentos =findViewById<Button>(R.id.quinhentos)
        val mil =findViewById<Button>(R.id.mil)
        val milQuinhentos =findViewById<Button>(R.id.milquinhentos)

        val engarrafamento =findViewById<Button>(R.id.btnEngarrafamento)
        val obra =findViewById<Button>(R.id.btnObras)
        val acidente = findViewById<Button>(R.id.btnAcidente)
        val estradafechada = findViewById<Button>(R.id.btnEstradaFechada)
        val buraco = findViewById<Button>(R.id.btnBuracos)

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



        val request = ServiceBuilder.buildService(EndPoints::class.java)

        val idUser = getIntent().getStringExtra("idUser")
        val us = getIntent().getStringExtra("username")
        val id_user: Int = idUser!!.toInt()

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
                    val intent = Intent(this, Marker::class.java)
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
                                (BitmapDescriptorFactory.HUE_BLUE)).alpha(0.7f).position(position).title(marker.titulo + " - " + marker.tipoProb))
                        }
                        else {
                            position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                            mMap.addMarker(MarkerOptions().position(position).title(marker.titulo + " - " + marker.tipoProb))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<marker>>, t: Throwable) {
                Toast.makeText(this@mapa, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(38.7, -9.7)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}