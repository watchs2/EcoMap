package amov.a2020157100.ecomap.ui

import android.content.pm.PackageManager
import amov.a2020157100.ecomap.EcoMap
import androidx.activity.result.contract.ActivityResultContracts
import amov.a2020157100.ecomap.ui.screens.ListViewScreen
import amov.a2020157100.ecomap.ui.screens.LoginScreen
import amov.a2020157100.ecomap.ui.screens.MainScreen
import amov.a2020157100.ecomap.ui.screens.MapViewScreen
import amov.a2020157100.ecomap.ui.screens.ProfileScreen
import amov.a2020157100.ecomap.ui.screens.RegisterScreen
import amov.a2020157100.ecomap.ui.screens.AddEcopontoScreen
import amov.a2020157100.ecomap.ui.screens.EcopontoDetails
import amov.a2020157100.ecomap.ui.theme.EcoMapTheme
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModelFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

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
        const val PROFILE_SCREEN = "Profile"
        const val ADDECOPONTO_SCREEN = "AddEcoponto"

        const val DETAIL_SCREEN = "Detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            EcoMapTheme {
                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = LOGIN_SCREEN,
                    ) {
                        composable(LOGIN_SCREEN) {
                            LoginScreen(
                                viewModel,
                                onSuccess = {
                                    navController.navigate(MAPVIEW_SCREEN) {
                                        //usa-se desta maneira caso se faça botao retroceder, obriga a fazer signout
                                        //em vez de ir para o login screen
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
                            ListViewScreen(viewModel,navController)
                        }
                        composable(PROFILE_SCREEN) {
                            ProfileScreen(navController)
                        }
                        composable(ADDECOPONTO_SCREEN) {
                            AddEcopontoScreen(viewModel,locationViewModel,navController, )
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
                                    viewModel = viewModel,
                                    navController = navController,
                                    recyclingPointId = recyclingPointId
                                )
                            } else {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
        //Permissões

        /*
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askSinglePermissionCamera.launch(android.Manifest.permission.CAMERA)
        }

         */
        verifyLocationPermissions()

    }

    private val askSinglePermissionCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        /* TODO */
       // verifyLocationPermissions()
    }

    fun verifyLocationPermissions() {
        locationViewModel.hasLocationPermission = (
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
        if (!locationViewModel.hasLocationPermission) {
            askLocationPermissions.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }

    }

    private val askLocationPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        locationViewModel.hasLocationPermission =
            map[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                    map[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
    }
}