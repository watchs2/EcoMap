package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.theme.TextDarkGray
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


/*
val CinzentoClaro = Color(0xFFEAEDEF)
val CinzentoEscuro = Color(0xFF37474F)
val Branco = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Green = Color(0xFF2E7C32)
val LightGreen = Color(0xFF80C683)
val Red = Color(0xFFD32F2F)
*/

@Composable
fun LoginScreen(
    viewModel: FirebaseViewModel,
    onSuccess: () -> Unit,
    onNavigationRegister: () -> Unit,
    modifier: Modifier = Modifier
) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    LaunchedEffect(viewModel.user.value) {
        if (viewModel.user.value != null && viewModel.error.value == null) {
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (isLandscape) Arrangement.Top else Arrangement.Center
    ) {

        Spacer(Modifier.height(if (isLandscape) 10.dp else 0.dp))

        Surface(
            modifier = Modifier
                .widthIn(max = 700.dp)
                .heightIn(max = if(isLandscape) 320.dp else 400.dp)
                .clip(RoundedCornerShape(20.dp))
                .shadow(
                    20.dp,
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                ),
            color = MaterialTheme.colorScheme.onPrimary
        ) {
            Column(
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título ou Logótipo (Opcional - podes esconder em Landscape se quiseres poupar espaço)
                /* if (!isLandscape) {
                     Text("EcoMap", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Green)
                     Spacer(Modifier.height(10.dp))
                } */

                Spacer(Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.label_email),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = viewModel.loginEmail.value,
                    onValueChange = { viewModel.loginEmail.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text(stringResource(R.string.placeholder_email)) }, // Corrigido para Text()
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor =  MaterialTheme.colorScheme.secondary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.label_password),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = viewModel.loginPassword.value,
                    onValueChange = { viewModel.loginPassword.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text(stringResource(R.string.placeholder_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor =MaterialTheme.colorScheme.onPrimary
                    ),
                    singleLine = true
                )

                if (viewModel.error.value != null) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = viewModel.error.value.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(5.dp))
                } else {
                    Spacer(Modifier.height(20.dp))
                }

                Button(
                    onClick = {
                        viewModel.signInWithEmail(
                            viewModel.loginEmail.value,
                            viewModel.loginPassword.value
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        stringResource(R.string.btn_sign_in),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(5.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.text_no_account),
                fontSize = 14.sp,
                color = TextDarkGray
            )
            TextButton(
                onClick = { onNavigationRegister() },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(18.dp)
            ) {
                Text(
                    stringResource(R.string.btn_sign_up),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(50.dp))
    }
}