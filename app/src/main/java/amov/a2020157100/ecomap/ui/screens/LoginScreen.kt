package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import amov.a2020157100.ecomap.R
import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.PaddingValues



/*
    Cores mas vão sair daqui
 */
val CinzentoClaro = Color(0xFFEAEDEF)
val CinzentoEscuro = Color(0xFF37474F)
val Branco = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Green = Color(0xFF2E7C32)
val LightGreen = Color(0xFF80C683)
val Red = Color(0xFFD22F2F)


@Composable
fun LoginScreen(
    viewModel: FirebaseViewModel,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    LaunchedEffect(viewModel.user.value) {
        if (viewModel.user.value != null && viewModel.error.value == null) {
            onSuccess()
        }
    }

    /*
     ------------Inicio Main--------------
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CinzentoClaro)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        /*
         ------------Inicio Formulário (Surface)--------------
        */
        Surface(
            modifier = Modifier
                .widthIn(max = 700.dp)
                .heightIn(max = 400.dp)
                .clip(RoundedCornerShape(20.dp))
                .shadow(
                    20.dp,
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                ),
            color = Branco
        ) {

            Column(
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Branco)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("your@email.com") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = LightGreen,
                        cursorColor = Green
                    ),
                    singleLine = true
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("••••••••") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = LightGreen,
                        cursorColor = Green
                    ),
                    singleLine = true
                )
                if(viewModel.error.value != null){
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = viewModel.error.value.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Red,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(5.dp))
                }else{
                    Spacer(Modifier.height(20.dp))
                }
                Button(
                    onClick = { viewModel.signInWithEmail(email.value, password.value) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green
                    )
                ) {
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(5.dp))
            }

        }
        /*
         ------------Fim Formulário--------------
        */

        Spacer(Modifier.height(20.dp))

        /*
    ------------Inicio Signup--------------
        */
        Row(

            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Don't have an account? ",
                fontSize = 14.sp,
                color = CinzentoEscuro
            )

            TextButton(
                onClick = {  },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(18.dp)
            ) {
                Text(
                    "Sign Up",
                    color = Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        /*
------------Fim Signup--------------
*/

    }
    /*
 ------------Fim Main--------------
 */
}