# 04장 요소 배치

## 미리 정의된 레이아웃 사용

Jetpack Compose는 기준이 되는 축을 따라 컨텐츠를 배열하는 기본적인 레이아웃을 제공한다.

- Horizontal: Row()
- Vertical: Column()
- Stacked: Box(), BoxWithConstraints()

### 기본 구성 요소 조합

- 가운데 정렬하기

```kotlin
@Composable
fun CheckboxWithLabel(label: String, state: MutableState<Boolean>) {
    Row(
        modifier = Modifier.clickable {
            state.value = !state.value
        }, verticalAlignment = Alignment.CenterVertically   // 수직 가운데 정렬
    ) {
        Checkbox(
            checked = state.value,
            onCheckedChange = {
                state.value = it
            }
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
```

- Box() 내에서 먼저 작성한 뷰부터 뒤에서 겹쳐 쌓인다.
- 먼저 Column과 Box를 사용한 예제부터 확인

```kotlin
@Composable
@Preview
fun PredefinedLayoutsDemo() {
    val red = remember { mutableStateOf(true) }
    val green = remember { mutableStateOf(true) }
    val blue = remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CheckboxWithLabel(
            label = stringResource(id = R.string.red),
            state = red
        )
        CheckboxWithLabel(
            label = stringResource(id = R.string.green),
            state = green
        )
        CheckboxWithLabel(
            label = stringResource(id = R.string.blue),
            state = blue
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            if (red.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                )
            }
            if (green.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                        .background(Color.Green)
                )
            }
            if (blue.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(64.dp)
                        .background(Color.Blue)
                )
            }
        }
    }
}
```

### 제약 조건을 기반으로 하는 레이아웃 생성

안드로이드의 전통적인 View기반 세계에서 ConstraintLayout으로 View 계층 구조를 평탄화한 것처럼 Box(), Row(), Column()을 중첩하는 행위를 제한하기 위한 방법

Compose에서는 기본 제공은 아니고 gradle 추가 필요함

```kotlin
implementation 'androidx.constraintlayout:constraintlayout-compose:1.0.1'
```

- Domain-Specific Language(DSL, 도메인 특화 언어)를 사용해 다른 요소와 연관이 있는 UI 요소의 위치와 크기를 정의한다.
- 그래서 내부에 있는 각각의 Composable 함수는 자신과 관련된 참조를 갖고 있어야 하며, 이 참조는 createRefs()를 사용해 생성된다.
- 제약조건은 modifier에서 constrainAs()를 사용하면 된다.

```kotlin
@Stable
fun Modifier.constrainAs(
    ref: ConstrainedLayoutReference,
    constrainBlock: ConstrainScope.() -> Unit
) = this.then(ConstrainAsModifier(ref, constrainBlock))
```

- constrainAs() 파라미터에서 후행 람다 표현식은 ConstrainScope를 받는다. [ConstrainScope 문서](https://developer.android.com/reference/kotlin/androidx/constraintlayout/compose/ConstrainScope)
- 이렇게 다른 컴포저블의 위치와 연결되는 위치를 정의하기 때문에 이런 프로퍼티를 anchors(앵커)라고 부른다.
- ConstraintLayout() 예제 확인해보자

```kotlin
@ExperimentalComposeUiApi
@Composable
@Preview
fun ConstraintLayoutDemo() {
    val red = remember { mutableStateOf(true) }
    val green = remember { mutableStateOf(true) }
    val blue = remember { mutableStateOf(true) }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (cbRed, cbGreen, cbBlue, boxRed, boxGreen, boxBlue) = createRefs()  // 여기서 제약조건의 참조가 될 값들을 선언/초기화 
        CheckboxWithLabel(
            label = stringResource(id = R.string.red),
            state = red,
            modifier = Modifier.constrainAs(cbRed) {
                top.linkTo(parent.top)  // 맨 위에 위치
            }
        )
        CheckboxWithLabel(
            label = stringResource(id = R.string.green),
            state = green,
            modifier = Modifier.constrainAs(cbGreen) {
                top.linkTo(cbRed.bottom)    // cbRed의 아래에 위치
            }
        )
        CheckboxWithLabel(
            label = stringResource(id = R.string.blue),
            state = blue,
            modifier = Modifier.constrainAs(cbBlue) {
                top.linkTo(cbGreen.bottom)  // cbGreen의 아래에 위치
            }
        )
        // Dimension.fillToConstraints
        if (red.value) {
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .constrainAs(boxRed) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(cbBlue.bottom, margin = 16.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )
        }
        if (green.value) {
            Box(
                modifier = Modifier
                    .background(Color.Green)
                    .constrainAs(boxGreen) {
                        start.linkTo(parent.start, margin = 32.dp)
                        end.linkTo(parent.end, margin = 32.dp)
                        top.linkTo(cbBlue.bottom, margin = (16 + 32).dp)
                        bottom.linkTo(parent.bottom, margin = 32.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )
        }
        if (blue.value) {
            Box(
                modifier = Modifier
                    .background(Color.Blue)
                    .constrainAs(boxBlue) {
                        start.linkTo(parent.start, margin = 64.dp)
                        end.linkTo(parent.end, margin = 64.dp)
                        top.linkTo(cbBlue.bottom, margin = (16 + 64).dp)
                        bottom.linkTo(parent.bottom, margin = 64.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )
        }
    }
}
```

## 단일 측정 단계의 이해

## 커스텀 레이아웃 작성
