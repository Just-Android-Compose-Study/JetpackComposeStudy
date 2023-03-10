@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.chapter01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chapter01.ui.theme.Chapter01Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // layoutId 참조가 아닌 setContent 함수 호출
        // parent: androidx.compose.runtime.CompositionContext 인스턴스
        // content: UI
        setContent {
            Chapter01Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Hello()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(
        text = stringResource(id = R.string.hello, name),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
fun Welcome() {
    Text(
        text = stringResource(id = R.string.welcome),
        style = MaterialTheme.typography.labelMedium,
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xff00ffff,
    widthDp = 100,
    heightDp = 100,
    showSystemUi = true,
    group = "test-group"
)
@Composable
fun WelcomePreview() {
    Chapter01Theme {
        Welcome()
    }
}


@Preview(showBackground = true, group = "test-group")
@Composable
fun GreetingPreview() {
    Chapter01Theme {
        Greeting("boring-km")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAndButton(name: MutableState<String>, nameEntered: MutableState<Boolean>) {
    Row(modifier = Modifier.padding(top = 8.dp)) {
        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            placeholder = {
                Text(
                    text = stringResource(
                        id = R.string.hint
                    )
                )
            },
            modifier = Modifier
                .alignByBaseline()  // 특정 Row() 내부에서 다른 컴포지션 함수들의 기준선을 정렬할 수 있다.
                .weight(1.0f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                capitalization = KeyboardCapitalization.Words,
            ),
            keyboardActions = KeyboardActions(onAny = {
                nameEntered.value = true
            })
        )
        Button(
            onClick = {
                nameEntered.value = true
            }, modifier = Modifier
                .alignByBaseline()
                .padding(8.dp)
        ) {
            Text(text = stringResource(id = R.string.done))
        }
    }
}

@Composable
fun Hello() {
    // remember, mutableStateOf의 역할에 집중해보자
    val name = remember {
        mutableStateOf("")
    }
    val nameEntered = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (nameEntered.value) {
            Greeting(name = name.value)
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Welcome()
                TextAndButton(name = name, nameEntered = nameEntered)
            }
        }
    }
}

@Composable
@Preview(group = "test-group2")
fun PreviewHello() {
    Chapter01Theme {
        Hello()
    }
}

// 이런 방법도 있지만 너무 불편해보여서 안 쓸란다.
//class HelloProvider : PreviewParameterProvider<String> {
//    override val values: Sequence<String>
//        get() = listOf("PreviewParameterProvider").asSequence()
//}
//
//@Composable
//@Preview
//fun AltGreeting2(@PreviewParameter(HelloProvider::class) name: String) {}