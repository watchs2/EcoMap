package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
                //Esta a fazer assincronamente
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