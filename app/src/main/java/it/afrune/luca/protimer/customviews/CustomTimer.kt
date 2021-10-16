package it.afrune.luca.protimer.customviews

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.widget.doOnTextChanged
import it.afrune.luca.protimer.R
import it.afrune.luca.protimer.customviews.customtypes.RepeatStatus
import it.afrune.luca.protimer.customviews.interval.Interval
import it.afrune.luca.protimer.customviews.interval.StoppedInterval
import it.afrune.luca.protimer.databinding.ViewCustomTimerBinding
import it.afrune.luca.protimer.focusAndShowKeyboard

class CustomTimer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var binding: ViewCustomTimerBinding? = null

    private var isTimerEditable = false
    private var repeatManager = RepeatManager()

    init {
        binding = ViewCustomTimerBinding.inflate(LayoutInflater.from(context), this, true)

        binding?.apply {
            if (timerIntervalContainer.children.toList()
                    .isEmpty()
            ) addIntervalCreatorView()     //se la lista è vuota, aggiungi la viw per creare intervalli

            timerButtonEdit.setOnClickListener {
                isTimerEditable =
                    !isTimerEditable            //se cliccato quando non è editabile lo rende tale, e viceversa
                makeEditable(isTimerEditable)
            }

            timerButtonLeft.isEnabled = false
            timerButtonRight.isEnabled = false

            timerRepeatText.apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                doOnTextChanged { text, start, before, count ->
                    repeatManager.setFromEditable(text.toString())
                }
            }
            timerIconRepeat.setOnClickListener {
                repeatManager.cycleStatus()
            }

            makeEditable(isTimerEditable)
        }
    }

    private fun makeEditable(editable: Boolean) {
        isTimerEditable = editable

        binding?.apply {
            //cicla su tutti gli interval e rendili editabili o meno
            timerIntervalContainer.forEach { if (it is StoppedInterval) it.makeEditable() }

            //rimuovi eventuali add view, ma se è editabile, riaggiungine una
            //(questo fa si che non ce ne siano due se si preme pulsante mentre lista intervalli è vuota)
            //(ma aggiungine una anche se ho finito di editare ma la lista è ancora vuota)
            removeAddView()
            if (isTimerEditable || timerIntervalContainer.children.toList()
                    .isEmpty()
            ) addIntervalCreatorView()

            //disabilita o cambia le icone
            if (isTimerEditable) {
                timerButtonEdit.isEnabled = false
                timerButtonSettings.isEnabled = false
                timerButtonLeft.isEnabled = true
                timerButtonMiddle.isEnabled = true
                timerButtonRight.isEnabled = true

                timerButtonMiddle.setImageResource(R.drawable.outline_done_24)
                timerButtonLeft.setImageResource(R.drawable.outline_undo_24)
                timerButtonRight.setImageResource(R.drawable.outline_delete_outline_24)

                timerTitle.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                timerButtonEdit.isEnabled = true
                timerButtonSettings.isEnabled = true
                timerButtonLeft.isEnabled =
                    false        //uscendo da modalità modifica il timer sarà sempre fermo
                timerButtonMiddle.isEnabled =
                    !(timerIntervalContainer.children.toList() as List<ConstraintLayout>)
                        .filterIsInstance<Interval>()    //se non ci sono intervalli nella lista, play è disabilitato
                        .isNullOrEmpty()
                timerButtonRight.isEnabled = false

                timerButtonMiddle.setImageResource(R.drawable.outline_play_arrow_24)
                timerButtonLeft.setImageResource(R.drawable.outline_stop_24)
                timerButtonRight.setImageResource(R.drawable.outline_pause_24)

                timerTitle.inputType = InputType.TYPE_NULL
            }

            repeatManager.updateView()

            //cambia i click
            setButtonClickListeners()
        }
    }

    private fun setButtonClickListeners() {
        binding?.apply {
            if (isTimerEditable) {
                timerButtonLeft.setOnClickListener { undo() }
                timerButtonMiddle.setOnClickListener { done() }
                timerButtonRight.setOnClickListener { delete() }
            } else {
                timerButtonLeft.setOnClickListener { stop() }
                timerButtonMiddle.setOnClickListener { start() }
                timerButtonRight.setOnClickListener { pause() }
            }
        }
    }

    private fun pause() {
        binding?.apply {
//            (timerIntervalContainer.children.toList() as List<Interval>)     //trova l'intervallo che è attivo
//                .find { it.countDownState == Interval.CountDownStatus.TICKING }
//                ?.pauseCountDown()

            timerButtonMiddle.isEnabled = true
            timerButtonRight.isEnabled = false
            timerButtonEdit.isEnabled = true
        }
    }

    private fun start() {
        binding?.apply {
//            val intervals =
//                (timerIntervalContainer.children.toList() as List<ConstraintLayout>).filterIsInstance<Interval>()
//            if (intervals.isNullOrEmpty()) return
//            val pausedInterval =
//                intervals.find { it.countDownState == Interval.CountDownStatus.PAUSED }
//            if (pausedInterval == null)   //se non ci sono countdown in pausa
//                intervals.filter {
//                    it.countDownState == Interval.CountDownStatus.NOTSTARTED    //prendi tutti gli intervalli non ancora partiti
//                }[0].startCountDown()   //e fai iniziare il primo
//            else pausedInterval.startCountDown()    //altrimenti riprendi il countdown messo in pausa

            timerButtonMiddle.isEnabled =
                false    //non si può premere play se il countdown sta andando
            timerButtonEdit.isEnabled = false      //nè editare
            timerButtonRight.isEnabled = true      //ma puoi mettere in pausa
            timerButtonLeft.isEnabled = true       //e stoppare
        }
    }

    private fun stop() {
        binding?.apply {
//            (timerIntervalContainer.children.toList() as List<Interval>)     //trova l'intervallo che è attivo, o in pausa
//                .filter { it.countDownState != Interval.CountDownStatus.NOTSTARTED }
//                .forEach { it.stopCountDown() }

            timerButtonMiddle.isEnabled = true
            timerButtonRight.isEnabled = false
            timerButtonLeft.isEnabled = false
            timerButtonEdit.isEnabled = true
        }
    }

    private fun delete() {
        TODO("Not yet implemented")
    }

    private fun done() {
        makeEditable(false)
    }

    private fun undo() {
        TODO("Not yet implemented")
    }

    /*Aggiunge la view usata per aggiungere intervalli*/
    private fun addIntervalCreatorView() {
        binding?.timerIntervalContainer?.addView(AddInterval(context) {
            addInterval()
            removeAddView()
            if (isTimerEditable) {          //se la view add era stata aggiunta perchè lista intervalli vuota, e non perchè è modificabile
                addIntervalCreatorView()
            }
        })
    }

    /*Rimuove la view usata per aggiungere intervalli*/
    private fun removeAddView() {
        binding?.apply {
            timerIntervalContainer.children.toList().find { view ->
                view is AddInterval
            }?.let {
                timerIntervalContainer.removeView(it)
            }
        }
    }

    /*Aggiunge intervalli*/
    private fun addInterval() {
        binding?.timerIntervalContainer?.addView((object : StoppedInterval(context) {
            override var onFinishedCallback: () -> Unit = ::startNext
        }))
    }

    private fun startNext() {
        binding?.let {
//            (it.timerIntervalContainer.children.toList() as List<Interval>)
//                .filter { it.countDownState == Interval.CountDownStatus.NOTSTARTED }.apply {
//                    if (this.isNotEmpty()) this[0].startCountDown()     //se ci sono ancora da iniziare, inizia il primo
//                    else finish()
//                }
        }
    }

    private fun finish() {
        repeatManager.apply {
            if (shouldRepeat()) repeat()
            else binding?.timerButtonEdit?.isEnabled = true
        }
    }

    inner class RepeatManager {
        private var repeat = RepeatStatus.DONT        //todo: Prendere dal repository

        fun decideVisibility() {
            binding?.apply {
                if (isTimerEditable) {
                    timerRepeatGroup.visibility = VISIBLE
                    timerIconRepeat.visibility = VISIBLE
                    timerRepeatText.visibility = VISIBLE
                } else {
                    timerRepeatGroup.visibility = when (repeat) {
                        RepeatStatus.DONT -> INVISIBLE
                        RepeatStatus.ALWAYS -> {
                            timerIconRepeat.visibility = VISIBLE
                            timerRepeatText.visibility = GONE
                            VISIBLE
                        }
                        RepeatStatus.NUMBER -> {
                            timerIconRepeat.visibility = VISIBLE
                            timerRepeatText.visibility = VISIBLE
                            VISIBLE
                        }
                    }
                }
            }
        }

        fun updateView() {
            binding?.timerRepeatText?.apply {
                Log.println(
                    Log.DEBUG,
                    "PROVA_TIMER",
                    "Updating - total: ${repeat.repCount}, current: ${repeat.getCurrent()}"
                )
                when (repeat) {
                    RepeatStatus.DONT -> {
                        hint = context.resources.getString(R.string.hint_repeat_dont)
                        isEnabled = false
                    }
                    RepeatStatus.ALWAYS -> {
                        hint = context.resources.getString(R.string.hint_repeat_repeat)
                        isEnabled = false
                    }
                    RepeatStatus.NUMBER -> {
                        isEnabled = isTimerEditable
                        setText(repeat.getCurrent().toString())
                    }
                }
                decideVisibility()
            }
        }

        fun setFromEditable(input: String) {
            if (isTimerEditable && input.isDigitsOnly()) {
                val times = input.toInt()
                repeat.repCount = times
                if (repeat.repCount == 0) {
                    repeat = RepeatStatus.DONT
                }
                updateView()
            }
        }

        fun repeat() {
            repeat--
            if (repeat== RepeatStatus.NUMBER && repeat.getCurrent() <=0) repeat.resetCurrent()
            updateView()
            stop()
            start()
        }

        fun shouldRepeat(): Boolean {
            return repeat != RepeatStatus.DONT
        }

        fun cycleStatus() {
            repeat = when (repeat) {
                RepeatStatus.DONT -> RepeatStatus.ALWAYS
                RepeatStatus.ALWAYS -> RepeatStatus.NUMBER
                RepeatStatus.NUMBER -> RepeatStatus.DONT
            }
            if (repeat == RepeatStatus.NUMBER) {
                binding?.timerRepeatText?.apply {
//                    isEnabled = true
                    focusAndShowKeyboard()
                }
            }
            updateView()
        }
    }
}