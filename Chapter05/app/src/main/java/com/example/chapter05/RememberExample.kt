@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.chapter05

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureTextField(
    temperature: MutableState<String>,  // 파라미터로 상태를 전달 받으며
    modifier: Modifier = Modifier,
    callback: () -> Unit
) {
    TextField(
        value = temperature.value,
        onValueChange = {
            temperature.value = it  // 변경 사항을 상태에 다시 저장한다.
        },
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder))
        },
        modifier = modifier,
        keyboardActions = KeyboardActions(onAny = {
            callback()
        }),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

@Composable
fun TemperatureRadioButton(
    selected: Boolean,
    resId: Int,
    onClick: (Int) -> Unit, // 파라미터를 받는 콜백함수를 파라미터로 설정
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                onClick(resId)  // onClick 콜백함수 호출
            }
        )
        Text(
            text = stringResource(resId),
            modifier = Modifier
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun TemperatureScaleButtonGroup(
    selected: MutableState<Int>,    // 상태를 받음
    modifier: Modifier = Modifier
) {
    val sel = selected.value
    val onClick = { resId: Int -> selected.value = resId }  // 3: 새로운 상태 값으로 지정한다.
    Row(modifier = modifier) {
        TemperatureRadioButton(
            selected = sel == R.string.celsius,
            resId = R.string.celsius,   // 2: redId 값을
            onClick = onClick   // 1: 라디오 버튼을 클릭하면, (버블업, bubble up)
        )
        TemperatureRadioButton(
            selected = sel == R.string.fahrenheit,
            resId = R.string.fahrenheit,
            onClick = onClick,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

// Convert

@Composable
@Preview
fun FlowOfEventsDemo() {
    val strCelsius = stringResource(id = R.string.celsius)
    val strFahrenheit = stringResource(id = R.string.fahrenheit)
    val temperature = remember { mutableStateOf("") }
    val scale = remember { mutableStateOf(R.string.celsius) }
    var convertedTemperature by remember { mutableStateOf(Float.NaN) }
    val calc = {
        val temp = temperature.value.toFloat()
        convertedTemperature = if (scale.value == R.string.celsius)
            (temp * 1.8F) + 32F // 섭씨 * 1.8 + 32
        else
            (temp - 32F) / 1.8F // (화씨 - 32) / 1.8
    }
    // 섭씨면 화씨로, 화씨면 섭씨로 표현
    val result = remember(convertedTemperature) {
        println("converted?: $convertedTemperature")
        if (convertedTemperature.isNaN())
            ""
        else
            "${convertedTemperature}${
                if (scale.value == R.string.celsius)
                    strFahrenheit
                else strCelsius
            }"
    }
    val enabled = temperature.value.isNotBlank()    //  비어 있지 않으면 모두 활성화
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TemperatureTextField(
            temperature = temperature,
            modifier = Modifier.padding(bottom = 16.dp),
            callback = calc     // 키보드 액션에서 완료 눌렀을 때
        )
        TemperatureScaleButtonGroup(
            selected = scale,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = calc,     // 직접 변환
            enabled = enabled
        ) {
            Text(text = stringResource(id = R.string.convert))
        }
        if (result.isNotEmpty()) {  // 결과가 있을 때만 보이는 Text
            Text(
                text = result,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}