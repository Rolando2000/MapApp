package ipvc.estg.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.mapapp.api.EndPoints
import ipvc.estg.mapapp.api.OutputPost
import ipvc.estg.mapapp.api.ServiceBuilder
import ipvc.estg.mapapp.api.User
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

                            val c: OutputPost = response.body()!!
                            Toast.makeText(this@retrofitLogin,c.MSG,Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<OutputPost>, t: Throwable) {
                        Toast.makeText(this@retrofitLogin, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }
}