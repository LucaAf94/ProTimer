package it.afrune.luca.protimer.customviews.interval

import android.content.Context
import android.widget.LinearLayout

class RunningInterval(context: Context, private val callback: ()-> Unit) : Interval(context, callback)  {

    init {
        binding.apply {
            intervalShowingLayout.visibility = VISIBLE

            intervalButtonBig.isEnabled = true
        }
    }

    fun pause() {
        if (parent is LinearLayout) {
            (parent as LinearLayout).addView(PausedInterval(context, callback))
            (parent as LinearLayout).removeView(this)
        }
    }

    fun stop() {
        if (parent is LinearLayout) {
            (parent as LinearLayout).addView(StoppedInterval(context, callback))
            (parent as LinearLayout).removeView(this)
        }
    }
}