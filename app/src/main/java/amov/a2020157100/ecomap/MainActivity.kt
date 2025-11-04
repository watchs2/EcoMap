package amov.a2020157100.ecomap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import amov.a2020157100.ecomap.ui.theme.EcoMapTheme
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost


class MainActivity : ComponentActivity() {

    companion object {
        const val LOGIN_SCREEN = "Login"
        const val MAIN_SCREEN = "Main"
    }

    private val viewModel : FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoMapTheme {
                Surface{
                    NavHost(

                    )
                }
            }
        }
    }
}

