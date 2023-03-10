package com.example.chapter06.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.chapter06.BuildConfig
import com.example.chapter06.R
import com.example.chapter06.navigation.Screens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) = Box(Modifier.fillMaxSize()) {

//    val scale = remember {
//        androidx.compose.animation.core.Animatable(0.0f)
//    }
//
//    LaunchedEffect(key1 = true) {
//        scale.animateTo(
//            targetValue = 0.7f,
//            animationSpec = tween(800, easing = {
//                OvershootInterpolator(4f).getInterpolation(it)
//            })
//        )
//        delay(1000)
//        navController.navigate(Screens.home)
//    }
//
//    Image(
//        painter = painterResource(id = R.drawable.ic_launcher_foreground),
//        contentDescription = "",
//        alignment = Alignment.Center, modifier = Modifier
//            .fillMaxSize()
//            .padding(40.dp)
//            .scale(scale.value)
//            .background(color = colorResource(id = R.color.android_green_dark))
//    )
//
//    Text(
//        text = "Version - ${BuildConfig.VERSION_NAME}",
//        textAlign = TextAlign.Center,
//        fontSize = 24.sp,
//        modifier = Modifier
//            .align(Alignment.BottomCenter)
//            .padding(16.dp)
//    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.black)),
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logo))
        val logoAnimationState =
            animateLottieCompositionAsState(composition = composition)
        LottieAnimation(
            composition = composition,
            progress = { logoAnimationState.progress }
        )
        if (logoAnimationState.isAtEnd && logoAnimationState.isPlaying) {
            navController.navigate(Screens.home)
        }
    }

}