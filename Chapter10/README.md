# 10. 컴포즈 앱 테스트와 디버깅

## 환경설정과 테스트 작성

**테스트와 디버깅은 꼭 필요하다.**

중요한 프로그램들이 모두 그래왔듯 결국 내가 개발하는 앱에도 버그가 발생할 것이다.

개발자로서의 삶을 더욱 윤택하게 만드려면 스스로 테스트 코드를 작성하고 자신의 코드나 다른 사람의 코드를 디버깅하는 데 익숙해야만 한다.

> - 유닛 테스트: 비즈니스 로직이 예상대로 동작함을 확인해야만 한다.
> - 통합 테스트: 앱의 모든 구성 요소가 적절히 통합되어 있는지 확인한다. ex) 앱이 하는 일에 따라 원격 서비스에 접근, DB와 연동, 디바이스에 파일을 읽고 쓰는 동작을
    포함할 수 있다.
> - UI 테스트: UI가 정확히 구현되었는지 테스트한다. 지원하는 모든 화면 크기에 대해 모든 UI 요소가 잘 나타나는지, 항상 적절한 값을 보여주는지, 버튼을 클릭하거나
    슬라이더를 이동하는 등의 상호작용이 의도된 함수를 호출시키는지? 앱의 모든 영역이 접근성을 가지는 지 확인해야 한다.

**테스트 피라미드**: Unit Test - 통합 테스트 - UI 테스트로 이어지는 구조

### 유닛 테스트 구현

- Unit은 작고 고립된 코드 조각으로, 프로그래밍 언어에 따라 일반적으로 function, method, sub routine, property가 이에 해당한다.
- Test class는 하나 이상의 테스트를 포함한다.
- 테스트는 잘 정의된 상황이나 조건 또는 기준이 포함되어 있는지를 확인한다.
- 테스트는 격리되어야 한다.
- **테스트는 이전 테스트에 의존해서는 안 된다.**
- **단언문(assertion)**은 예상되는 행위를 나타낸다. 단언문을 충족시키지 못하면 테스트는 실패한다.

### Composable 함수 테스트

```kotlin
@Composable
fun SimpleButtonDemo() {
    val a = stringResource(id = R.string.a)
    val b = stringResource(id = R.string.b)
    var text by remember { mutableStateOf(a) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            text = if (text == a) b else a
        }) {
            Text(text = text)
        }
    }
}
```

- UI 테스트 코드는 아래에서

```kotlin
// SimpleInstrumentedTest.kt
@RunWith(AndroidJUnit4::class)
class SimpleInstrumentedTest {

    @get:Rule
    var name = TestName()   // 테스트 메서드 내부에서 현재 테스트 이름을 제공할 수 있게 해준다.

    /* 
    createComposeRule: ComposeContentTestRule 구현체, AndroidComposeTestRule<ComponentActivity>
    createAndroidComposeRule: ComponentActivity 이외의 액티비티 클래스용 AndroidComposeTestRule을 생성할 수 있게 해준다.
     */
    @get:Rule
    val rule: ComposeContentTestRule = createComposeRule()

    @Before
    fun setup() {
        // 테스트할 Composable 함수 로딩
        // 테스트마다 정확히 한 번만 호출되어야 한다.
        rule.setContent {
            SimpleButtonDemo()
        }
    }

    @Test
    fun testInitialLetterIsA() {
        // onNodeWithText: finder라 불린다. 시맨틱 노드에서 동작한다.
        // 특정 컴포저블이 기대한 대로 나타나거나 동작하는지 테스트하려면 컴포즈 계층 구조의 모든 자식 사이에서 해당 컴포저블을 찾아내야 한다.
        // 여기서 시맨틱 트리가 동작한다. -> UI 계층 구조와 동시에 생성되며 Rule, Text, Action과 같은 속성을 사용해 계층 구조를 설명한다.
        rule.onNodeWithText("A").assertExists()
    }

    @Test
    fun testPrintMethodName() {
        println(name.methodName)    // Logcat에서 testPrintMethodName 확인하면 된다. 
    }
}
```

## 시맨틱 이해

## 컴포즈 앱 디버깅
