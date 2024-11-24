package com.qadis.lessonmaker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Preview(showSystemUi = true)
@Composable
fun SplashScreen() {
    val jostFont = FontFamily(
        Font(R.font.jost_bold)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F9E41)),
        contentAlignment = Alignment.Center,
    ) {


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = (-40).dp)
        ) {


            Image(
                painter = painterResource(R.drawable.biit_logo),
                contentDescription = "BIIT LOGO",
                modifier = Modifier
                    .width(191.dp)
                    .height(185.dp)
                    .shadow(100.dp, shape = RoundedCornerShape(100.dp)),
                )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Explore, Learn & Succeed",
                fontSize = 25.sp,
                fontFamily = jostFont,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(10f, 10f),
                        blurRadius = 20f
                    )
                )
            )
        }
    }
}



@Composable
fun Delay()
{
    val showSplashScreen = remember { true }
    LaunchedEffect(showSplashScreen) {
        delay(300)
    }
    if(showSplashScreen)
    {
        SplashScreen()
    }
    else
    {
        LoginScreen()
    }
}