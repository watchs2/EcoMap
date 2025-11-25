package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.MainActivity
import amov.a2020157100.ecomap.ui.theme.GreenLimeLight
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

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
    // 1. Detetar Landscape
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Recycling Point",
                        style = MaterialTheme.typography.titleLarge, // TitleLarge é melhor que HeadlineLarge aqui
                        color = Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco,
                    titleContentColor = Color.Black
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(CinzentoClaro)
            ) {
                if (isLandscape) {
                    // --- LAYOUT LANDSCAPE (2 Colunas) ---
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Coluna Esquerda: Tipo e Localização
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            EcoPointTypeSection(
                                selectedType = viewModel.addType.value,
                                onTypeSelected = { viewModel.addType.value = it }
                            )
                            LocationSection(locationViewModel, viewModel)
                        }

                        // Coluna Direita: Foto, Notas, Info e Botão
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PhotoSection()
                            NotesSection(viewModel) // Passamos o viewModel agora
                            InfoSection()
                            SubmitButtonSection(viewModel, navController)

                            // Espaço extra no fundo
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                } else {
                    // --- LAYOUT PORTRAIT (1 Coluna) ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()), // Scroll na coluna toda
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        EcoPointTypeSection(
                            selectedType = viewModel.addType.value,
                            onTypeSelected = { viewModel.addType.value = it }
                        )
                        LocationSection(locationViewModel, viewModel)
                        PhotoSection()
                        NotesSection(viewModel)
                        InfoSection()
                        SubmitButtonSection(viewModel, navController)
                    }
                }
            }
        }
    )
}

// --- SECÇÕES REUTILIZÁVEIS ---

@Composable
fun SubmitButtonSection(viewModel: FirebaseViewModel, navController: NavHostController) {
    Column {
        if (viewModel.error.value != null) {
            Text(
                text = viewModel.error.value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                viewModel.addRecyclingPoint(
                    type = viewModel.addType.value,
                    latatitude = viewModel.addLatitude.value,
                    longitude = viewModel.addLongitude.value,
                    imgUrl = null,
                    notes = viewModel.addNotes.value,
                    onSuccess = {
                        viewModel.resetAddForm() // Limpar form após sucesso
                        navController.navigate(MainActivity.MAPVIEW_SCREEN)
                    }
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green,
                contentColor = Branco
            )
        ) {
            Text(
                "Add Ecoponto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MyTtitle(title: String){
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun LocationSection(
    locationViewModel: LocationViewModel,
    viewModel: FirebaseViewModel // Recebe o FirebaseViewModel para guardar estado
){
    val currentLocation = locationViewModel.currentLocation.value


    val lat = viewModel.addLatitude.value
    val lon = viewModel.addLongitude.value
    val displayLocation = if (lat != 0.0 || lon != 0.0) {
        val latDir = if (lat >= 0) "N" else "S"
        val lonDir = if (lon >= 0) "E" else "W"
        "%.4f° %s, %.4f° %s".format(lat, latDir, lon, lonDir)
    } else {
        ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle("Location *")

            OutlinedTextField(
                value = displayLocation,
                onValueChange = { /* Read Only */ },
                readOnly = true,
                placeholder = { Text("No location selected") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.location2),
                        contentDescription = null,
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
                    disabledContainerColor = CinzentoClaro,
                    disabledTextColor = Black,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if(currentLocation != null){
                        // Guardar no ViewModel
                        viewModel.addLatitude.value = currentLocation.latitude
                        viewModel.addLongitude.value = currentLocation.longitude
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = Branco
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.compass),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text="Use Current Location", style = MaterialTheme.typography.bodyMedium)
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

            // Usando FlowRow seria melhor, mas mantendo a lógica simples de Rows:
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                // Preencher espaço vazio se houver número impar
                if (types.size % 3 != 0) {
                    Spacer(modifier = Modifier.weight(1f))
                }
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
        label = {
            Text(
                name.replace(" bin", ""), // Simplificar texto (opcional)
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, shape = CircleShape)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GreenLimeLight.copy(alpha=0.3f), // Cor de fundo mais suave
            containerColor = CinzentoClaro,
            selectedLabelColor = Black
        ),
        border = if (isSelected) BorderStroke(2.dp, Green) else BorderStroke(0.dp, Color.Transparent)
    )
}

@Composable
private fun PhotoSection() {
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
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CinzentoClaro),
                border = BorderStroke(1.dp, GreenLimeLight),
                onClick = { /* TODO: Abrir Câmara/Galeria */ }
            ){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Icon(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = "Camara",
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
private fun NotesSection(viewModel: FirebaseViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle("Notes (Optional)")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.addNotes.value,
                onValueChange = { viewModel.addNotes.value = it },
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Information about access, conditions, etc.")},
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = GreenLimeLight,
                    cursorColor = Green,
                    focusedContainerColor = Branco,
                    unfocusedContainerColor = Branco
                )
            )
        }
    }
}

@Composable
fun InfoSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GreenLimeLight.copy(alpha = 0.4f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.info),
                contentDescription = null,
                tint = Green,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Your eco-point will be pending verification by the community.",
                color = Green,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}