package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amov.a2020157100.ecomap.R


@Composable
fun RegisterScreen(
    viewModel: FirebaseViewModel,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
){
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordConfirm = remember { mutableStateOf("") }

    LaunchedEffect(viewModel.user.value) {
        if (viewModel.user.value != null && viewModel.error.value == null) {
            onSuccess()
        }
    }

    //UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CinzentoClaro)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ){
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
        ){
            Column(
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Branco)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.label_email),
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
                    placeholder = {stringResource(R.string.placeholder_email)},
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = LightGreen,
                        cursorColor = Green
                    ),
                    singleLine = true
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.label_password),
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
                    placeholder = { stringResource(R.string.placeholder_password)},
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = LightGreen,
                        cursorColor = Green
                    ),
                    singleLine = true
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text= stringResource(R.string.label_confirm_password),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = passwordConfirm.value,
                    onValueChange = {passwordConfirm.value = it},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { stringResource(R.string.placeholder_password) },
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
                    onClick = {
                            viewModel.createUserWithEmail(email.value,password.value, passwordConfirm.value)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green
                    )
                ) {
                    Text(stringResource(R.string.btn_sign_up), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(5.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(

            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.text_have_account),
                fontSize = 14.sp,
                color = CinzentoEscuro
            )

            TextButton(
                onClick = {  },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(18.dp)
            ) {
                Text(
                    stringResource(R.string.btn_sign_in),
                    color = Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }



}