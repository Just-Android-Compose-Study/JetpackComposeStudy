package com.boringkm.chapter10

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentDescriptionTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun 공항셔틀을_ContentDescription으로_설정한_이미지의_가로_길이는_128dp다() {
        var contentDescription = ""
        rule.setContent {
            ImageDemo()
            contentDescription = stringResource(id = R.string.airport_shuttle)
        }
        rule.onNodeWithContentDescription(contentDescription)
            .assertWidthIsEqualTo(128.dp)
            .printToLog("")
    }
}