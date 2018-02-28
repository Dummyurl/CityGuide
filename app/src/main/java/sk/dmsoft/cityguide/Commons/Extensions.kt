package sk.dmsoft.cityguide.Commons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Daniel on 27. 2. 2018.
 */

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}