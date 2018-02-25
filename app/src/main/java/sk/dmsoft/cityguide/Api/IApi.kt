package sk.dmsoft.cityguide.Api


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import sk.dmsoft.cityguide.Models.*
import sk.dmsoft.cityguide.Models.Account.Login
import sk.dmsoft.cityguide.Models.Account.Registration

/**
 * Created by Daniel on 13. 11. 2017.
 */
interface IApi {

    @POST("account/getToken")
    fun login(@Body model: Login): Call<AccessToken>

    @POST("account/register")
    fun registration(@Body model: Registration): Call<AccessToken>

    @GET("countries")
    fun getCountries(): Call<ArrayList<Country>>

    @GET("places")
    fun getPlaces(): Call<ArrayList<Place>>
}