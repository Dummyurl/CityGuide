package sk.dmsoft.cityguide.Api

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Models.*
import sk.dmsoft.cityguide.Models.Account.Login
import sk.dmsoft.cityguide.Models.Account.Registration
import sk.dmsoft.cityguide.Models.Account.Registration1
import sk.dmsoft.cityguide.Models.Account.Registration2
import java.util.concurrent.TimeUnit

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

    fun registration2(model: Registration2): Call<ResponseBody> {
        return api.registration2(model)
    }

    fun getCountries(): Call<ArrayList<Country>> {
        return api.getCountries()
    }

    fun getPlaces(): Call<ArrayList<Place>> {
        return api.getPlaces()
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