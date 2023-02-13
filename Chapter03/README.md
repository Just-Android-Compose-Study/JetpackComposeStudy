# 03장 컴포즈 핵심 원칙 자세히 알아보기

## 컴포저블 함수 자세히 살펴보기

### 컴포저블 함수의 구성 요소
- 가시성 변경자: 용도에 따라 접근 범위를 정하면 되겠다.
- fun
- 함수명: **파스칼 표기법**으로 작성
- 매개변수: 선택적
- 반환 타입: 선택적 -> 값을 리턴하지 않아도 Composable 함수가 화면에 표현된다.
- 코드 블록

### UI 요소 내보내기
- IDE를 이용해 직접 확인해보기

#### androidx.compose.material.Text()

```kotlin
val textColor = color.takeOrElse {
    style.color.takeOrElse {
        LocalContentColor.current
    }
}
// NOTE(text-perf-review): It might be worthwhile writing a bespoke merge implementation that
// will avoid reallocating if all of the options here are the defaults
val mergedStyle = style.merge(
    TextStyle(
        color = textColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        lineHeight = lineHeight,
        fontFamily = fontFamily,
        textDecoration = textDecoration,
        fontStyle = fontStyle,
        letterSpacing = letterSpacing
    )
)
BasicText(
    text,
    modifier,
    mergedStyle,
    onTextLayout,
    overflow,
    softWrap,
    maxLines,
)
```

#### BasicText()

> **BasicText**에서 찾은 CoreText 관련 주석
>
> The ID used to identify this CoreText. If this CoreText is removed from the composition tree and then added back, this ID should stay the same.
>
> Notice that we need to update selectable ID when the input text or selectionRegistrar has been updated.
>
> When text is updated, the selection on this CoreText becomes invalid. It can be treated as a brand new CoreText.
>
> When SelectionRegistrar is updated, CoreText have to request a new ID to avoid ID collision.

![img.png](CoreText.png)
- https://developer.android.com/jetpack/androidx/releases/compose-foundation#1.0.0-alpha12
- CoreText는 public API에서 사용 불가
- BasicText() 하단부에서 Layout()을 호출하고 있다.

#### Layout()

```kotlin
@Suppress("NOTHING_TO_INLINE")
@Composable
@UiComposable
inline fun Layout(
    modifier: Modifier = Modifier,
    measurePolicy: MeasurePolicy
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val viewConfiguration = LocalViewConfiguration.current
    val materialized = currentComposer.materialize(modifier)
    ReusableComposeNode<ComposeUiNode, Applier<Any>>(
        factory = ComposeUiNode.Constructor,
        update = {
            set(measurePolicy, ComposeUiNode.SetMeasurePolicy)
            set(density, ComposeUiNode.SetDensity)
            set(layoutDirection, ComposeUiNode.SetLayoutDirection)
            set(viewConfiguration, ComposeUiNode.SetViewConfiguration)
            set(materialized, ComposeUiNode.SetModifier)
        },
    )
}
```

- ```ReusableComposeNode()```를 호출한다.
- Node라고 하는 UI 요소 계층 구조를 내보낸다.
- 노드는 factory를 통해 생성된다.
- ```update```: 업데이트를 수행하는 코드 작성
- ```skippableUpdate```: 변경자를 조작하는 코드 작성 -> ```materialized```로 바뀜

#### ReusableComposeNode()

```kotlin
// ComposeNode is a special case of readonly composable and handles creating its own groups, so
// it is okay to use.
@Suppress("NONREADONLY_CALL_IN_READONLY_COMPOSABLE", "UnnecessaryLambdaCreation")
@Composable inline fun <T : Any, reified E : Applier<*>> ReusableComposeNode(
    noinline factory: () -> T,
    update: @DisallowComposableCalls Updater<T>.() -> Unit
) {
    if (currentComposer.applier !is E) invalidApplier()
    currentComposer.startReusableNode()
    if (currentComposer.inserting) {
        currentComposer.createNode { factory() }    // 여기서 노드를 생성한다.
    } else {
        currentComposer.useNode()   // 기존 노드 사용
    }
    currentComposer.disableReusing()
    Updater<T>(currentComposer).update()
    currentComposer.enableReusing()
    currentComposer.endNode()
}
```

- ```ReusableComposeNode```는 **새로운 노드가 생성돼야 할지 또는 기존 노드를 재사용해야 할지를 결정한다.**
- ```currentComposer```는 androidx.compose.runtime.Composables.kt에 있는 최상위 Composer 변수

#### ComposableUiNode

```kotlin
/**
 * Interface extracted from LayoutNode to not mark the whole LayoutNode class as @PublishedApi.
 */
@PublishedApi
internal interface ComposeUiNode {
    var measurePolicy: MeasurePolicy
    var layoutDirection: LayoutDirection
    var density: Density
    var modifier: Modifier
    var viewConfiguration: ViewConfiguration

    /**
     * Object of pre-allocated lambdas used to make use with ComposeNode allocation-less.
     */
    companion object {
        val Constructor: () -> ComposeUiNode = LayoutNode.Constructor
        val VirtualConstructor: () -> ComposeUiNode = { LayoutNode(isVirtual = true) }
        val SetModifier: ComposeUiNode.(Modifier) -> Unit = { this.modifier = it }
        val SetDensity: ComposeUiNode.(Density) -> Unit = { this.density = it }
        val SetMeasurePolicy: ComposeUiNode.(MeasurePolicy) -> Unit =
            { this.measurePolicy = it }
        val SetLayoutDirection: ComposeUiNode.(LayoutDirection) -> Unit =
            { this.layoutDirection = it }
        val SetViewConfiguration: ComposeUiNode.(ViewConfiguration) -> Unit =
            { this.viewConfiguration = it }
    }
}
```

