package it.afrune.luca.protimer.customviews.interval

import android.content.Context
import android.widget.LinearLayout

class EditableInterval (context: Context, private val callback: ()-> Unit) : Interval(context, callback)  {

    init {
        binding.apply {
            intervalEditingLayout.visibility = VISIBLE

            intervalButtonBig.isEnabled = false
        }
    }

    fun done() {
        saveChanges()

        if (parent is LinearLayout) {
                (parent as LinearLayout).addView(PausedInterval(context, callback))
            (parent as LinearLayout).removeView(this)
        }
    }

    private fun saveChanges() {
        TODO("Not yet implemented")
    }
}