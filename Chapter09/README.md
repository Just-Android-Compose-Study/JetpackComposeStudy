# 09. 상호 운용 API 자세히 알아보기

## 컴포즈 앱에서 뷰 나타내기

### 컴포즈 앱에 커스텀 컴포넌트 추가

QRCode 스캔하는 ZXing 라이브러리를 AndroidView를 이용해 ComposeApp에서 사용해보는 예제

```kotlin
class ZxingDemoActivity : ComponentActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView

    private val text = MutableLiveData("")

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                barcodeView.resume()
            }
        }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1: layout.xml inflate
        val root = layoutInflater.inflate(R.layout.layout, null)

        // 2: barcodeView 초기화
        barcodeView = root.findViewById(R.id.barcode_scanner)
        val formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.initializeFromIntent(intent)
        val callback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if (result.text == null || result.text == text.value) {
                    return
                }
                text.value = result.text
            }
        }

        // 3: 지속적인 스캔 실시
        barcodeView.decodeContinuous(callback)

        setContent {
            val state = text.observeAsState()
            state.value?.let {
                ZxingDemo(root, it)
            }
        }
    }
}

@Composable
fun ZxingDemo(root: View, value: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        AndroidView(modifier = Modifier.fillMaxSize(),
            factory = {
                root
            })
        if (value.isNotBlank()) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.h4
            )
        }
    }
}
```

- factory 블록은 구성될 뷰를 얻기 위해 정확히 한 번만 호출된다. 
  항상 UI 스레드에서 호출될 것이기 때문에 필요에 따라 View property 설정이 가능하다.

### AndroidViewBinding()으로 View Layer 구조 Inflating

- ComposeActivity에서 ViewActivity
- Compose여도 xml 파일과 ViewBinding 

```kotlin
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
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.google.android.material.textview.MaterialTextView
        android:id="@+id/textView"
        android:layout_width="0dp"
        android:layout_height="64dp"
        android:background="?colorSecondary"
        android:gravity="center"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.google.android.material.button.MaterialButton
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="@string/view_activity"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textView" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- 반대로 ViewActivity에서는 기존 코드처럼 ViewBinding을 사용하고 ComposeView를 사용해 Composable 함수를 불러온다.

```kotlin
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
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    tools:context=".ViewActivity">

    <com.google.android.material.slider.Slider
        android:id="@+id/slider"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.compose.ui.platform.ComposeView
        android:id="@+id/compose_view"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/slider" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

## 뷰와 컴포저블 함수 간 데이터 공유

## 뷰 계층 구조에 컴포저블 임베디드