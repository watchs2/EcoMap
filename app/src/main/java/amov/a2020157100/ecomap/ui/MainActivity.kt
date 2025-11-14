package amov.a2020157100.ecomap.ui

import amov.a2020157100.ecomap.ui.screens.ListViewScreen
import amov.a2020157100.ecomap.ui.screens.LoginScreen
import amov.a2020157100.ecomap.ui.screens.MainScreen
import amov.a2020157100.ecomap.ui.screens.MapViewScreen
import amov.a2020157100.ecomap.ui.screens.ProfileScreen
import amov.a2020157100.ecomap.ui.screens.RegisterScreen
import amov.a2020157100.ecomap.ui.screens.AddEcopontoScreen
import amov.a2020157100.ecomap.ui.theme.EcoMapTheme
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    companion object {
        const val LOGIN_SCREEN = "Login"
        const val MAIN_SCREEN = "Main"
        const val REGISTER_SCREEN = "Register"
        const val MAPVIEW_SCREEN = "MapView"
        const val LISTVIEW_SCREEN = "ListView"
        const val PROFILE_SCREEN = "Profile"
        const val ADDECOPONTO_SCREEN = "AddEcoponto"
    }

    private val viewModel : FirebaseViewModel by viewModels()

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
                                        //usa-se desta maneira caso se faça botao retroceder, obriga a fazer signout
                                        //em vez de ir para o login screen
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
                            MapViewScreen(navController)
                        }
                        composable(LISTVIEW_SCREEN) {
                            ListViewScreen(navController)
                        }
                        composable(PROFILE_SCREEN) {
                            ProfileScreen(navController)
                        }
                        composable(ADDECOPONTO_SCREEN) {
                            AddEcopontoScreen(navController)
                        }
                    }
                }
            }
        }
    }
}