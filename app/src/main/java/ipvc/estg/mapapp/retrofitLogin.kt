package ipvc.estg.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.mapapp.api.EndPoints
import ipvc.estg.mapapp.api.ServiceBuilder
import ipvc.estg.mapapp.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class retrofitLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit_login)

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getUsers()

        val userText = findViewById<TextView>(R.id.user)
        val passText = findViewById<TextView>(R.id.pass)

        val teste=findViewById<TextView>(R.id.teste)

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {

                if (response.isSuccessful) {

                    val i: List<User> = response.body()!!

                    var a: Int = i.size

                    val button = findViewById<Button>(R.id.btn_login)
                    button.setOnClickListener {
                        var num: Int = 0
                        var aux: Int = 0
                        var auxPa: Int = 0
                        val user = userText.text.toString()
                        val pass = passText.text.toString()
                        teste.text=user
                        do {
                            if (i[num].user == user) {
                                aux = 1

                                if (i[num].pass == pass) {
                                    auxPa = 1
                                    teste.text="login feito"
                                }
                            }
                            num++;
                        } while (num < a)

                        if(aux == 0) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.WrongUser),
                                Toast.LENGTH_SHORT).show()

                        }
                        if(auxPa == 0) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.wrongPass),
                                Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@retrofitLogin, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}