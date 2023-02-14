package com.example.chapter03.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

fun Modifier.drawWhiteCross() = then(
    object : DrawModifier {
        override fun ContentDrawScope.draw() {
            drawLine(
                color = Color.White,
                start = Offset(0F, 0F),
                end = Offset(size.width - 1, size.height - 1),
                strokeWidth = 10F
            )
            drawLine(
                color = Color.White,
                start = Offset(0F, size.height - 1),
                end = Offset(size.width - 1, 0F),
                strokeWidth = 10F
            )
            drawContent()
        }
    }
)

fun Modifier.drawHiddenCross() = then(
    object : DrawModifier {
        override fun ContentDrawScope.draw() {
            drawContent()
            drawBehind {
                drawLine(
                    color = Color.Blue,
                    start = Offset(0F, 0F),
                    end = Offset(size.width - 1, size.height - 1),
                    strokeWidth = 10F
                )
                drawLine(
                    color = Color.Blue,
                    start = Offset(0F, size.height - 1),
                    end = Offset(size.width - 1, 0F),
                    strokeWidth = 10F
                )
            }
        }
    }
)