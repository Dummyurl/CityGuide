package sk.dmsoft.cityguide.Api


import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import sk.dmsoft.cityguide.Models.*
import sk.dmsoft.cityguide.Models.Account.*
import sk.dmsoft.cityguide.Models.Guides.GuideListItem
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Search.SearchInCity
import sk.dmsoft.cityguide.Models.Search.SearchRequest
import sk.dmsoft.cityguide.Models.Search.SearchResluts

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

    @POST("account/register/guide")
    fun registrationGuideInfo(@Body model: RegistrationGuideInfo): Call<ResponseBody>

    @GET("countries")
    fun getCountries(): Call<ArrayList<Country>>

    @GET("places")
    fun getPlaces(): Call<ArrayList<Place>>

    @GET("proposals")
    fun getProposals(): Call<ArrayList<Proposal>>

    @POST("search")
    fun search(@Body model: SearchRequest): Call<SearchResluts>

    @POST("guides")
    fun searchInCity(@Body model: SearchInCity): Call<ArrayList<GuideListItem>>

}