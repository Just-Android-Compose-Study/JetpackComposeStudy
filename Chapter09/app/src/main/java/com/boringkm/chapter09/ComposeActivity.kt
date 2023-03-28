@file:OptIn(ExperimentalMaterial3Api::class)

package com.boringkm.chapter09

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.boringkm.chapter09.databinding.CustomBinding

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MyViewModel by viewModels()  // ViewModel 초기화
        viewModel.setSliderValue(intent.getFloatExtra(KEY, 0F)) // Slider 값 초기화
        setContent {
            ViewIntegrationDemo(viewModel) {
                // onClick
                // ComposeActivity -> ViewActivity
                val i = Intent(
                    this,
                    ViewActivity::class.java
                )
                i.putExtra(KEY, viewModel.sliderValue.value)    // 현재 sliderValue
                startActivity(i)
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ViewIntegrationDemo(viewModel: MyViewModel, onClick: () -> Unit) {
    val sliderValueState = viewModel.sliderValue.observeAsState()
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title =
            {
                Text(text = stringResource(id = R.string.compose_activity))
            })
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Slider(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    viewModel.setSliderValue(it)
                },
                value = sliderValueState.value ?: 0F
            )
            // AndroidViewBinding 사용
            AndroidViewBinding(
                modifier = Modifier.fillMaxWidth(),
                factory = CustomBinding::inflate
            ) {
                // custom.xml 안에 있는 textView, button
                textView.text = sliderValueState.value.toString()
                button.setOnClickListener {
                    onClick()
                }
            }
        }
    }
}