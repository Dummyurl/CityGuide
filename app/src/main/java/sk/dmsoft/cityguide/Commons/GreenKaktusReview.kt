package sk.dmsoft.cityguide.Commons

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import sk.dmsoft.cityguide.R

class GreenKaktusReview : LinearLayout {
    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)


    init {
        orientation = LinearLayout.HORIZONTAL
    }

    fun reload(count: Double){
        if (childCount > 0)
            removeAllViews()
        var kaktusCount = count

        while (kaktusCount > 1.0){
            val kaktusImage = ImageView(context)
            kaktusImage.setImageDrawable(context.getDrawable(R.drawable.logo))
            val lp = LinearLayout.LayoutParams(50, 50)
            lp.marginEnd = 5
            kaktusImage.layoutParams = lp
            this@GreenKaktusReview.addView(kaktusImage)
            kaktusCount -= 1.0
        }

        if (kaktusCount > 0.65){
            val kaktusImage = ImageView(context)
            kaktusImage.setImageDrawable(context.getDrawable(R.drawable.logo))
            val lp = LinearLayout.LayoutParams(50, 50)
            lp.marginEnd = 5
            kaktusImage.layoutParams = lp
            this@GreenKaktusReview.addView(kaktusImage)
        }

        else if (kaktusCount > 0.35){
            val kaktusImage = ImageView(context)
            kaktusImage.setImageDrawable(context.getDrawable(R.drawable.logo_half))
            val lp = LinearLayout.LayoutParams(50, 50)
            lp.marginEnd = 5
            kaktusImage.layoutParams = lp
            this@GreenKaktusReview.addView(kaktusImage)
        }
    }
}