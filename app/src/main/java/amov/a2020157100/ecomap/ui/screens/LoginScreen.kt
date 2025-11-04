package amov.a2020157100.ecomap.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


@Composable
fun LoginScreen(
    viewModel: FirebaseViewModel,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
){
    val userEmail = remember { mutableStateOf("") }
    val userPassword = remember { mutableStateOf("") }

}