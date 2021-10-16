package it.afrune.luca.protimer.customviews.interval

import android.content.Context

class PausedInterval(context: Context, private val callback: ()-> Unit) : Interval(context, callback)  {
}