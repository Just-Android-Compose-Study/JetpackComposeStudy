# 06장 조립

## 컴포즈 앱 스타일링

Material You (Material 3) 사용하면서 변경사항이 좀 많은 것 같아 레퍼런스 참고
> - https://m3.material.io/
> - [Compose의 Material 2에서 Material 3으로 이전](https://developer.android.com/jetpack/compose/designsystems/material2-material3?hl=ko)

### 색상, 모양, 텍스트 스타일 정의

예제코드 중 Material3이 반영되지 않은 코드는 변환해가며 작성해봤다.

부모 테마를 재정의해 테마를 중첩할 수 있다.

```kotlin
@Composable
@Preview
fun MaterialThemeDemo() {
    MaterialTheme(
        typography = Typography.copy(	// Typography 재사용
            displayLarge = TextStyle(color = Color.Red)
        )
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello",
                style = MaterialTheme.typography.displayLarge	// 수정된 MaterialTheme
            )
            Spacer(modifier = Modifier.width(2.dp))
            MaterialTheme(
                typography = Typography.copy(	// Typography 재사용
                    displayLarge = TextStyle(color = Color.Blue)
                )
            ) {
                Text(
                    text = "Compose",
                    style = MaterialTheme.typography.displayLarge // 수정된 MaterialTheme
                )
            }
        }
    }
}
```

### 리소스 기반의 테마 사용

테마 xml에도 컬러 값을 사용하고 컴포즈에서도 컬러 값을 또 선언하지 않으려면 ```colorResource()```을 사용해 리소스 쪽에서만 컬러 값을 정리해두자.

```kotlin
val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        // Android 31 미만에서는 다크모드 X
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme.copy(secondary = colorResource(id = R.color.orange_dark))
}
```

SplashScreen을 안드로이드 12 이전에서도 사용할 수 있는 방법도 있다니 찾아보자.

## 툴바와 메뉴 통합

## 네비게이션 추가

## 요약