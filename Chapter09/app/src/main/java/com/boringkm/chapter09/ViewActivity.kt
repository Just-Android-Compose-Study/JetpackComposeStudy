package com.boringkm.chapter09

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.boringkm.chapter09.databinding.LayoutBinding

const val KEY = "key"

class ViewActivity : AppCompatActivity() {

    private lateinit var binding: LayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: MyViewModel by viewModels()  // ViewModel 초기화
        viewModel.setSliderValue(intent.getFloatExtra(KEY, 0F)) // Slider 값 초기화

        viewModel.sliderValue.observe(this) {// ViewModel 내 sliderValue 값 observing
            binding.slider.value = it
        }
        // slider 값 ViewModel 내 sliderValue에 반영
        binding.slider.addOnChangeListener { _, value, _ -> viewModel.setSliderValue(value) }

        // layout에서 ComposeView 사용
        binding.composeView.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                val sliderValue = viewModel.sliderValue.observeAsState()
                sliderValue.value?.let {
                    // ViewActivity -> ComposeActivity
                    ComposeDemo(it) {   // 버튼 클릭 시 동작
                        val i = Intent(
                            context,
                            ComposeActivity::class.java
                        )
                        i.putExtra(KEY, it)
                        startActivity(i)
                    }
                }
            }
        }
    }
}

@Composable
fun ComposeDemo(value: Float, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .height(64.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString()
            )
        }
        Button(
            onClick = onClick,  // 버튼 클릭 시 ComposeActivity 띄우기
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.compose_activity))
        }
    }
}