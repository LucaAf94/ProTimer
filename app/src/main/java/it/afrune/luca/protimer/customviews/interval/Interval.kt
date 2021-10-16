package it.afrune.luca.protimer.customviews.interval

import android.content.*
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.afrune.luca.protimer.databinding.ViewCustomIntervalBinding

abstract class Interval @JvmOverloads constructor(
    context: Context,
    protected val callback: () -> Unit,
    time: HMSTime
) : ConstraintLayout(context, null, 0) {

    protected val binding: ViewCustomIntervalBinding =
        ViewCustomIntervalBinding.inflate(LayoutInflater.from(context), this, true)

    //region HMSTime
    class HMSTime(var hours: Int, var minutes: Int, var seconds: Int) {
        operator fun dec(): HMSTime {
            if (--seconds < 0) {
                seconds = 59
                if (--minutes < 0) {
                    minutes = 59
                    if (--hours < 0) {
                        hours = 0
                        minutes = 0
                        seconds = 0
                    }
                }
            }
            return this
        }

        val totalSeconds: Long get() = (hours * 60 * 60 + minutes * 60 + seconds).toLong()
    }

    protected val selectedTime: MutableLiveData<HMSTime> = MutableLiveData(time)
    protected val currentTime: MutableLiveData<HMSTime> = MutableLiveData(time)
    //endregion

    abstract class CountDownReceiver : BroadcastReceiver() {

        companion object {
            const val ACTION_TICK = "it.afrune.luca.protimer.CD_TICK"
            const val ACTION_FINISH = "it.afrune.luca.protimer.CD_FINISH"
        }

        override fun onReceive(context: Context, intent: Intent) {
            intent.action?.let { Log.d("PROVA_SERVICE", it) }
            when (intent.action) {
                ACTION_TICK -> onTick()
                ACTION_FINISH -> onFinish()
            }
        }

        abstract var onFinish: () -> Unit

        abstract var onTick: () -> Unit
    }

    private lateinit var countDownReceiver: CountDownReceiver

    init {
        binding.apply {
            intervalNumberPickerSeconds.maxValue = 59
            intervalNumberPickerSeconds.minValue = 0

            intervalNumberPickerMinutes.maxValue = 59
            intervalNumberPickerMinutes.minValue = 0

            intervalNumberPickerHours.maxValue = 99
            intervalNumberPickerHours.minValue = 0
        }

        context.lifecycleOwner?.let {
            currentTime.observe(it, {
                updateTimerUI(it)
            })
        }
    }

    private fun updateTimerUI(time: HMSTime) {
        binding.intervalHours.text = time.hours.toString()
        binding.intervalMinutes.text = time.minutes.toString()
        binding.intervalSeconds.text = time.seconds.toString()
    }
}

val Context.lifecycleOwner: LifecycleOwner?
    get() {
        var context: Context? = this

        while (context != null && context !is LifecycleOwner) {
            val baseContext = (context as? ContextWrapper?)?.baseContext
            context = if (baseContext == context) null else baseContext
        }

        return if (context is LifecycleOwner) context else null
    }

