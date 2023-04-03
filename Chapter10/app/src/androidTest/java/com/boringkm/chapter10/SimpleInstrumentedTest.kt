package com.boringkm.chapter10

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith

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

    @After
    fun finish() {
        println("finished")
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
        println(name.methodName)
    }

    @Test
    fun A에서_버튼을_누르면_B로_텍스트가_바뀐다() {
        rule.onNodeWithText("A").performClick()
        rule.onNodeWithText("B").assertExists().printToLog("A에서_버튼을_누르면_B로_텍스트가_바뀐다")
    }
}