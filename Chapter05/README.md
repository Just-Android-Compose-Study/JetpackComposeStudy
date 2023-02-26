# 05장 컴포저블 함수 상태 관리

## 상태를 갖거나 갖지 않는 컴포저블 함수 이해

**UI는 항상 현재 데이터를 보여줘야만 한다는 것이 중요하다. 따라서 값이 변경되면 반드시 UI에 알려야 한다.**
이를 위해 ```observable``` 타입을 사용한다.

```kotlin
// kotlin에서 사용되는 예시
var counter by observable(-1) { _, oldValue, newValue ->
	println("$oldValue -> $newValue")
}

for (i in 0..3) counter = i
```

Jetpack Compose에서는 이러한 콜백 함수 없이도 상태가 변경되면 자동으로 관련된 UI 요소를 재구성하도록 동작한다.

### Composable 함수에서 상태 사용

stateful: Composable 함수가 값을 유지(remember)하고 있으면 stateful 함수다

```kotlin
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
```

```kotlin
// caculation은 기억할 값을 생성하는 lambda 표현식
// 구성되는 동안 단 한 번만 평가되고 다시는 평가되지 않는다.
@Composable
inline fun <T> remember(crossinline calculation: @DisallowComposableCalls () -> T): T =
    currentComposer.cache(false, calculation)

@Composable
inline fun <T> remember(
    key1: Any?,
    crossinline calculation: @DisallowComposableCalls () -> T
): T {
	// key1으로 들어오는 값이 변경되었는지 체크
    return currentComposer.cache(currentComposer.changed(key1), calculation)
}
```

- changed 내용

```kotlin
/**
 * A Compose compiler plugin API. DO NOT call directly.
 *
 * Check [value] is different than the value used in the previous composition. This is used,
 * for example, to check parameter values to determine if they have changed.
 *
 * @param value the value to check
 * @return `true` if the value if [equals] of the previous value returns `false` when passed
 * [value].
 */
@ComposeCompilerApi
fun changed(value: Any?): Boolean
```

- Composer.cache()

```kotlin
// invalid 값이 위에 있는 check에서 값이 변경되었을 때 true로 들어오면서 값이 변경된다. (block 람다식 재실행)
@ComposeCompilerApi
inline fun <T> Composer.cache(invalid: Boolean, block: @DisallowComposableCalls () -> T): T {
    @Suppress("UNCHECKED_CAST")
    return rememberedValue().let {
        if (invalid || it === Composer.Empty) {
            val value = block()
            updateRememberedValue(value)
            value
        } else it
    } as T
}
```

```kotlin
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
```

### 상태를 갖지 않는 Composable 함수 작성

```kotlin
@Composable
fun SimpleStatelessComposable2(text: State<String>) {
    Text(text = text.value)
}
```

이 함수는 파라미터로 상태를 받지만 저장하지 않고 다른 상태를 기억하지도 않는다.
**멱등성(idempotent)**: 연산을 여러 번 적용하더라도 결과가 달라지지 않는 성질을 의미

#### Composable 함수의 준수사항
- 빠름: Composable은 무거운 연산을 하지 말아야 한다. 웹 서비스나 어떠한 I/O도 호출해서는 안 된다. Composable에서 사용하는 데이터는 전달받는 형식이 돼야 한다.

- 부수 효과에서 자유로움: 전역 프로퍼티를 수정하거나 의도치 않은 observable 효과를 생산하지 말아야 한다.

- 멱등성: ```remember { }```를 사용하지 않고(1) 전역 프로퍼티에도 접근하지 않으며(2) **예측 불가능한 코드를 호출하지 말아야 한다.(3)**

#### 상태를 갖는 Composable, 상태를 갖지 않는 Composable

**상태를 갖는 Composable에서 상태를 갖지 않는 Composable을 호출하자.**

```kotlin
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
```

## 상태 호이스팅과 이벤트 전달

## 환경설정 변경에도 데이터 유지

## 요약