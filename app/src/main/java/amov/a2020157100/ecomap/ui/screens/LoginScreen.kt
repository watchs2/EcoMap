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

// Mantém as tuas cores
val CinzentoClaro = Color(0xFFEAEDEF)
val CinzentoEscuro = Color(0xFF37474F)
val Branco = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Green = Color(0xFF2E7C32)
val LightGreen = Color(0xFF80C683)
val Red = Color(0xFFD32F2F)

@Composable
fun LoginScreen(
    viewModel: FirebaseViewModel,
    onSuccess: () -> Unit,
    onNavigationRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Detetar a orientação do ecrã
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Nota: Removemos os 'remember' locais. Agora usamos o ViewModel.

    LaunchedEffect(viewModel.user.value) {
        if (viewModel.user.value != null && viewModel.error.value == null) {
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CinzentoClaro)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()), // 2. Scroll para evitar cortes em Landscape/Teclado
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (isLandscape) Arrangement.Top else Arrangement.Center // Em landscape, começa do topo
    ) {

        // Em Landscape, reduzimos o espaço no topo para caber tudo
        Spacer(Modifier.height(if (isLandscape) 10.dp else 0.dp))

        Surface(
            modifier = Modifier
                .widthIn(max = 700.dp)
                // Se estiver em landscape, damos menos altura máxima para não ocupar o ecrã todo verticalmente
                .heightIn(max = if(isLandscape) 320.dp else 400.dp)
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
                    .padding(28.dp)
                    // Adicionar scroll interno no cartão também é boa prática para ecrãs muito pequenos
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
                    color = Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))

                // 3. LIGAÇÃO AO VIEWMODEL (Persistência)
                OutlinedTextField(
                    value = viewModel.loginEmail.value, // Lê do ViewModel
                    onValueChange = { viewModel.loginEmail.value = it }, // Escreve no ViewModel
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text(stringResource(R.string.placeholder_email)) }, // Corrigido para Text()
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

                Text(
                    text = stringResource(R.string.label_password),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))

                // 3. LIGAÇÃO AO VIEWMODEL
                OutlinedTextField(
                    value = viewModel.loginPassword.value, // Lê do ViewModel
                    onValueChange = { viewModel.loginPassword.value = it }, // Escreve no ViewModel
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text(stringResource(R.string.placeholder_password)) }, // Corrigido
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

                // Mensagem de Erro
                if (viewModel.error.value != null) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = viewModel.error.value.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Red,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(5.dp))
                } else {
                    Spacer(Modifier.height(20.dp))
                }

                Button(
                    // Passamos os valores atuais do ViewModel para a função de login
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
                        containerColor = Green
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
                color = CinzentoEscuro
            )
            TextButton(
                onClick = { onNavigationRegister() },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(18.dp) // Ajuste fino para alinhar texto
            ) {
                Text(
                    stringResource(R.string.btn_sign_up),
                    color = Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Espaço extra no fundo para garantir que conseguimos fazer scroll até ao fim
        Spacer(Modifier.height(50.dp))
    }
}