package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.MainActivity
import amov.a2020157100.ecomap.ui.theme.GreenLimeLight
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource





val blueBinColor = Color(0xFF2196F3)
val greenBinColor = Color(0xFF4CAF50)
val yellowBinColor = Color(0xFFFFEB3B)
val redBinColor = Color(0xFFC0172F)
val blackBinColor = Color(0xFF1A1A19)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEcopontoScreen(
    viewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
){


    val latitude = remember { mutableStateOf<Double>(0.0) }
    val longitude= remember { mutableStateOf<Double>(0.0) }

    val type = remember { mutableStateOf("") }
    val picture= remember { mutableStateOf("") }
    val observacoes= remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Mantive a sua estrutura original para o título
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Branco) // Use a sua cor definida
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "New Recycling point",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Black, // Use a sua cor definida
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco, // Use a sua cor definida
                    titleContentColor = Color.Black // Pode usar 'Black' se estiver definida
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },

        content = {   paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(CinzentoClaro)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ){
                item{
                    EcoPointTypeSection(
                        selectedType = type.value,
                        onTypeSelected = { type.value = it }
                    )
                }
                item{
                    LocationSection(locationViewModel,longitude,latitude)
                }
                item {
                    PhotoSection()
                }
                item{
                    NotesSection(observacoes)
                }
                item{
                    InfoSection()
                }
                item {
                    if (viewModel.error.value != null) {
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = viewModel.error.value.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = Red,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(5.dp))
                    }
                }
                item{
                    Button(
                        modifier= Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = {
                            viewModel.addRecyclingPoint(
                                    type.value,
                                    latitude.value,
                                longitude.value,
                                null,
                                observacoes.value,
                                onSuccess = {
                                    navController.navigate(MainActivity.MAPVIEW_SCREEN)
                                }
                            )
                        } ,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green,
                            contentColor = Branco
                        )

                    ) {
                        Text(
                            "Add Ecoponto",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }


        },
    )


}

@Composable
private fun MyTtitle(title: String){
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun LocationSection(
    locationViewModel: LocationViewModel,
    longitude :  MutableState<Double>,
    latitude :  MutableState<Double>
){

    val localization = remember { mutableStateOf("") }
    val currentLocation = locationViewModel.currentLocation.value


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)

    ){

        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle("Location *")

            OutlinedTextField(

                value = if (localization.value.isEmpty()) "" else localization.value,
                onValueChange = { /* Não fazer nada */ },
                readOnly = true,
                placeholder = { Text("") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.location2),
                        contentDescription = "Pin de Localização",
                        tint = Green,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = CinzentoClaro,
                    unfocusedContainerColor = CinzentoClaro,
                    cursorColor = Color.Transparent,

                    focusedTextColor = Black,
                    unfocusedTextColor = Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if(currentLocation !=null ){
                        latitude.value = currentLocation.latitude
                        longitude.value = currentLocation.longitude;

                        val lat = currentLocation.latitude
                        val lon = currentLocation.longitude

                        val latDir = if (lat >= 0) "N" else "S"
                        val lonDir = if (lon >= 0) "E" else "W"
                        localization.value = "${currentLocation.latitude}° ${latDir}, ${currentLocation.longitude}° ${lonDir}"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = Branco
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.compass),
                        contentDescription = "Usar Localização Atual",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text="Use Current Location", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun EcoPointTypeSection(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val types = mapOf(
        "Blue bin" to blueBinColor,
        "Green bin" to greenBinColor,
        "Yellow bin" to yellowBinColor,
        "Red bin" to redBinColor,
        "Black bin" to blackBinColor
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle("EcoPonto Type *")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.entries.take(3).forEach { (name, color) ->
                    TypeChipManual(
                        name = name,
                        color = color,
                        isSelected = name == selectedType,
                        onClick = { onTypeSelected(name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp)) // Espaço entre as linhas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espaço entre os chips
            ) {
                types.entries.drop(3).forEach { (name, color) ->
                    TypeChipManual(
                        name = name,
                        color = color,
                        isSelected = name == selectedType,
                        onClick = { onTypeSelected(name) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeChipManual(
    name: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        modifier = modifier,
        label = { Text(name, color = if (isSelected) Black else Color.Black) },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, shape = CircleShape)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = LightGreen,
            containerColor = CinzentoClaro,
            selectedLabelColor = Black
        ),
        border = if (isSelected) BorderStroke(1.dp, Green) else null
    )
}


@Composable
private fun PhotoSection(
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle("Photo (Optional)")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp), // **Adicionado uma altura para ser visível**
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CinzentoClaro),
                border = BorderStroke(1.dp, LightGreen),
                onClick = { /* TODO: Abrir Câmara/Galeria */ } // Torná-lo clicável!
            ){
                Box(
                    modifier = Modifier.fillMaxSize(), // Preenche o Card pai
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ){
                        Icon(
                            painter = painterResource(R.drawable.camera), // Use o seu ícone
                            contentDescription = "Camara",
                            tint = Color.Black,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to take a photo",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun NotesSection(text: MutableState<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle("Notes (Optional)")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text.value,
                onValueChange = {text.value = it},
                shape = RoundedCornerShape(10.dp),
                label = { Text("Adicione informações adicionais ")},
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = LightGreen,
                    cursorColor = Green
                )
            )

        }
    }
}

@Composable
fun InfoSection() {
 //TODO passar para public e passar cor texto
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        color = GreenLimeLight,
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.info),
                contentDescription = "Informação",
                tint = Green,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Your eco-point will be pending until verified by 2 other users.",
                color = Green,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}



