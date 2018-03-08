package sk.dmsoft.cityguide.Api


import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import sk.dmsoft.cityguide.Models.*
import sk.dmsoft.cityguide.Models.Account.Login
import sk.dmsoft.cityguide.Models.Account.Registration
import sk.dmsoft.cityguide.Models.Account.Registration1
import sk.dmsoft.cityguide.Models.Account.Registration2
import sk.dmsoft.cityguide.Models.Proposal.Proposal

/**
 * Created by Daniel on 13. 11. 2017.
 */
interface IApi {

    @POST("account/getToken")
    fun login(@Body model: Login): Call<AccessToken>

    @POST("account/register")
    fun registration(@Body model: Registration): Call<AccessToken>

    @POST("account/register/1")
    fun registration1(@Body model: Registration1): Call<ResponseBody>

    @Multipart
    @POST("account/register/2")
    fun registration2(@Part aboutMe: MultipartBody.Part, @Part profilePhoto: MultipartBody.Part): Call<ResponseBody>

    @GET("countries")
    fun getCountries(): Call<ArrayList<Country>>

    @GET("places")
    fun getPlaces(): Call<ArrayList<Place>>

    @GET("proposals")
    fun getProposals(): Call<ArrayList<Proposal>>

}