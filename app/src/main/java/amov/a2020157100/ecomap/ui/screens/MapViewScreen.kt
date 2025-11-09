package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp


val MapColor = Color(0xFFD2EAD3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen(
    modifier: Modifier = Modifier
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
                }
                /*
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco,
                    titleContentColor = Color.Black
                ),
                */


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

            BottomAppBar(
                containerColor = Branco,
                contentPadding = PaddingValues(0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ){

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = Green
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp), // Adiciona um padding vertical na linha de botões
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        BottomNavItem(
                            icon = Icons.Filled.Map,
                            label = "Map",
                            isSelected = true,

                        )

                        BottomNavItem(
                            icon = Icons.Filled.FormatListNumbered,
                            label = "List",
                            isSelected = false,

                        )

                        BottomNavItem(
                            icon = Icons.Filled.Person,
                            label = "Profile",
                            isSelected = false,
                        )
                    }
                }
            }
        }


    )


}
@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
) {

    val contentColor = if (isSelected) Green else Color.Gray
    val backgroundColor = if (isSelected) Color.White else Color.Transparent

    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max) 
            .height(IntrinsicSize.Max) 
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}


















