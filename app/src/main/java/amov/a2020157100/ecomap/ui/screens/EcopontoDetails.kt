package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.model.RecyclingPoint
import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.ui.composables.EcoMapTopBar
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
import amov.a2020157100.ecomap.ui.theme.*
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
            val title = if (recyclingPoint != null) stringResource(binNameRes)
            else stringResource(R.string.detail_loading)
            EcoMapTopBar(
                title = title,
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
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    )
}

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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                stringResource(R.string.detail_verification_status),
                style = MaterialTheme.typography.labelMedium,
                color = TextDarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            val statusText = when (recyclingPoint.status) {
                Status.DELETE.name -> stringResource(R.string.list_status_deleting)
                Status.FINAL.name -> stringResource(R.string.list_status_verified)
                else -> stringResource(R.string.list_status_pending)
            }
            val statusColor = when (recyclingPoint.status) {
                Status.DELETE.name -> StatusError
                Status.FINAL.name -> StatusVerified
                else -> StatusPending
            }
            StatusBadge(text = statusText, color = statusColor)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.background)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(stringResource(R.string.detail_location_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextDarkGray)
                    Text(String.format(Locale.US, "%.5f, %.5f", recyclingPoint.latatitude, recyclingPoint.longitude), style = MaterialTheme.typography.bodyMedium, color = TextDarkGray)
                    Text(
                        text = if (distance != null) stringResource(R.string.detail_current_distance, distance) else "-- m",
                        style = MaterialTheme.typography.bodyMedium, color = TextDarkGray
                    )
                }
            }

            // Notas
            if (!recyclingPoint.notes.isNullOrBlank()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.background)
                Text(stringResource(R.string.detail_notes_title), style = MaterialTheme.typography.labelMedium, color = TextDarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(recyclingPoint.notes, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f))
                }
            }

            if(recyclingPoint.condition != null){
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.background)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.info), null, tint = TextDarkGray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.detail_last_report),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkGray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        val (displayText, color) = getConditionDisplay(recyclingPoint.condition.state)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(text = stringResource(displayText), color = color)
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        if(recyclingPoint.condition.imgUrl != null){
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                AsyncImage(
                                    model = recyclingPoint.condition.imgUrl,
                                    contentDescription = stringResource(R.string.detail_report_image_cd),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }


                        if (!recyclingPoint.condition.notes.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.detail_observations_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextDarkGray
                            )
                            Text(
                                text = recyclingPoint.condition.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.detail_community_actions_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = TextDarkGray
                )
                Text(
                    text = stringResource(R.string.detail_community_action_appeal),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (recyclingPoint.status == Status.PENDING.name) {
                        Button(
                            onClick = { viewModel.confirmEcoponto(recyclingPoint.id) },
                            modifier = Modifier.width(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(6.dp)
                        ){
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ThumbUp, null, modifier = Modifier.size(20.dp))
                                Text(stringResource(R.string.detail_btn_confirm_vote), fontSize = 12.sp)
                                Text("${recyclingPoint.idsVoteAprove?.size ?: 0}/2", fontSize = 10.sp)
                            }
                        }
                    }
                   if(recyclingPoint.status != Status.DELETE.name){
                       Button(
                           onClick = { viewModel.deleteEcoponto(recyclingPoint.id) },
                           modifier = Modifier.width(120.dp),
                           shape = RoundedCornerShape(12.dp),
                           colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                           contentPadding = PaddingValues(6.dp)
                       ) {
                           Column(horizontalAlignment = Alignment.CenterHorizontally) {
                               Icon(Icons.Default.DeleteForever, null, modifier = Modifier.size(20.dp))
                               Text(stringResource(R.string.detail_btn_remove_vote), fontSize = 12.sp)
                           }
                       }
                   }else{
                       Button(
                           onClick = { viewModel.deleteEcoponto(recyclingPoint.id) },
                           modifier = Modifier.width(120.dp),
                           shape = RoundedCornerShape(12.dp),
                           colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                           contentPadding = PaddingValues(6.dp)
                       ) {
                           Column(horizontalAlignment = Alignment.CenterHorizontally) {
                               Icon(Icons.Default.DeleteForever, null, modifier = Modifier.size(20.dp))
                               Text(stringResource(R.string.detail_btn_remove_vote), fontSize = 12.sp)
                               Text("${(recyclingPoint.idsVoteRemove?.size?.minus(1)) ?: 0}/2", fontSize = 10.sp)
                           }
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

    // Agora utilizamos os Resources para os textos das opções
    val conditionOptions = listOf(
        "BOM" to Pair(R.string.binState_good, StatusGood),
        "CHEIO" to Pair(R.string.binState_full, StatusFull),
        "DANIFICADO" to Pair(R.string.binState_damaged, StatusDamaged),
        "DESAPARECIDO" to Pair(R.string.binState_missing, StatusMissing.copy(alpha = 0.6f))
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(stringResource(R.string.detail_verify_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.detail_verify_desc), style = MaterialTheme.typography.bodyMedium, color = TextDarkGray)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.background)

            Text(stringResource(R.string.detail_current_state_label), style = MaterialTheme.typography.labelMedium, color = TextDarkGray, modifier = Modifier.padding(bottom = 8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                conditionOptions.forEach { (key, info) ->
                    val (labelRes, color) = info
                    val isSelected = viewModel.reportState.value == key

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.reportState.value = key },
                        label = { Text(stringResource(labelRes)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
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
                placeholder = { Text(stringResource(R.string.detail_observations_placeholder)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
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