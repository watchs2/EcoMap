package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.MainActivity
import amov.a2020157100.ecomap.ui.composables.AppBottomBar
import amov.a2020157100.ecomap.ui.composables.EcoMapTopBar
import amov.a2020157100.ecomap.ui.composables.Map
import amov.a2020157100.ecomap.ui.theme.BackgroundMap
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen(
    firebaseViewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        topBar = {
            EcoMapTopBar(
                title = "EcoMap",
                showBackButton = false
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(BackgroundMap)
                    .fillMaxSize()
            ){
                Map(firebaseViewModel, locationViewModel)
            }
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate(MainActivity.ADDECOPONTO_SCREEN) }
                ) {
                    Icon(Icons.Filled.Add, tint = MaterialTheme.colorScheme.onPrimary, contentDescription = stringResource(
                        R.string.cd_add_button))
                }
            }
        },
        bottomBar = {
            AppBottomBar(navController, onSignOut = {
                firebaseViewModel.signOut()
                navController.navigate(MainActivity.LOGIN_SCREEN) {
                    popUpTo(0)
                }})
        }
    )
}