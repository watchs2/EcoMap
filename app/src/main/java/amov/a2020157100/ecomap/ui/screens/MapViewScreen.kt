package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.ui.MainActivity
import amov.a2020157100.ecomap.ui.composables.AppBottomBar
import amov.a2020157100.ecomap.ui.composables.Map
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

//TODO Cores tem de sair daqui
val MapColor = Color(0xFFD2EAD3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen(
    firebaseViewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // Detetar Landscape para ajustes finos (como remover a sombra)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EcoMap",
                        style = MaterialTheme.typography.headlineMedium, // Título maior e destacado
                        color = Green,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco,
                    titleContentColor = Color.Black
                ),
                // Em landscape, removemos a sombra para ganhar espaço visual vertical e parecer mais limpo
                modifier = Modifier.shadow(if (isLandscape) 0.dp else 4.dp)
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MapColor)
                    .fillMaxSize()
            ){
                Map(firebaseViewModel, locationViewModel)
            }
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp) // espaçamento entre os botões
            ) {
                FloatingActionButton(
                    containerColor = Branco,
                    onClick = { /* Todo: Focar na localização do utilizador */ }
                ) {
                    Icon(Icons.Filled.Navigation, tint = Green, contentDescription = "Me")
                }
                FloatingActionButton(
                    containerColor = Green,
                    onClick = { navController.navigate(MainActivity.ADDECOPONTO_SCREEN) }
                ) {
                    Icon(Icons.Filled.Add, tint = Branco, contentDescription = "Add")
                }
            }
        },
        bottomBar = {
            AppBottomBar(navController, onSignOut = {firebaseViewModel.signOut()})
        }
    )
}