- Modifier
- Density
- MeasurePolicy
- LayoutDirection
- VirtualConstructor (새로 생김)
- ViewConfiguration (새로 생김)

### 값 반환

- Composable 함수의 주목적은 UI 구성이기 때문에 대부분 반환 타입을 명시하지 않는다.
- Composition이나 Recomposition의 일부인 무언가를 반환해야 한다면 반드시 Composable 함수여야 한다.
- 반환된 데이터가 Compose와 아무 관련이 없더 상태를 유지하기 위한 값으로 사용한다면 Composable 함수로 만들어야 한다.
  - **이런 경우에는 함수명을 CamelCase로 작성**

## UI 구성과 재구성

- Jetpack Compose는 앱 데이터가 변경되어 UI가 변경되어야 하는 경우 개발자가 선제적으로 컴포넌트 트리를 변경하는 행위에 의존하지 않는다.
- 대신 이러한 변화를 자체적으로 감지하고 영향을 받는 부분만 갱신한다.

### Composable 함수 간 상태 공유

```kotlin
@Composable
fun ColorPicker(color: MutableState<Color>) {
    val red = color.value.red
    val green = color.value.green
    val blue = color.value.blue
    Column {
        Slider(
            value = red,
            onValueChange = { color.value = Color(it, green, blue) })
        Slider(
            value = green,
            onValueChange = { color.value = Color(red, it, blue) })
        Slider(
            value = blue,
            onValueChange = { color.value = Color(red, green, it) })
    }
}
```

- ```ColorPicker()``` 함수가 ```Color```가 아닌 ```MutableState<Color>``` 를 파라미터로 받는 이유
  - color의 값이 ColorPicker 안에서만 변경될 것이기 때문에 상위 Composable 함수에서 해당 변화에 대해 알 수 있어야 한다.

- 전역 프로퍼티를 사용하는 방식도 있겠지만 이는 권고하지 않는 방식이며, 되도록 Composable 함수의 모습과 행위에 영향을 주는 모든 데이터는 매개변수(parameter)로 전달하는 것이 좋다.

- state hoisting(상태 호이스팅): 상태를 전달받아 Composable 함수를 호출한 곳으로 상태를 옮기는 것

> ### 중요 사항
>
> Composable을 SideEffect가 없게 만들자. (동일한 인자로 함수를 반복적으로 호출해도 항상 동일한 결과를 생산함)
>
> 호출자로부터 모든 관련 데이터를 얻는 것 이외에도 전역프로퍼티에 의존하거나 예측 불가능한 값을 반환하는 함수 호출하는 것도 금지된다. -> 보통 IDE에서 경고해주는 것 같다.

- Compose UI는 Composable 함수의 중첩 호출로 정의된다.
- Composable 함수는 UI 요소 또는 UI 요소 계층 구조를 발행한다.
- UI를 처음 구성하는 것을 Composition이라고 부른다.
- 앱 데이터 변경 시 UI를 재구성하는 것을 Recomposition이라 부른다.
- Recomposition은 자동으로 발생한다.

### 크기 제어

- ```fillMaxSize()```, ```fillMAxWidth()```, ```BoxWidthConstraints()``` 등을 잘 활용해보기

### 액티비티 내에서 Composable 계층 구조 나타내기

> #### setContent
> **parent**: null 값이 가능한 CompositionContext
> **content**: 선언하는 UI를 위한 Composable function

```kotlin
// ComponentActivity.kt
public fun ComponentActivity.setContent(
    parent: CompositionContext? = null,
    content: @Composable () -> Unit
) {
	// 액티비티가 이미 ComposeView의 인스턴스를 포함하는지 알아내기 위해 사용된다.
    val existingComposeView = window.decorView
        .findViewById<ViewGroup>(android.R.id.content)
        .getChildAt(0) as? ComposeView

    if (existingComposeView != null) with(existingComposeView) {
        setParentCompositionContext(parent)
        setContent(content)
    } else ComposeView(this).apply { // findViewById 실패 시 새로운 인스턴스 생성: ComposeView
        // Set content and parent **before** setContentView
        // to have ComposeView create the composition on attach
        setParentCompositionContext(parent)
        setContent(content)
        // Set the view tree owners before setting the content view so that the inflation process
        // and attach listeners will see them already present
        setOwners()
        setContentView(this, DefaultActivityContentLayoutParams)
    }
}
```

```kotlin
// ComposeView.android.kt AbstractComposeView
private var parentContext: CompositionContext? = null
    set(value) {
        if (field !== value) {
            field = value
            if (value != null) {
                cachedViewTreeCompositionContext = null
            }
            val old = composition
            if (old !== null) {
                old.dispose()
                composition = null
                // Recreate the composition now if we are attached.
                if (isAttachedToWindow) {
                    ensureCompositionCreated()
                }
            }
        }
    }
```

```kotlin
// ComposeView.android.kt

// parentContext 대체재 찾기 
private fun resolveParentCompositionContext() = parentContext
    ?: findViewTreeCompositionContext()?.cacheIfAlive()
    ?: cachedViewTreeCompositionContext?.get()?.takeIf { it.isAlive }
    ?: windowRecomposer.cacheIfAlive()

@Suppress("DEPRECATION") // Still using ViewGroup.setContent for now
private fun ensureCompositionCreated() {
    if (composition == null) {
        try {
            creatingComposition = true
            composition = setContent(resolveParentCompositionContext()) {
                Content()
            }
        } finally {
            creatingComposition = false
        }
    }
}
```

## 컴포저블 함수의 행위 수정

-

## 요약