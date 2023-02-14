package com.example.chapter03

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chapter03.ui.drawWhiteCross
import com.example.chapter03.ui.drawHiddenCross
import com.example.chapter03.ui.theme.Chapter03Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Chapter03Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextWithWarning1("", Modifier.background(Color.Blue)) {
                            Toast.makeText(applicationContext, "클릭됨", Toast.LENGTH_SHORT).show()
                        }
                        Box(Modifier.height(10.dp))
                        TextWithWarning2(Modifier.background(Color.Blue)) {
                            Toast.makeText(applicationContext, "클릭됨", Toast.LENGTH_SHORT).show()
                        }
                        Box(Modifier.height(10.dp))
                        TextWithoutWarning(
                            Modifier.background(Color.Blue),
                            Modifier.background(Color.Green)
                        ) {
                            Toast.makeText(applicationContext, "클릭됨", Toast.LENGTH_SHORT).show()
                        }
                        Box(Modifier.height(10.dp))
                        Text(
                            text = "Hello 여러분",
                            modifier = Modifier.height(30.dp).background(Color.Black).drawWhiteCross(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                        )
                        Text(
                            text = "숨겨진 크로스",
                            modifier = Modifier.height(30.dp).background(Color.Transparent).drawHiddenCross(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextWithWarning1(
    name: String = "Default",
    modifier: Modifier = Modifier,
    callback: () -> Unit
) {
    Text(text = "TextWithWarning1 $name!", modifier = modifier
        .background(Color.Yellow)
        .clickable { callback.invoke() })
}

@Composable
fun TextWithWarning2(test: Modifier = Modifier, name: String = "", callback: () -> Unit) {
    Text(text = "TextWithWarning2 $name!", modifier = test
        .background(Color.Yellow)
        .clickable { callback.invoke() })
}

@Composable
fun TextWithoutWarning(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier,
    name: String = "",
    callback: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "TextWithoutWarning $name!", modifier = modifier
            .padding(10.dp) // margin concept
            .background(Color.Yellow)
            .padding(10.dp) // real padding
            .clickable { callback.invoke() })

        val context = LocalContext.current
        Button(
            modifier = buttonModifier.clickable {
                Toast.makeText(context, "버튼에 clickable을 넣으면?", Toast.LENGTH_SHORT).show()
            },
            onClick = { Toast.makeText(context, "버튼 클릭됨", Toast.LENGTH_SHORT).show() }) {
            Text("버튼")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Chapter03Theme {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextWithWarning1("", Modifier.background(Color.Blue)) {}
            Box(Modifier.height(10.dp))
            TextWithWarning2(Modifier.background(Color.Blue)) {}
            Box(Modifier.height(10.dp))
            TextWithoutWarning(
                Modifier.background(Color.Blue),
                Modifier.background(Color.Green)
            ) {}
        }
    }
}