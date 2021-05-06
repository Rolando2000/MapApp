package ipvc.estg.mapapp.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @Multipart
    @POST("/myslim/api/postMarker")
    fun postMarker(@Part("titulo") titulo: RequestBody,
                   @Part("descricao") descricao: RequestBody,
                   @Part("longitude") longitude: RequestBody,
                   @Part("latitude") latitude: RequestBody,
                   @Part imagem:MultipartBody.Part,
                   @Part("tipoProb") tipoProb: RequestBody,
                   @Part("idUser") idUser: Int?): Call<Outputmarker>

    @POST("/myslim/api/markerPut/{id}")
    fun updateMarker(@Field("titulo") titulo:String?,
                     @Field("descricao") descricao:String?,
                     @Field("longitude") longitude:Double?,
                     @Field("latitude") latitude:Double?,
                     @Field("imagem") imagem:String?,
                     @Field("tipoproblema") tipoproblema:String?):Call<Outputmarker>



}