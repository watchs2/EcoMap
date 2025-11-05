package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: FirebaseViewModel,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("EcoMap") },
                actions = {
                    TextButton(onClick = { onSignOut() }) {
                        Text("Sign Out")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Conteúdo principal da tela
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Text(
                text = "Bem-vindo ao EcoMap!",
                modifier = Modifier.padding(16.dp),
                color = Color.Black
            )
        }
    }
}
