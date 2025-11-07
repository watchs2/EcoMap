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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.PaddingValues


val CinzentoClaro = Color(0xFFEAEDEF)
val CinzentoEscuro = Color(0xFF37474F)
val Branco = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Green = Color(0xFF2E7C32)
val LightGreen = Color(0xFF80C683)
val Red = Color(0xFFEC0D0D)
val Azul = Color(0xFF0027FF)

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
    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda a tela
            .background(CinzentoClaro) //cinzento
            .padding(20.dp),
        contentAlignment = Alignment.Center // Centraliza o conteúdo
    ) {
        /*
         ------------Inicio Formulário--------------
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
            //Isto está repetido
            Column(
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp)) // Bordas arredondadas
                    .background(Branco) // Cor de fundo do card
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(5.dp))
                // Label para o campo de email
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
                // Label para o campo de senha
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
                Spacer(Modifier.height(28.dp)) // Espaço antes do botão

                // Botão de login
                Button(
                    onClick = { viewModel.createUserWithEmail(email.value, password.value) },
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


        /*
    ------------Inicio Signup--------------
   */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Don't have an account? ",
                fontSize = 14.sp,
                color = CinzentoEscuro
            )

            // Botão de "Sign Up" (cadastro)
            TextButton(
                onClick = { /* Lógica de cadastro */ },
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


@Composable
fun LogoSection(modifier: Modifier = Modifier) {
    // Coluna que centraliza o logo e o texto
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Caixa colorida representando o ícone ou logo do app
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {}
        Spacer(Modifier.height(20.dp)) // Espaço vertical

        // Nome do aplicativo
        Text(
            text = "EcoMap",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 20.dp)
        )

        // Subtítulo ou slogan
        Text(
            text = "Share and find eco-points",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}


@Composable
fun LoginForm(
    email: String,                         // Valor atual do campo de email
    onEmailChange: (String) -> Unit,       // Função chamada quando o email muda
    password: String,                      // Valor atual do campo de senha
    onPasswordChange: (String) -> Unit,    // Função chamada quando a senha muda
    modifier: Modifier = Modifier,         // Modificador externo
    viewModel: FirebaseViewModel           // ViewModel com a lógica de autenticação
) {
    // Coluna principal do formulário de login
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)) // Bordas arredondadas
            .background(MaterialTheme.colorScheme.onBackground) // Cor de fundo do card
            .shadow(4.dp, ambientColor = Color.Black.copy(alpha = 0.1f)) // Pequena sombra
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Label para o campo de email
        Text(
            text = "Email",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(4.dp))

        // Campo de texto para inserir o email
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            placeholder = { Text("your@email.com") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        // Label para o campo de senha
        Text(
            text = "Password",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(4.dp))

        // Campo de texto para senha com ocultação de caracteres
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            placeholder = { Text("••••••••") },
            visualTransformation = PasswordVisualTransformation(), // Oculta o texto digitado
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )

        Spacer(Modifier.height(28.dp)) // Espaço antes do botão

        // Botão de login
        Button(
            onClick = {
                viewModel.createUserWithEmail(
                    email,
                    password
                )
            }, // Chama o método do ViewModel
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(16.dp))

        // Link de "Esqueceu a senha?"
        TextButton(onClick = { /* Lógica de recuperação de senha */ }) {
            Text(
                "Forgot password?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // Espaço e link para criar uma nova conta
    Spacer(Modifier.height(28.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            "Don't have an account? ",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // Botão de "Sign Up" (cadastro)
        TextButton(
            onClick = { /* Lógica de cadastro */ },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(18.dp)
        ) {
            Text(
                "Sign Up",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/*
@Composable
fun LoginScreen(
    viewModel: FirebaseViewModel,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
){
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    LaunchedEffect(viewModel.user.value) {
        if(viewModel.user.value != null && viewModel.error.value == null){
            onSuccess()
        }
    }

    //ui
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (viewModel.error.value != null) {
            Text(
                text="Error: ${viewModel.error.value}",
                modifier = Modifier
                    .background(Color(255, 0, 0))
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        OutlinedTextField(
            value = email.value,
            onValueChange = {email.value = it},
            label = {Text(stringResource(R.string.email))},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = password.value,
            onValueChange = {password.value = it},
            label = {Text(stringResource(R.string.password))},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(onClick = {
                viewModel.createUserWithEmail(email.value, password.value)
            }) {
                Text(stringResource(R.string.register))
            }
            Button(onClick = {
                viewModel.signInWithEmail(email.value, password.value)
            }) {
                Text(stringResource(R.string.login))
            }
        }
    }
}

 */