package amov.a2020157100.ecomap.ui

import android.Manifest
import android.content.pm.PackageManager
import amov.a2020157100.ecomap.EcoMap
import androidx.activity.result.contract.ActivityResultContracts
import amov.a2020157100.ecomap.ui.screens.*
import amov.a2020157100.ecomap.ui.theme.EcoMapTheme
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModelFactory
import android.os.Bundle
import androidx.compose.runtime.remember
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.navArgument
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.SnackbarHost
import amov.a2020157100.ecomap.ui.theme.StatusError
import androidx.compose.material3.Snackbar
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    private val app by lazy{
        application as EcoMap
    }

    private val viewModel : FirebaseViewModel by viewModels()

    private val locationViewModel : LocationViewModel by viewModels {
        LocationViewModelFactory(app.locationHandler)
    }

    companion object {
        const val LOGIN_SCREEN = "Login"
        const val MAIN_SCREEN = "Main"
        const val REGISTER_SCREEN = "Register"
        const val MAPVIEW_SCREEN = "MapView"
        const val LISTVIEW_SCREEN = "ListView"
        const val ADDECOPONTO_SCREEN = "AddEcoponto"
        const val DETAIL_SCREEN = "Detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        verifyPermissions()

        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            val context = LocalContext.current
            val currentError = viewModel.error.value
            var errorMessage: String? = null
            currentError?.let { uiText ->
                errorMessage = uiText.asString(context)
            }

            LaunchedEffect(errorMessage) {
                if (errorMessage != null) {
                    snackbarHostState.showSnackbar(
                        message = errorMessage,
                        withDismissAction = true
                    )
                    viewModel.clearError()
                }
            }

            EcoMapTheme {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = StatusError,
                                contentColor = androidx.compose.ui.graphics.Color.White,
                                actionColor = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                ){innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = LOGIN_SCREEN,
                        ) {
                            composable(LOGIN_SCREEN) {
                                LoginScreen(
                                    viewModel,
                                    onSuccess = {
                                        navController.navigate(MAPVIEW_SCREEN) {
                                            popUpTo(LOGIN_SCREEN) { inclusive = true }
                                        }
                                    },
                                    onNavigationRegister = {
                                        navController.navigate(REGISTER_SCREEN)
                                    }
                                )
                            }
                            composable(REGISTER_SCREEN) {
                                RegisterScreen(
                                    viewModel,
                                    onSuccess = {
                                        navController.navigate(LOGIN_SCREEN) {
                                            popUpTo(REGISTER_SCREEN) { inclusive = true }
                                        }
                                    },
                                    onNavigationLogin = {
                                        navController.navigate(LOGIN_SCREEN)
                                    }
                                )
                            }
                            composable(MAIN_SCREEN) {
                                MainScreen(
                                    viewModel = viewModel,
                                    onSignOut = {
                                        viewModel.signOut()
                                        navController.navigate(LOGIN_SCREEN) {
                                            popUpTo(MAIN_SCREEN) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(MAPVIEW_SCREEN) {
                                MapViewScreen(viewModel ,locationViewModel,navController)
                            }
                            composable(LISTVIEW_SCREEN) {
                                ListViewScreen(viewModel,locationViewModel,navController)
                            }
                            composable(ADDECOPONTO_SCREEN) {
                                AddEcopontoScreen(viewModel,locationViewModel,navController)
                            }
                            composable(
                                route="$DETAIL_SCREEN/{recyclingPointId}",
                                arguments = listOf(navArgument("recyclingPointId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val recyclingPointId =
                                    backStackEntry.arguments?.getString("recyclingPointId")
                                if (recyclingPointId != null) {
                                    EcopontoDetails(
                                        viewModel,
                                        locationViewModel,
                                        navController,
                                        recyclingPointId
                                    )
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (locationViewModel.hasLocationPermission) {
            locationViewModel.startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        locationViewModel.stopLocationUpdates()
    }

    private fun verifyPermissions() {
        val permissionsToRequest = ArrayList<String>()


        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }


        val coarseGranted = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val fineGranted = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


        locationViewModel.hasLocationPermission = coarseGranted || fineGranted


        if (!coarseGranted && !fineGranted) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else if (!fineGranted) {

            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }


        if (permissionsToRequest.isNotEmpty()) {
            askMultiplePermissions.launch(permissionsToRequest.toTypedArray())
        }
    }

    private val askMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->

        val coarse = results[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val fine = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        locationViewModel.hasLocationPermission = coarse || fine

        if (locationViewModel.hasLocationPermission) {
            locationViewModel.startLocationUpdates()
        }
    }
}