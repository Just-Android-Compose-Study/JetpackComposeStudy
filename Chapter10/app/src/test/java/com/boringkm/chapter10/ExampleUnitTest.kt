package com.boringkm.chapter10

import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupAll() {
            println("Setting up")
        }
    }

    @Before
    fun setup() {
        println("Setup test")
    }

    @After
    fun teardown() {
        println("Clean up test")
    }

    @Test
    fun 정수_리스트의_각_항목이_짝수인지_아닌지는_results에_적혀있는_결과와_같다() {
        val nums = listOf(Int.MIN_VALUE, -3, -2, 2, 3, Int.MAX_VALUE)
        val results = listOf(true, false, true, true, false, false)
        nums.forEachIndexed { index, num ->
            val result = isEven(num)
            println("isEven($num) returns $result")
            assertEquals(result, results[index])
        }
    }

    @Test
    fun 값이_0이면_짝수다() {
        assertTrue(isEven(0))
    }
}