package ipvc.estg.mapapp.api

import okhttp3.MultipartBody
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


    @GET("/myslim/api/getMarker")
    fun getMarker(): Call<List<marker>>

    @GET("/myslim/api/marker/{id}")
    fun getMarkerById(@Path("id") id:Int): Call<marker>

    @GET("/myslim/api/markerUser/{idUser}")
    fun getMarkerByIdUser(@Path("idUser") id:Int): Call<List<marker>>

    @FormUrlEncoded
    @POST("/myslim/api/postMarker")
    fun postMarker(@Field("titulo") titulo:String?,
                   @Field("descricao") descricao:String?,
                   @Field("longitude") longitude:Double?,
                   @Field("latitude") latitude:Double?,
                   @Field("imagem") imagem: MultipartBody.Part,
                    @Field("tipoProb") tipoProb:String?,
                   @Field("idUser") idUser: Int?): Call<Outputmarker>

    @POST("/myslim/api/markerPut/{id}")
    fun updateMarker(@Field("titulo") titulo:String?,
                     @Field("descricao") descricao:String?,
                     @Field("longitude") longitude:Double?,
                     @Field("latitude") latitude:Double?,
                     @Field("imagem") imagem:String?,
                     @Field("tipoproblema") tipoproblema:String?):Call<Outputmarker>



}