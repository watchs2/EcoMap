package amov.a2020157100.ecomap.ui.screens

import androidx.compose.material3.MaterialTheme

// watchs2/ecomap/EcoMap-4fec592fcaaed0ce2dbbb9c086a15175e9c99d8f/app/src/main/java/amov/a2020157100/ecomap/ui/screens/ProfileScreen.kt


import amov.a2020157100.ecomap.ui.composables.AppBottomBar
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel // Import adicionado
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton // Import adicionado
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    //location
    val location = remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Branco)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Profile",
                            color = Green,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco,
                    titleContentColor = Color.Black
                ),
                actions = { // Ações adicionadas para o Sign Out
                    TextButton(onClick = { onSignOut() }) {
                        Text("Sign Out", color = Green)
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(CinzentoClaro)
                    .fillMaxSize()

            ){
                // Conteúdo do ecrã de perfil aqui
            }

        },
        bottomBar = {
            AppBottomBar(navController = navController)
        }
    )
}