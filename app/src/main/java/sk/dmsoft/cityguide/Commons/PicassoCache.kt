package sk.dmsoft.cityguide.Commons

import android.content.Context
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso



object PicassoCache {
    var instance: Picasso? = null

    fun CreatePicassoCache(context: Context) {

        //val downloader = OkHttpDownloader(context, Long.MAX_VALUE)
        val builder = Picasso.Builder(context)
        //builder.downloader(downloader)

        instance = builder.build()
    }
}