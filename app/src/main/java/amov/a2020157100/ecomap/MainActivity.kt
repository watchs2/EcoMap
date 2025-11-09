package amov.a2020157100.ecomap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import amov.a2020157100.ecomap.ui.theme.EcoMapTheme
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.screens.LoginScreen
import amov.a2020157100.ecomap.ui.screens.MainScreen
import amov.a2020157100.ecomap.ui.screens.RegisterScreen
import amov.a2020157100.ecomap.ui.screens.MapViewScreen



class MainActivity : ComponentActivity() {

    companion object {
        const val LOGIN_SCREEN = "Login"
        const val MAIN_SCREEN = "Main"
        const val REGISTER_SCREEN = "Register"
        const val MapViewScreen = "MapView"
    }

    private val viewModel : FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            EcoMapTheme {
                Surface{
                    NavHost(
                        navController = navController,
                        startDestination = LOGIN_SCREEN,
                    ){
                        composable(LOGIN_SCREEN) {
                            LoginScreen(
                                viewModel,
                                onSuccess = {
                                    navController.navigate(MapViewScreen) {
                                        //usa-se desta maneira caso se faça botao retroceder, obriga a fazer signout
                                        //em vez de ir para o login screen
                                        popUpTo(LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigationRegister ={
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
                                viewModel= viewModel,
                                onSignOut = {
                                    viewModel.signOut()
                                    navController.navigate(LOGIN_SCREEN){ popUpTo(MAIN_SCREEN) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(MapViewScreen) {
                            MapViewScreen()
                        }
                    }
                }
            }
        }
    }
}

