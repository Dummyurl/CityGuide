package sk.dmsoft.cityguide.Api


import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import sk.dmsoft.cityguide.Models.*
import sk.dmsoft.cityguide.Models.Account.*
import sk.dmsoft.cityguide.Models.Guides.GuideDetails
import sk.dmsoft.cityguide.Models.Guides.GuideListItem
import sk.dmsoft.cityguide.Models.Proposal.MeetingPoint
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Proposal.ProposalRequest
import sk.dmsoft.cityguide.Models.Search.SearchInCity
import sk.dmsoft.cityguide.Models.Search.SearchRequest
import sk.dmsoft.cityguide.Models.Search.SearchResluts

/**
 * Created by Daniel on 13. 11. 2017.
 */
interface IApi {

    @GET("init")
    fun init(): Call<InitResponse>

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

    @GET("proposals/{id}")
    fun getProposal(@Path("id") id: Int): Call<Proposal>

    @GET("proposals/unconfirmed")
    fun getUnconfirmedProposals(): Call<ArrayList<Proposal>>

    @POST("proposals")
    fun createProposal(@Body model: ProposalRequest): Call<ResponseBody>

    @PUT("proposals/{id}")
    fun editProposal(@Path("id") id: Int, @Body model: ProposalRequest): Call<ResponseBody>

    @PUT("proposals/confirm/{id}")
    fun confirmProposal(@Path("id") id: Int): Call<ResponseBody>

    @DELETE("proposals/{id}")
    fun deleteProposal(@Path("id") id: Int): Call<ResponseBody>

    @POST("proposals/start/{id}")
    fun startProposal(@Path("id") id: Int): Call<ResponseBody>

    @POST("proposals/end/{id}")
    fun endProposal(@Path("id") id: Int): Call<Proposal>

    @POST("proposals/meetingpoint/{id}")
    fun setMeetingPoint(@Path("id") id:Int, @Body meetingPoint: MeetingPoint): Call<ResponseBody>

    @GET("proposals/meetingppoint/{id}")
    fun getMeetigPoint(@Path("id") id: Int): Call<MeetingPoint>

    @POST("search")
    fun search(@Body model: SearchRequest): Call<SearchResluts>

    @POST("guides")
    fun searchInCity(@Body model: SearchInCity): Call<ArrayList<GuideListItem>>

    @GET("guides/{id}")
    fun getGuideDetails(@Path("id") id: String): Call<GuideDetails>

    @POST("account/register/fcm")
    fun registerFcm(@Body model: RegisterFcm): Call<ResponseBody>

    @GET("checkout/token")
    fun getCheckoutToken(): Call<CheckoutToken>

    @POST("checkout/transaction")
    fun createTransaction(@Body model: TransactionRequest): Call<ResponseBody>
}