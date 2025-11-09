package amov.a2020157100.ecomap.ui.screens


import amov.a2020157100.ecomap.ui.composables.AppBottomBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


val MapColor = Color(0xFFD2EAD3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen(
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
                            text = "EcoMap",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Green,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextField(
                                value = location.value,
                                onValueChange = { location.value = it },
                                placeholder = { Text("Search location...") },
                                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Green,
                                    unfocusedBorderColor = Green.copy(alpha = 0.5f),
                                    focusedContainerColor = Branco,
                                    unfocusedContainerColor = Branco,
                                    cursorColor = Green
                                )
                            )

                        }
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
                    .background(MapColor)
                    .fillMaxSize()

            )

        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp) // espaçamento entre eles
            ) {

                FloatingActionButton(
                    containerColor = Branco,
                    onClick = { /* Todo */ }
                ) {
                    Icon(Icons.Filled.Navigation, tint = Green, contentDescription = "Me")
                }
                FloatingActionButton(
                    containerColor = Green,
                    onClick = { /* Todo */ }
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













