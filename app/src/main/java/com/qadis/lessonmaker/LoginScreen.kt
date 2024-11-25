import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qadis.lessonmaker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val jostFont = FontFamily(
        Font(R.font.jost_bold)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .background(Color.White)
    ) {
        Text(
            text = "Explore, Learn & Succeed",
            fontSize = 22.sp,
            fontFamily = jostFont,
            color = Color(0xFF0F9E41),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Gray,
                    offset = Offset(10f, 10f),
                    blurRadius = 10f
                )
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Logo
            Image(
                painter = painterResource(R.drawable.biit_logo),
                contentDescription = "BIIT LOGO",
                modifier = Modifier
                    .width(172.dp)
                    .height(167.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Username field
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(text = "Username", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Person Icon",
                        tint = Color.Black,
                        modifier = Modifier.width(30.dp).height(20.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)  // Set fixed height for uniform size
                    .padding(horizontal = 16.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(60.dp))
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Password field
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(text = "Password", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Person Icon",
                        tint = Color.Black,
                        modifier = Modifier.width(30.dp).height(20.dp)

                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)  // Set fixed height for uniform size
                    .padding(horizontal = 16.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(60.dp))
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(35.dp))

            // Login button
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(45.dp)
                    .padding(horizontal = 16.dp)
                    .shadow(8.dp, shape = RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9E41))
            ) {
                Text(
                    text = "LOGIN",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot password
            ClickableText(
                text = AnnotatedString("Forgot Password?"),
                onClick = { /* Handle forgot password */ },
                style = LocalTextStyle.current.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.None,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}


