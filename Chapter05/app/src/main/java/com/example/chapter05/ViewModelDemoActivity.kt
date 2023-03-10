@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.chapter05

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.coroutines.coroutineContext

class MyViewModel : ViewModel() {

    // observable 변수
    private val _text: MutableLiveData<String> =
        MutableLiveData<String>("Hello #3")

    val text: LiveData<String>
        get() = _text

    fun setText(value: String) {
        _text.value = value
    }
}

class ViewModelDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViewModelDemo()
        }
    }
}

@Composable
@Preview
fun ViewModelDemo() {
    val viewModel: MyViewModel = viewModel()
    // 상태를 임시로 기억함
    val state1 = remember {
        mutableStateOf("Hello #1")
    }
    /*
    It behaves similarly to remember,
     but the stored value will survive the activity or process recreation
      using the saved instance state mechanism
     (for example it happens when the screen is rotated in the Android application).
     */
    val state2 = rememberSaveable {
        mutableStateOf("Hello #2")
    }

    val testState = rememberSaveable(saver = Saver(
        save = {
            println("save")
            mutableStateOf("Hello #tester2")
        },
        restore = {
            println("restore")
            mutableStateOf("Restored")
        }
    )) {
        println("initial")
        mutableStateOf("initial")
    }


    val state3 = viewModel.text.observeAsState()    // 변경 가능한 상태를 리턴함, Nullable State를 리턴함
    state3.value?.let {
        Column(modifier = Modifier.fillMaxWidth()) {
            val context = LocalContext.current as Activity

            MyTextField(state1) { state1.value = it }
            MyTextField(state2) { state2.value = it }
            MyTextField(state3) {
                viewModel.setText(it)
            }
            MyTextField(testState) {
                testState.value = it
            }
            Button(onClick = {
                context.startActivity(Intent(context, ViewModelDemoActivity::class.java))
                context.finish()

            }) {
                Text(text = "재시작")
            }
        }
    }
}

@Composable
fun MyTextField(
    value: State<String?>,  // Nullable State
    onValueChange: (String) -> Unit
) {
    value.value?.let {
        TextField(
            value = it,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}