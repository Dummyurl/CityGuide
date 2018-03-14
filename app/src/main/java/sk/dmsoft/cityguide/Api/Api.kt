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
import sk.dmsoft.cityguide.Models.Account.*
import sk.dmsoft.cityguide.Models.Guides.GuideDetails
import sk.dmsoft.cityguide.Models.Guides.GuideListItem
import sk.dmsoft.cityguide.Models.Search.SearchInCity
import sk.dmsoft.cityguide.Models.Search.SearchRequest
import sk.dmsoft.cityguide.Models.Search.SearchResluts


/**
 * Created by Daniel on 13. 11. 2017.
 */

class Api constructor(private val activity : Activity? = null) {

    private val api : IApi
    private val domain = "http://cityguide.dmsoft.sk/"

    init {
        if (activity != null) {
            val accessToken: String = AccountManager.accessToken

            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient().newBuilder()
                    .addInterceptor(AuthenticationInterceptor(accessToken))
                    .addInterceptor(logInterceptor)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(domain +"api/")
                    .addConverterFactory(GsonConverterFactory.create())
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

    fun registration(model: Registration): Call<AccessToken> {
        return api.registration(model)
    }

    fun registration1(model: Registration1): Call<ResponseBody> {
        return api.registration1(model)
    }

    fun registration2(model: Registration2, photoUri: Uri): Call<ResponseBody> {
        val wholeID = DocumentsContract.getDocumentId(photoUri)
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor = activity?.contentResolver?.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null)

        var filePath = ""

        val columnIndex = cursor?.getColumnIndex(column[0])

        if (cursor!!.moveToFirst()) {
            filePath = cursor.getString(columnIndex!!)
        }

        cursor.close()
        val image = File(filePath)

        val imageBody : RequestBody = RequestBody.create(MediaType.parse("image/*"), image)
        val imagePart = MultipartBody.Part.createFormData("profilePhoto", image.name, imageBody)

        val aboutMePart = MultipartBody.Part.createFormData("aboutMe", model.aboutMe)
        return api.registration2(aboutMePart, imagePart)
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

    fun search(model: SearchRequest): Call<SearchResluts> {
        return api.search(model)
    }

    fun searchInCity(model: SearchInCity): Call<ArrayList<GuideListItem>>{
        return api.searchInCity(model)
    }

    fun guideDetails(id: String): Call<GuideDetails> {
        return api.getGuideDetails(id)
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


class AuthenticationInterceptor(private val accessToken: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Log.e("Interceptor", "ACCESS TOKEN bearer $accessToken")
        // Add authorization header with updated authorization value to intercepted request
        val authorisedRequest = originalRequest.newBuilder()
                .header("Authorization", "bearer " +accessToken)
                .build()
        return chain.proceed(authorisedRequest)
    }
}