package ipvc.estg.mapapp.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("/user/")
    fun getUsers(): Call<List<User>>

    @GET("/user/{id}")
    fun getUserById(@Path("id") id:Int): Call<User>



}