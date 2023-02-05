# 01장 Compose App 첫 빌드

### 환영 인사 나타내기
- xml에 있는 ui가 아닌 compose.material3에 있는 UI 객체 사용한다.
- compose.ui.res.stringResource() 사용해서 strings.xml의 값 참조한다.

### 열, 텍스트 필드, 버튼 사용
- **Row()**: 가로로 UI 배치한다.
- **Modifier(변경자)**: Composable 함수의 외형과 행위에 영향을 준다. -> 3장에 이어서
- **padding**: 원하는 dp(density-independent pixels) 만큼 공간 생성
- **MutableState**: 변경할 수 있는 값, MutableState 타입의 값을 변경하면 컴포저블 함수는 다시 그려지거나 다시 채색된다.
