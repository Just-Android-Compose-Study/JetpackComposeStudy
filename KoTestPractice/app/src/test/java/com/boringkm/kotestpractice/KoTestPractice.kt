package com.boringkm.kotestpractice

import io.kotest.assertions.show.show
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class KoTestPractice : FunSpec({
    test("String length should return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
        show()
    }
})