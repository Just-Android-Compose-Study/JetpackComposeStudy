package com.boringkm.chapter10

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.ValueElement
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.boringkm.chapter10.ui.theme.Chapter10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Chapter10Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    SimpleButtonDemo()
                    ImageDemo()
                }
            }
        }
    }
}

@Composable
fun SimpleButtonDemo() {
    val a = stringResource(id = R.string.a)
    val b = stringResource(id = R.string.b)
    var text by remember { mutableStateOf(a) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, modifier = Modifier.clickable {
            text = if (text == a) b else a
        })
    }
}

@Composable
fun ImageDemo() {
    Image(
        painter = painterResource(id = R.drawable.ic_baseline_airport_shuttle_24),
        contentDescription = stringResource(id = R.string.airport_shuttle),
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .size(width = 128.dp, height = 128.dp)
            .background(Color.Blue)
    )
}

val COLOR1 = Color.White
val COLOR2 = Color.LightGray
const val TAG1 = "BoxButtonDemo"

val BackgroundColorKey = SemanticsPropertyKey<Color>("BackgroundColor")
var SemanticsPropertyReceiver.backgroundColor by BackgroundColorKey

@Composable
fun BoxButtonDemo() {
    isDebugInspectorInfoEnabled = true  // InspectableValue.kt 전역변수 설정
    var color by remember { mutableStateOf(COLOR1) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(TAG1)
            .semantics { backgroundColor = color }.also {
                (it as CombinedModifier).run {
                    val inner = this.javaClass.getDeclaredField("inner")
                    inner.isAccessible = true
                    val value = inner.get(this) as InspectorValueInfo
                    value.inspectableElements.forEach { ve: ValueElement ->
                        Log.i("ValueElement", "value element: $ve")
                    }
                }
            }
            .background(color = color),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            color = if (color == COLOR1)
                COLOR2
            else
                COLOR1
        }) {
            Text(text = stringResource(id = R.string.toggle))
        }
    }
}