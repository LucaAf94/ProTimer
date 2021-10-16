package it.afrune.luca.protimer.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import it.afrune.luca.protimer.databinding.ViewCustomAddBinding

class AddInterval @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    constructor(context: Context, onClickListener: OnClickListener) : this(context) {
        binding.addButton.setOnClickListener(onClickListener)
    }

    var binding : ViewCustomAddBinding = ViewCustomAddBinding.inflate(LayoutInflater.from(context), this, true)
}