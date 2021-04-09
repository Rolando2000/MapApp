package ipvc.estg.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful){

                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@retrofitLogin, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}