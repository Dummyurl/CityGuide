package sk.dmsoft.cityguide.Commons

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

/**
 * Created by Daniel on 27. 2. 2018.
 */

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ImageView.load(url : String, callback: () -> Unit){
    PicassoCache.instance?.load(url)?.networkPolicy(NetworkPolicy.OFFLINE)?.into(this, object: com.squareup.picasso.Callback{
        override fun onSuccess() {
            callback()
        }

        override fun onError() {
            PicassoCache.instance?.load(url)?.fit()?.transform(CropCircleTransformation())?.into(this@load)
            callback()
        }
    })
}

fun ImageView.loadCircle(url: String){
    PicassoCache.instance?.load(url)?.networkPolicy(NetworkPolicy.OFFLINE)?.fit()?.transform(CropCircleTransformation())?.into(this, object: com.squareup.picasso.Callback{
        override fun onSuccess() {}

        override fun onError() {
            PicassoCache.instance?.load(url)?.fit()?.transform(CropCircleTransformation())?.into(this@loadCircle)
        }
    })
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction, addToBackstack: Boolean, sharedTransActionItem: View? = null) {
    val transaction = beginTransaction().func()
    if (addToBackstack)
        transaction.addToBackStack("true")
    if (sharedTransActionItem != null)
        transaction.addSharedElement(sharedTransActionItem, sharedTransActionItem.transitionName)
    transaction.commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, addToBackstack: Boolean = false){
    supportFragmentManager.inTransaction ({ add(frameId, fragment) }, addToBackstack)
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, addToBackstack: Boolean = false, sharedTransActionItem: View? = null) {
    supportFragmentManager.inTransaction({replace(frameId, fragment)}, addToBackstack, sharedTransActionItem)
}