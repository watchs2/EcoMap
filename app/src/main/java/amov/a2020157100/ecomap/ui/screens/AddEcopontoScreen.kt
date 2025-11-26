package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.MainActivity
import amov.a2020157100.ecomap.ui.composables.ImagePickerSelector
import amov.a2020157100.ecomap.ui.theme.GreenLimeLight
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import amov.a2020157100.ecomap.utils.camera.FileUtils
import android.content.res.Configuration
import coil3.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import java.io.File

// Cores
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
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("New Recycling Point", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFEAEDEF)) // CinzentoClaro
            ) {
                if (isLandscape) {
                    // --- LAYOUT LANDSCAPE (Split View) ---
                    Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            EcoPointTypeSection(viewModel.addType.value) { viewModel.addType.value = it }
                            LocationSection(locationViewModel, viewModel)
                        }
                        Column(
                            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            //PhotoSection(viewModel) { showImageSourceDialog = true }
                            ImagePickerSelector(viewModel.addPhotoPath.value,
                                onImageSelected ={ path ->
                                    viewModel.addPhotoPath.value = path
                                } )
                            NotesSection(viewModel)
                            InfoSection()
                            SubmitButtonSection(viewModel, navController)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                } else {
                    // --- LAYOUT PORTRAIT (Single Column) ---
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        EcoPointTypeSection(viewModel.addType.value) { viewModel.addType.value = it }
                        LocationSection(locationViewModel, viewModel)
                        //PhotoSection(viewModel) { showImageSourceDialog = true }
                        ImagePickerSelector(viewModel.addPhotoPath.value,
                            onImageSelected ={ path ->
                                viewModel.addPhotoPath.value = path
                            })
                        NotesSection(viewModel)
                        InfoSection()
                        SubmitButtonSection(viewModel, navController)
                    }
                }
            }
        }
    )
}

// --- COMPONENTES ---

@Composable
private fun PhotoSection(
    viewModel: FirebaseViewModel,
    onPhotoClick: () -> Unit
) {
    val photoPath = viewModel.addPhotoPath.value

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Photo (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEAEDEF)),
                border = BorderStroke(1.dp, GreenLimeLight),
                onClick = onPhotoClick
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (photoPath != null) {
                        AsyncImage(
                            model = photoPath,
                            contentDescription = "Selected Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.BottomEnd) {
                            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.7f)) {
                                Icon(Icons.Default.CameraAlt, null, modifier = Modifier.padding(8.dp), tint = Color(0xFF2E7C32))
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painterResource(R.drawable.camera), null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tap to take a photo", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubmitButtonSection(viewModel: FirebaseViewModel, navController: NavHostController) {
    Column {
        if (viewModel.error.value != null) {
            Text(viewModel.error.value.toString(), color = Color(0xFFD22F2F), modifier = Modifier.padding(bottom = 8.dp))
        }
        if (viewModel.isLoading.value) {
            CircularProgressIndicator(color = Color(0xFF2E7C32))
        }else {
            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                onClick = {
                    viewModel.addRecyclingPoint(
                        type = viewModel.addType.value,
                        latitude = viewModel.addLatitude.value,
                        longitude = viewModel.addLongitude.value,
                        imgPath = viewModel.addPhotoPath.value,
                        notes = viewModel.addNotes.value,
                        onSuccess = {
                            viewModel.resetAddForm()
                            navController.navigate(MainActivity.MAPVIEW_SCREEN)
                        }
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7C32),
                    contentColor = Color.White
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
}

@Composable
private fun LocationSection(locationViewModel: LocationViewModel, viewModel: FirebaseViewModel){
    val currentLocation = locationViewModel.currentLocation.value
    val lat = viewModel.addLatitude.value
    val lon = viewModel.addLongitude.value
    val displayLocation = if (lat != 0.0 || lon != 0.0) "%.4f, %.4f".format(lat, lon) else ""

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Location *", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = displayLocation, onValueChange = {}, readOnly = true,
                placeholder = { Text("No location selected") },
                leadingIcon = { Icon(painterResource(R.drawable.location2), null, tint = Color(0xFF2E7C32), modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color(0xFFEAEDEF), unfocusedContainerColor = Color(0xFFEAEDEF))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { if(currentLocation != null){ viewModel.addLatitude.value = currentLocation.latitude; viewModel.addLongitude.value = currentLocation.longitude } },
                modifier = Modifier.fillMaxWidth().height(45.dp), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7C32), contentColor = Color.White)
            ) {
                Icon(painterResource(R.drawable.compass), null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use Current Location")
            }
        }
    }
}

@Composable
private fun EcoPointTypeSection(selectedType: String, onTypeSelected: (String) -> Unit) {
    val types = mapOf("Blue bin" to blueBinColor, "Green bin" to greenBinColor, "Yellow bin" to yellowBinColor, "Red bin" to redBinColor, "Black bin" to blackBinColor)
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("EcoPonto Type *", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.entries.take(3).forEach { TypeChipManual(it.key, it.value, it.key == selectedType, { onTypeSelected(it.key) }, Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.entries.drop(3).forEach { TypeChipManual(it.key, it.value, it.key == selectedType, { onTypeSelected(it.key) }, Modifier.weight(1f)) }
                if (types.size % 3 != 0) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeChipManual(name: String, color: Color, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilterChip(
        selected = isSelected, onClick = onClick, modifier = modifier,
        label = { Text(name.replace(" bin", ""), style = MaterialTheme.typography.bodySmall, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal, maxLines = 1) },
        leadingIcon = { Box(modifier = Modifier.size(10.dp).background(color, shape = CircleShape)) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GreenLimeLight.copy(alpha=0.3f), containerColor = Color(0xFFEAEDEF), selectedLabelColor = Color.Black),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF2E7C32)) else BorderStroke(0.dp, Color.Transparent)
    )
}

@Composable
private fun NotesSection(viewModel: FirebaseViewModel) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Notes (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(), value = viewModel.addNotes.value, onValueChange = { viewModel.addNotes.value = it },
                shape = RoundedCornerShape(10.dp), placeholder = { Text("Info about access, etc.")}, minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2E7C32), unfocusedBorderColor = GreenLimeLight, cursorColor = Color(0xFF2E7C32), focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )
        }
    }
}

@Composable
fun InfoSection() {
    Surface(modifier = Modifier.fillMaxWidth(), color = GreenLimeLight.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.info), null, tint = Color(0xFF2E7C32), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Pending verification by community.", color = Color(0xFF2E7C32), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        }
    }
}