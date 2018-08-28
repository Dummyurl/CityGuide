package sk.dmsoft.cityguide.Api

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Models.*
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import java.io.File
import java.util.concurrent.TimeUnit
import android.provider.MediaStore
import android.provider.DocumentsContract
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Models.Account.*
import sk.dmsoft.cityguide.Models.Guides.GuideDetails
import sk.dmsoft.cityguide.Models.Guides.GuideListItem
import sk.dmsoft.cityguide.Models.Proposal.MeetingPoint
import sk.dmsoft.cityguide.Models.Proposal.ProposalRequest
import sk.dmsoft.cityguide.Models.Search.SearchInCity
import sk.dmsoft.cityguide.Models.Search.SearchRequest
import sk.dmsoft.cityguide.Models.Search.SearchResluts
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import sk.dmsoft.cityguide.Models.Proposal.CompletedProposal


/**
 * Created by Daniel on 13. 11. 2017.
 */

class Api constructor(private val activity : Context? = null) {

    private val api : IApi

    init {
        if (activity != null) {

            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val gson = GsonBuilder()
                    .setLenient()
                    .create()

            val client = OkHttpClient().newBuilder()
                    .addInterceptor(AuthenticationInterceptor())
                    .addInterceptor(logInterceptor)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(AppSettings.apiUrl +"api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build()

            api = retrofit.create(IApi::class.java)
        }
        else {
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://haukis-001-site6.etempurl.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            api = retrofit.create(IApi::class.java)
        }
    }

    fun login(model: Login): Call<AccessToken> {
        return api.login(model)
    }

    fun logout(): Call<ResponseBody>{
        return api.logout()
    }

    fun registration(model: Registration): Call<AccessToken> {
        return api.registration(model)
    }

    fun registration1(model: Registration1): Call<ResponseBody> {
        return api.registration1(model)
    }

    fun registration2(model: Registration2, photoUri: Uri): Call<ResponseBody> {
        var imagePart: MultipartBody.Part? = null
        if (photoUri.path.length > 5) {
            val image = File(photoUri.path)

            val imageBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), image)
            imagePart = MultipartBody.Part.createFormData("profilePhoto", image.name, imageBody)
        }

        val aboutMePart = MultipartBody.Part.createFormData("aboutMe", model.aboutMe)
        return api.registration2(aboutMePart, imagePart)
    }

    fun registration3(model: Registration3): Call<ResponseBody>{
        return api.registration3(model)
    }

    fun setAccountType(model: SetAccountTypeModel): Call<ResponseBody>{
        return api.setAccountType(model)
    }

    fun init(): Call<InitResponse>{
        return api.init()
    }

    fun registrationGuideInfo(model: RegistrationGuideInfo): Call<ResponseBody>{
        return api.registrationGuideInfo(model)
    }

    fun getCountries(): Call<ArrayList<Country>> {
        return api.getCountries()
    }

    fun getPlaces(): Call<ArrayList<Place>> {
        return api.getPlaces()
    }

    fun getProposals(): Call<ArrayList<Proposal>> {
        return api.getProposals()
    }

    fun getCompletedProposals(page: Int): Call<ArrayList<CompletedProposal>>{
        return api.getCompletedProposals((page))
    }

    fun getSpecifiedCompletedProposals(page: Int, request: StatsRequest): Call<ArrayList<CompletedProposal>>{
        return api.getSpecifiedCompletedProposals(page, request)
    }

    fun getUnconfirmedProposals(): Call<ArrayList<Proposal>> {
        return api.getUnconfirmedProposals()
    }

    fun getProposal(id: Int): Call<Proposal>{
        return api.getProposal(id)
    }

    fun editProposal(id: Int, model: ProposalRequest): Call<ResponseBody>{
        return api.editProposal(id, model)
    }

    fun confirmProposal(model: Proposal): Call<ResponseBody>{
        return api.confirmProposal(model.id)
    }

    fun search(model: SearchRequest): Call<SearchResluts> {
        return api.search(model)
    }

    fun searchInCity(model: SearchInCity): Call<ArrayList<GuideListItem>>{
        return api.searchInCity(model)
    }

    fun guideDetails(id: String): Call<GuideDetails> {
        return api.getGuideDetails(id)
    }

    fun facebookRegister(model: RegisterExternal): Call<AccessToken>{
        return api.facebookRegister(model)
    }

    fun registerFcm(model: RegisterFcm): Call<ResponseBody>{
        return api.registerFcm(model)
    }

    fun createProposal(model: ProposalRequest): Call<ResponseBody> {
        return api.createProposal(model)
    }

    fun deleteProposal(id: Int): Call<ResponseBody>{
        return api.deleteProposal(id)
    }

    fun setMeetingPoint(id: Int, meetingPoint: MeetingPoint): Call<ResponseBody>{
        return api.setMeetingPoint(id, meetingPoint)
    }

    fun getMeetingPoint(id: Int): Call<MeetingPoint>{
        return api.getMeetigPoint(id)
    }

    fun startProposal(id: Int): Call<ResponseBody>{
        return api.startProposal(id)
    }

    fun endProposal(id: Int): Call<Proposal>{
        return api.endProposal(id)
    }

    fun getCheckoutToken(): Call<CheckoutToken>{
        return api.getCheckoutToken()
    }

    fun createTransaction(model: TransactionRequest): Call<ResponseBody>{
        return api.createTransaction(model)
    }

    fun getInterests(): Call<ArrayList<Interest>>{
        return api.getInterests()
    }

    fun savePaymentMethod(model: CreatePaymentMethodRequest): Call<ResponseBody>{
        return api.savePaymentMethod(model)
    }

    fun forgottenPassword(model: ForgottenPassword): Call<PasswordResetCode>{
        return api.forgottenPassword(model)
    }

    fun resetPassword(model: PasswordResetModel): Call<ResponseBody>{
        return api.resetPassword(model)
    }

    fun changePassword(model: UpdatePassword): Call<ResponseBody>{
        return api.changePassword(model)
    }

    fun getStats(): Call<Stats>{
        return api.getStats()
    }

    fun getSpecifiedStats(model: StatsRequest): Call<Stats>{
        return api.getSpecifiedStats(model)
    }

    //fun uploadImages(image: NoteImage) : Call<ResponseBody> {
    //    val file : RequestBody = RequestBody.create(MediaType.parse("image/*"), image.image)
    //    val imagePart = MultipartBody.Part.createFormData("image", image.image?.name, file)
    //    val descPart = MultipartBody.Part.createFormData("desc", image.desc)
    //    val noteIdPart = MultipartBody.Part.createFormData("noteId", image.noteId)
    //    return api.uploadImage(noteIdPart, imagePart, descPart)
    //}


    fun isOnline(): Boolean {
        val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}


class AuthenticationInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Log.e("Interceptor", "ACCESS TOKEN bearer ${AccountManager.accessToken}")
        // Add authorization header with updated authorization value to intercepted request
        val authorisedRequest = originalRequest.newBuilder()
                .header("Authorization", "bearer " +AccountManager.accessToken)
                .build()
        return chain.proceed(authorisedRequest)
    }
}