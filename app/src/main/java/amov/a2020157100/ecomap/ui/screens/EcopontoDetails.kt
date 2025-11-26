package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.model.RecyclingPoint
import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.ui.composables.ImagePickerSelector
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import android.content.res.Configuration
import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import amov.a2020157100.ecomap.ui.composables.getConditionDisplay
import androidx.compose.foundation.rememberScrollState
import amov.a2020157100.ecomap.ui.composables.StatusBadge
import amov.a2020157100.ecomap.ui.composables.getBinStringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcopontoDetails(
    viewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel,
    navController: NavHostController,
    recyclingPointId: String
) {
    LaunchedEffect(recyclingPointId) {
        viewModel.resetReportState()
        viewModel.getRecyclingPoint(recyclingPointId)
    }

    DisposableEffect(Unit) {
        onDispose {
          viewModel.resetReportState()
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val recyclingPoint = viewModel.selectedRecyclingPoint.value

    val currentLocation by locationViewModel.currentLocation
    val binNameRes = getBinStringRes(recyclingPoint?.type)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (recyclingPoint != null) stringResource(binNameRes)
                        else stringResource(R.string.detail_loading),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Branco)
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(CinzentoClaro)
            ) {
                if ((recyclingPoint != null) || (viewModel.isLoading.value && recyclingPoint != null)){
                    if (isLandscape) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                PhotoSection(recyclingPoint.imgUrl)
                                Spacer(modifier = Modifier.height(16.dp))
                                MainInfoSection(recyclingPoint, currentLocation)
                            }

                            // Coluna Direita
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                ActionsSection(viewModel, recyclingPoint)
                                Spacer(modifier = Modifier.height(16.dp))
                                ReportSection(
                                    recyclingPoint = recyclingPoint,
                                    currentLocation = currentLocation,
                                    viewModel = viewModel
                                )
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                        }
                    } else {
                        // --- LAYOUT PORTRAIT (1 Coluna) ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PhotoSection(recyclingPoint.imgUrl)
                            MainInfoSection(recyclingPoint, currentLocation)
                            ActionsSection(viewModel, recyclingPoint)
                            ReportSection(
                                recyclingPoint = recyclingPoint,
                                currentLocation = currentLocation,
                                viewModel = viewModel
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Green)
                    }
                }
            }
        }
    )
}

// --- SECÇÕES REUTILIZÁVEIS ---

