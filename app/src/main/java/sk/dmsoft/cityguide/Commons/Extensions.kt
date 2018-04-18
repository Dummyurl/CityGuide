package sk.dmsoft.cityguide.Commons

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

/**
 * Created by Daniel on 27. 2. 2018.
 */

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ImageView.load(url : String){
    PicassoCache.instance?.load(url)?.into(this)
}

fun ImageView.loadCircle(url: String){
    PicassoCache.instance?.load(url)?.transform(CropCircleTransformation())?.into(this)
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction, addToBackstack: Boolean) {
    if (addToBackstack)
        beginTransaction().func().addToBackStack("true").commit()
    else
        beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, addToBackstack: Boolean = false){
    supportFragmentManager.inTransaction ({ add(frameId, fragment) }, addToBackstack)
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, addToBackstack: Boolean = false) {
    supportFragmentManager.inTransaction({replace(frameId, fragment)}, addToBackstack)
}