package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.MainActivity
import amov.a2020157100.ecomap.ui.composables.EcoMapTopBar
import amov.a2020157100.ecomap.ui.composables.ImagePickerSelector
import amov.a2020157100.ecomap.ui.composables.getBinStringRes
import amov.a2020157100.ecomap.ui.theme.*
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEcopontoScreen(
    viewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
){

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            EcoMapTopBar(
                title = stringResource(R.string.add_ecopoint_title),
                navController = navController,
                showBackButton = true
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (isLandscape) {
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
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        EcoPointTypeSection(viewModel.addType.value) { viewModel.addType.value = it }
                        LocationSection(locationViewModel, viewModel)
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


@Composable
fun SubmitButtonSection(viewModel: FirebaseViewModel, navController: NavHostController) {
    Column {
        if (viewModel.error.value != null) {
            Text(
                text = viewModel.error.value.toString(),
                color = StatusError, // Substituído Color(0xFFD22F2F)
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (viewModel.isLoading.value) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
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
                    containerColor = MaterialTheme.colorScheme.primary, // Substituído Color(0xFF2E7C32)
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.add_submit_btn),
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.add_location_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = displayLocation,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(R.string.add_location_placeholder)) },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.location2),
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = BackgroundGray, // Substituído Color(0xFFEAEDEF)
                    unfocusedContainerColor = BackgroundGray
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if(currentLocation != null){
                        viewModel.addLatitude.value = currentLocation.latitude
                        viewModel.addLongitude.value = currentLocation.longitude
                    }
                },
                modifier = Modifier.fillMaxWidth().height(45.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(painterResource(R.drawable.compass), null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_location_btn))
            }
        }
    }
}

@Composable
private fun EcoPointTypeSection(selectedType: String, onTypeSelected: (String) -> Unit) {
    val types = mapOf(
        "Blue bin" to BinBlue,
        "Green bin" to BinGreen,
        "Yellow bin" to BinYellow,
        "Red bin" to BinRed,
        "Black bin" to BinBlack
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.add_type_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.entries.take(3).forEach {
                    TypeChipManual(it.key, it.value, it.key == selectedType, { onTypeSelected(it.key) }, Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.entries.drop(3).forEach {
                    TypeChipManual(it.key, it.value, it.key == selectedType, { onTypeSelected(it.key) }, Modifier.weight(1f))
                }
                if (types.size % 3 != 0) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeChipManual(name: String, color: Color, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        modifier = modifier,
        label = {
            // Usa getBinStringRes para traduzir o nome do caixote
            Text(
                text = stringResource(getBinStringRes(name)),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        },
        leadingIcon = { Box(modifier = Modifier.size(10.dp).background(color, shape = CircleShape)) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha=0.3f),
            containerColor = BackgroundGray,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(0.dp, Color.Transparent)
    )
}

@Composable
private fun NotesSection(viewModel: FirebaseViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.add_notes_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.addNotes.value,
                onValueChange = { viewModel.addNotes.value = it },
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text(stringResource(R.string.add_notes_placeholder))},
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun InfoSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.info),
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.add_pending_info),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}