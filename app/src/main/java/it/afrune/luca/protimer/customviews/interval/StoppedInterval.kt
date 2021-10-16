package it.afrune.luca.protimer.customviews.interval

import android.content.Context
import android.widget.LinearLayout

class StoppedInterval(context: Context, callback: ()-> Unit, time : HMSTime) : Interval(context, callback, time) {

    init {
        binding.apply {
            intervalShowingLayout.visibility = VISIBLE

            intervalButtonBig.isEnabled = false
        }
    }

    fun makeEditable() {
        if (parent is LinearLayout) {
                (parent as LinearLayout).addView(EditableInterval(context, callback))
            (parent as LinearLayout).removeView(this)
        }
    }

    fun start() {
        if (parent is LinearLayout) {
                (parent as LinearLayout).addView(RunningInterval(context, callback))
            (parent as LinearLayout).removeView(this)
        }
    }
}