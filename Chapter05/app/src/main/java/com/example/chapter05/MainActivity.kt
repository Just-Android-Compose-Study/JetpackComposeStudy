package com.example.chapter05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.chapter05.ui.theme.Chapter05Theme
import kotlinx.coroutines.delay
import java.util.*
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Chapter05Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RememberWithKeyDemo()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Chapter05Theme {
        Greeting("Android")
    }
}

@Composable
@Preview
fun SimpleStateDemo1() {
    val num = remember { mutableStateOf(Random.nextInt(0, 10)) }
    Text(text = "값: ${num.value}")

    LaunchedEffect(true) {
        delay(3000)
        num.value = 9999
    }
}

@Composable
@Preview
fun SimpleStateDemo2() {
    val num by remember { mutableStateOf(Random.nextInt(0, 10)) }
    // num 값을 직접 변경할 수는 없어졌다.
    // num 자체에서는 state 가지지 않음
    Text(text = num.toString())
}

@Composable
fun RememberWithKeyDemo() {
    var key by remember { mutableStateOf(false) }
    // key의 상태를 지켜보는 date, 변경시 람다식이 재실행 될 것임
    val date by remember(key) { mutableStateOf(Date()) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = date.toString())
        /*
        remember로 설정한 값이 이전 구성과 같다면 재평가되지 않고,
        동일하지 않은 경우에 새로운 값으로 계산하고, 이 값을 기억하고 반환한다.
        */
        Button(onClick = { key = !key }) {
            Text(text = stringResource(id = R.string.click))
        }
    }
}

@Composable
fun SimpleStatelessComposable2(text: State<String>) {
    Text(text = text.value)
}

// stateless
@ExperimentalMaterial3Api
@Composable
fun TextFieldDemo(state: MutableState<TextFieldValue>) {
    TextField(
        value = state.value,
        onValueChange = { state.value = it },
        placeholder = { Text("Hello") },
        modifier = Modifier.fillMaxWidth()
    )
}

// stateful
@ExperimentalMaterial3Api
@Composable
@Preview
fun TextFieldDemo() {
    val state = remember {
        mutableStateOf(TextFieldValue(""))
    }
    TextFieldDemo(state)
}