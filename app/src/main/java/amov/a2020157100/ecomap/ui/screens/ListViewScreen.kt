package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.ui.MainActivity
import amov.a2020157100.ecomap.ui.composables.AppBottomBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewScreen(
    navController: NavHostController,
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
                            text = "Nearby",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Green,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )


                        /*
                        // Linha Verde que ocupa toda a largura
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 2.dp,
                            color = Green
                        )
                        */

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco,
                    titleContentColor = Color.Black
                ),



                )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(CinzentoClaro)
                    .fillMaxSize()

            )

        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp) // espaçamento entre eles
            ) {

                FloatingActionButton(
                    containerColor = Green,
                    onClick = { navController.navigate(MainActivity.ADDECOPONTO_SCREEN) }
                ) {
                    Icon(Icons.Filled.Add, tint = Branco, contentDescription = "Add")
                }
            }
        },
        bottomBar = {
            AppBottomBar(navController = navController)
        }


    )


}
