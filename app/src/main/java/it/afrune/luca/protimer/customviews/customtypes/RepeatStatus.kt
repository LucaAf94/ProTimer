package it.afrune.luca.protimer.customviews.customtypes

enum class RepeatStatus {
    DONT, ALWAYS, NUMBER;

    private var totalRep = 0
    private var currentRep = 0
    var repCount : Int
        get() {return totalRep}
        set(value) {
            totalRep = value
            currentRep = totalRep
        }

    operator fun dec() : RepeatStatus {
        return this.apply {
            currentRep--
        }
    }

    fun getCurrent(): Int {return currentRep}
    fun resetCurrent() {currentRep = totalRep}
}