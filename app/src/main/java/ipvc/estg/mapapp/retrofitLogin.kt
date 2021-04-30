package ipvc.estg.mapapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.mapapp.api.*
import ipvc.estg.mapapp.entities.Note
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class retrofitLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit_login)

        val userText = findViewById<TextView>(R.id.user)
        val passText = findViewById<TextView>(R.id.pass)

        val teste=findViewById<TextView>(R.id.teste)

        /*val sharedPref: SharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val soundValue = sharedPref.getBoolean(getString(R.string.sound), false)
        Log.d("****SHAREDPREF", "Read $soundValue")

        if(soundValue) {
            findViewById<CheckBox>(R.id.remember).isChecked = true
        }*/

        val login = findViewById<Button>(R.id.btn_login)
        login.setOnClickListener {

            val request = ServiceBuilder.buildService(EndPoints::class.java)

            val user = userText.text.toString()
            val pass = passText.text.toString()

            teste.text=user

            if(user == "") {
                Toast.makeText(
                        applicationContext,
                        getString(R.string.username_null),
                        Toast.LENGTH_LONG).show()
            }
            else if(pass == "") {
                Toast.makeText(
                        applicationContext,
                        getString(R.string.pass_null),
                        Toast.LENGTH_LONG).show()
            }
            else {
                val call = request.login(user, pass)

                call.enqueue(object : Callback<OutputPost> {
                    override fun onResponse(call: Call<OutputPost>, response: Response<OutputPost>) {
                        if (response.isSuccessful) {

                            /*val sharedPref: SharedPreferences = getSharedPreferences(
                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString()
                            }*/

                            val c: OutputPost = response.body()!!
                            Toast.makeText(this@retrofitLogin,c.MSG,Toast.LENGTH_SHORT).show()
                            markerInicio(c.id)
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<OutputPost>, t: Throwable) {
                        Toast.makeText(this@retrofitLogin, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }

    fun markerInicio(marker: String) {
        val intent = Intent(this, Marker::class.java)
        intent.putExtra("idUser", marker)
        startActivity(intent)
    }

}