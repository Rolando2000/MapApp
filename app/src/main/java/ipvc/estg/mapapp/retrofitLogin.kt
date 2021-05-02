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

        val sharedPref:SharedPreferences=getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val ischecked = sharedPref.getBoolean(getString(R.string.sound),false)
        Log.d("shared", "Read $ischecked")

        if(ischecked){
            findViewById<CheckBox>(R.id.remember).isChecked=true
            val auxU=sharedPref.getString(getString(R.string.user), "")
            val auxP=sharedPref.getString(getString(R.string.password), "")

            val request = ServiceBuilder.buildService(EndPoints::class.java)

            val call = request.login(auxU, auxP)

            call.enqueue(object : Callback<OutputPost> {
                override fun onResponse(call: Call<OutputPost>, response: Response<OutputPost>) {
                    if (response.isSuccessful) {

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

                val aux = findViewById<CheckBox>(R.id.remember)

                call.enqueue(object : Callback<OutputPost> {
                    override fun onResponse(call: Call<OutputPost>, response: Response<OutputPost>) {
                        if (response.isSuccessful) {

                            if(aux.isChecked) {
                                checkbox(aux)
                            }
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

    fun checkbox(view: View){
        if(view is CheckBox){
            val usernameText = findViewById<TextView>(R.id.user)

            val passwordText = findViewById<TextView>(R.id.pass)

            val sharedPref: SharedPreferences= getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            with (sharedPref.edit()){
                putBoolean(getString(R.string.sound), view.isChecked)
                putString(getString(R.string.user), usernameText.text.toString())
                putString(getString(R.string.password), passwordText.text.toString())
                commit()
            }
        }
    }

}