@Composable
fun MainInfoSection(
    recyclingPoint: RecyclingPoint,
    currentLocation: Location?
) {
    val distance = remember(currentLocation, recyclingPoint) {
        if (currentLocation != null) {
            val loc = Location("").apply {
                latitude = recyclingPoint.latatitude
                longitude = recyclingPoint.longitude
            }
            currentLocation.distanceTo(loc).toInt()
        } else {
            null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Branco),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Status
            Text("Estado de Verificação", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            val statusText = when (recyclingPoint.status) {
                Status.DELETE.name -> stringResource(R.string.list_status_deleting)
                Status.FINAL.name -> stringResource(R.string.list_status_verified)
                else -> stringResource(R.string.list_status_pending)
            }
            val statusColor = when (recyclingPoint.status) {
                Status.DELETE.name -> deleteColor
                Status.FINAL.name -> verifiedColor
                else -> pendingColor
            }
            StatusBadge(text = statusText, color = statusColor)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

            // Localização
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Green, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(stringResource(R.string.detail_location_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(String.format(Locale.US, "%.5f, %.5f", recyclingPoint.latatitude, recyclingPoint.longitude), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(
                        text = if (distance != null) stringResource(R.string.detail_current_distance, distance) else "-- m",
                        style = MaterialTheme.typography.bodyMedium, color = Color.Gray
                    )
                }
            }

            // Notas
            if (!recyclingPoint.notes.isNullOrBlank()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)
                Text(stringResource(R.string.detail_notes_title), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CinzentoClaro.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(recyclingPoint.notes, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp), color = Black.copy(alpha = 0.8f))
                }
            }

            // Condição
            if(recyclingPoint.condition != null){
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

                // Cabeçalho da secção
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.info), null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Último Reporte da Comunidade",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cartão unificado com a informação do reporte
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Branco),
                    border = BorderStroke(1.dp, CinzentoClaro),
                    elevation = CardDefaults.cardElevation(0.dp) // Flat design para diferenciar do card principal
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        val (displayText, color) = getConditionDisplay(recyclingPoint.condition.state)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(text = stringResource(displayText), color = color)
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        // 2. Imagem (Com altura fixa e cantos arredondados)
                        if(recyclingPoint.condition.imgUrl != null){
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // Altura fixa resolve problemas de layout
                            ) {
                                AsyncImage(
                                    model = recyclingPoint.condition.imgUrl,
                                    contentDescription = "Imagem do reporte",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        // 3. Notas
                        if (!recyclingPoint.condition.notes.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Observações:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                            Text(
                                text = recyclingPoint.condition.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Black.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ActionsSection(viewModel: FirebaseViewModel, recyclingPoint: RecyclingPoint) {
    if (recyclingPoint.status == Status.PENDING.name || recyclingPoint.status == Status.DELETE.name) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Branco),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Ações da Comunidade",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (recyclingPoint.status == Status.PENDING.name) {
                        Button(
                            onClick = { viewModel.confirmEcoponto(recyclingPoint.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Green)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ThumbUp, null, modifier = Modifier.size(20.dp))
                                Text("Confirmar", fontSize = 12.sp)
                                Text("${recyclingPoint.idsVoteAprove?.size ?: 0}/2", fontSize = 10.sp)
                            }
                        }
                    }
                    // Remover
                    Button(
                        onClick = { viewModel.deleteEcoponto(recyclingPoint.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.DeleteForever, null, modifier = Modifier.size(20.dp))
                            Text("Remover", fontSize = 12.sp)
                            Text("${recyclingPoint.idsVoteRemove?.size ?: 0}/3", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportSection(
    recyclingPoint: RecyclingPoint,
    currentLocation: Location?,
    viewModel: FirebaseViewModel
) {
    if (currentLocation == null) return
    val ecopontoLocation = Location("").apply {
        latitude = recyclingPoint.latatitude
        longitude = recyclingPoint.longitude
    }

    if(currentLocation.distanceTo(ecopontoLocation).toInt() > 15) return

    val conditionOptions = listOf(
        "BOM" to Pair("Bom", Green),
        "CHEIO" to Pair("Cheio", pendingColor),
        "DANIFICADO" to Pair("Danificado", Red),
        "DESAPARECIDO" to Pair("Desaparecido", Black.copy(alpha = 0.6f))
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Branco),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(stringResource(R.string.detail_verify_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Green)
            Text(stringResource(R.string.detail_verify_desc), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

            Text("Estado Atual *", style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                conditionOptions.forEach { (key, info) ->
                    val (label, color) = info
                    val isSelected = viewModel.reportState.value == key

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.reportState.value = key },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color,
                            selectedLabelColor = Branco
                        ),
                        border = FilterChipDefaults.filterChipBorder(borderColor = color, selected = isSelected, enabled = true)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                value = viewModel.reportNotes.value,
                onValueChange = { viewModel.reportNotes.value = it },
                placeholder = { Text("Observações (Opcional)") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = LightGreen,
                    cursorColor = Green,
                    focusedContainerColor = Branco,
                    unfocusedContainerColor = Branco
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            ImagePickerSelector(viewModel.addReportPhotoPath.value,
                onImageSelected ={ path ->
                    viewModel.addReportPhotoPath.value = path
                } )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateEcopontoCondicion(
                        recyclingPoint.id,
                        viewModel.reportState.value,
                        viewModel.reportNotes.value,
                        viewModel.addReportPhotoPath.value
                    )
                },
                enabled = viewModel.reportState.value.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green)
            ) {
                Icon(Icons.Outlined.Flag, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.detail_button_report))
            }
        }
    }
}

@Composable
fun PhotoSection(imgUrl: String?){
    if(imgUrl.isNullOrBlank())
        return
    Card(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CinzentoClaro)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = imgUrl,
                contentDescription = stringResource(R.string.detail_image_title),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}





