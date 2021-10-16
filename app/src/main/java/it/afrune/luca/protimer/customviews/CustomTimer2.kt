package it.afrune.luca.protimer.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import it.afrune.luca.protimer.databinding.ViewCustomTimerBinding

class CustomTimer2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val binding : ViewCustomTimerBinding = ViewCustomTimerBinding
        .inflate(LayoutInflater.from(context), this, true)


}