package com.mikekuzn.mscheduler.entities

val repeats = arrayListOf<Repeat>(
    Repeat.Once,
    Repeat.Everyday,
    Repeat.EveryWeek(0b1111100),
    Repeat.EveryWeek(0b0000011),
    Repeat.EveryWeek(-1),
    Repeat.EveryMonth(-1),
    Repeat.EveryYear(-1, -1)
)

sealed class Repeat {
    object Once : Repeat() {
        override fun toString(): String {
            return "Once"
        }
    }
    object Everyday : Repeat() {
        override fun toString() = "Everyday"
    }

    class EveryWeek(private val weekMask: Int) : Repeat() {
        // TODO change all strings to recurse and make multi-language support and change first week dey (Вс/Пн)
        private var stringValue: String = ""

        init {
            val weekdayNames = arrayListOf(
                "Пн",
                "Вт",
                "Ср",
                "Чт",
                "Пт",
                "Сб",
                "Вс"
            )
            if (weekMask == 0b1111100) stringValue = "Weekdays"
            else if (weekMask == 0b0000011) stringValue = "Weekends"
            else if (weekMask <= 0b0000000 || weekMask > 0b1111111) stringValue += "EveryWeek ... TODO"
            else {
                for (i in 0..7) {
                    if (weekMask and 1 shl i != 0) {
                        stringValue += " ${weekdayNames[i]}"
                    }
                }
            }
        }

        override fun toString() = stringValue

    }
    class EveryMonth(val day: Byte) : Repeat() {
        // TODO
        override fun toString() = "EveryMonth ... TODO"
    }
    class EveryYear(val month: Byte, val day: Byte) : Repeat() {
        // TODO
        override fun toString() = "EveryYear ... TODO"
    }
}