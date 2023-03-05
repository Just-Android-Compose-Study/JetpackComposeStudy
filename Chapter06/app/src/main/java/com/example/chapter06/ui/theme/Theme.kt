package com.example.chapter06.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.chapter06.R

private val DarkColorScheme = darkColorScheme(
    primary = AndroidGreenDark,
    secondary = OrangeDark
)

private val LightColorScheme = lightColorScheme(
    primary = AndroidGreen,
    secondary = Orange,
    onPrimaryContainer = AndroidGreenDark,
    onSecondaryContainer = OrangeDark

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun Chapter06Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Android 31 미만에서는 다크모드 X
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme.copy(secondary = colorResource(id = R.color.orange_dark))
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        /* getting the current window by tapping into the Activity */
        val currentWindow = (view.context as? Activity)?.window
            ?: throw Exception("Not in an activity - unable to get Window reference")

        SideEffect {
            /* the default code did the same cast here - might as well use our new variable! */
            currentWindow.statusBarColor = colorScheme.primary.toArgb()
            /* accessing the insets controller to change appearance of the status bar, with 100% less deprecation warnings */
            WindowCompat
                .getInsetsController(currentWindow, view)
                .isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography.copy(labelLarge = TextStyle(fontSize = 24.sp)),
        content = content,
        shapes = MaterialTheme.shapes.copy(small = CutCornerShape(8.dp))
    )
}

@Composable
@Preview
fun MaterialThemeDemo() {
    MaterialTheme(
        typography = Typography.copy(	// Typography 재사용
            displayLarge = TextStyle(color = Color.Red)
        )
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello",
                style = MaterialTheme.typography.displayLarge	// 수정된 MaterialTheme
            )
            Spacer(modifier = Modifier.width(2.dp))
            MaterialTheme(
                typography = Typography.copy(	// Typography 재사용
                    displayLarge = TextStyle(color = Color.Blue)
                )
            ) {
                Text(
                    text = "Compose",
                    style = MaterialTheme.typography.displayLarge // 수정된 MaterialTheme
                )
            }
        }
    }
}