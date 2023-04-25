package com.boringkm.kotestpractice

import android.icu.util.Calendar
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BehaviorSpecTest: BehaviorSpec({
    given("2023년 4월 25일은") {
        val year = 2023; val month = 4; val day = 25
        `when`("달력에서 보았을 때") {
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            then("화요일이다.") {
                val result = calendar.get(Calendar.DAY_OF_WEEK)

                result shouldBe Calendar.TUESDAY
            }
        }
    }
})