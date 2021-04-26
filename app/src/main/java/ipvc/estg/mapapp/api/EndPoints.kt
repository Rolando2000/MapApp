package ipvc.estg.mapapp.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("/myslim/api/user")
    fun getUsers(): Call<List<User>>

    @GET("/myslim/api/user/{id}")
    fun getUserById(@Path("id") id:Int): Call<User>

    @FormUrlEncoded
    @POST("/myslim/api/user/login")
    fun login(@Field("username") username:String?,
              @Field("password") password:String?): Call<OutputPost>



}