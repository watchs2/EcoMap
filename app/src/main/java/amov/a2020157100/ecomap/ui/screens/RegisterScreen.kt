package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(
    viewModel: FirebaseViewModel,
    onSuccess: () -> Unit,
    onNavigationLogin: () -> Unit,
    modifier: Modifier = Modifier
){
    // 1. Detetar Orientação
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Removemos os 'remember' locais. Usamos o ViewModel.

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
            .padding(20.dp)
            .verticalScroll(rememberScrollState()), // 2. Scroll para Landscape
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (isLandscape) Arrangement.Top else Arrangement.Center
    ){

        // Espaço dinâmico no topo
        Spacer(Modifier.height(if(isLandscape) 10.dp else 0.dp))

        Surface(
            modifier = Modifier
                .widthIn(max = 700.dp)
                // Ajuste de altura máxima para landscape
                .heightIn(max = if(isLandscape) 360.dp else 550.dp)
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
                    .padding(28.dp)
                    .verticalScroll(rememberScrollState()), // Scroll interno no cartão
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(Modifier.height(5.dp))

                // --- EMAIL ---
                Text(
                    text = stringResource(R.string.label_email),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = viewModel.registerEmail.value, // Ligar ao ViewModel
                    onValueChange = { viewModel.registerEmail.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text(stringResource(R.string.placeholder_email)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = LightGreen,
                        cursorColor = Green,
                        focusedContainerColor = Branco,
                        unfocusedContainerColor = Branco
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(20.dp))

                // --- PASSWORD ---
                Text(
                    text = stringResource(R.string.label_password),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = viewModel.registerPassword.value, // Ligar ao ViewModel
                    onValueChange = { viewModel.registerPassword.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text(stringResource(R.string.placeholder_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = LightGreen,
                        cursorColor = Green,
                        focusedContainerColor = Branco,
                        unfocusedContainerColor = Branco
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(20.dp))

                // --- CONFIRM PASSWORD ---
                Text(
                    text= stringResource(R.string.label_confirm_password),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = viewModel.registerConfirmPassword.value, // Ligar ao ViewModel
                    onValueChange = {viewModel.registerConfirmPassword.value = it},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text(stringResource(R.string.placeholder_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = LightGreen,
                        cursorColor = Green,
                        focusedContainerColor = Branco,
                        unfocusedContainerColor = Branco
                    ),
                    singleLine = true
                )

                // --- ERROS ---
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

                // --- BOTÃO REGISTAR ---
                Button(
                    onClick = {
                        viewModel.createUserWithEmail(
                            viewModel.registerEmail.value,
                            viewModel.registerPassword.value,
                            viewModel.registerConfirmPassword.value
                        )
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

        // --- RODAPÉ LOGIN ---
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.text_have_account),
                fontSize = 14.sp,
                color = CinzentoEscuro
            )

            TextButton(
                onClick = { onNavigationLogin() },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(18.dp)
            ) {
                Text(
                    stringResource(R.string.btn_sign_in),
                    color = Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Espaço extra para garantir scroll total
        Spacer(Modifier.height(50.dp))
    }
}