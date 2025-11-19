package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.model.RecyclingPoint
import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
        viewModel.getRecyclingPoint(recyclingPointId)
    }

    DisposableEffect(Unit) {
        onDispose {
           // viewModel.clearSelectedRecyclingPoint()
        }
    }

    val recyclingPoint by viewModel.selectedRecyclingPoint
    val binNameRes = getBinStringRes(recyclingPoint?.type)
    val currentLocation by locationViewModel.currentLocation

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (recyclingPoint != null) stringResource(binNameRes)
                        else stringResource(R.string.detail_loading),
                        fontWeight = FontWeight.Bold
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

                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(CinzentoClaro),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { PhotoSection(recyclingPoint) }

                    // Secção Principal (Info + Votação Admin)
                    item { MainSection(viewModel, recyclingPoint,currentLocation) }


                 
                }

        }
    )
}

@Composable
private fun MainSection(viewModel: FirebaseViewModel, recyclingPoint: RecyclingPoint?,currentLocation: Location?,) {
    if (recyclingPoint == null) return
    val distance = remember(currentLocation, recyclingPoint) {
        if (currentLocation != null) {
            val ecopontoLocation = Location("").apply {
                latitude = recyclingPoint.latatitude
                longitude = recyclingPoint.longitude
            }
            currentLocation.distanceTo(ecopontoLocation).toInt()
        } else {
            null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Estado Atual",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    var statusText = stringResource(R.string.list_status_pending)
                    var statusColor = pendingColor
                    if (recyclingPoint.status == Status.DELETE.name) {
                        statusText =stringResource(R.string.list_status_deleting)
                        statusColor= deleteColor

                    } else if (recyclingPoint.status == Status.FINAL.name) {
                        statusText =stringResource(R.string.list_status_verified)
                        statusColor=verifiedColor

                    }


                    StatusBadge(text = statusText, color = statusColor)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

            // Localização
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Green,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.detail_location_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = String.format(Locale.US, "%.5f, %.5f", recyclingPoint.latatitude, recyclingPoint.longitude),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = if (distance != null) {
                            stringResource(R.string.detail_current_distance, distance)
                        } else {
                            "-- m"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                    )
                  
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)
            
            if (recyclingPoint.status == Status.PENDING.name || recyclingPoint.status == Status.DELETE.name) {

                Text(
                    text = "Ações da Comunidade",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (recyclingPoint.status == Status.PENDING.name) {
                        Button(
                            onClick = { viewModel.confirmEcoponto(recyclingPoint.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Green)
                        ) {
                            Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Confirmar")
                                val votes = recyclingPoint.idsVoteAprove?.size ?: 0
                                Text("Votos: $votes/2", fontSize = 10.sp, lineHeight = 10.sp)
                            }
                        }
                        Button(
                            onClick = { viewModel.deleteEcoponto(recyclingPoint.id) }, // Assumindo que reportEcoponto aqui é para confirmar delete
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Red)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Remover")
                            }
                        }
                    }

                    if (recyclingPoint.status == Status.DELETE.name) {
                        Button(
                            onClick = { viewModel.deleteEcoponto(recyclingPoint.id) }, // Assumindo que reportEcoponto aqui é para confirmar delete
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Red)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Remover")
                                val votes = recyclingPoint.idsVoteRemove?.size ?: 0
                                Text("Votos: ${votes - 1}/2", fontSize = 10.sp, lineHeight = 10.sp)
                            }
                        }
                    }
                }
            }

            if(recyclingPoint.condition != null){
                Text(text = stringResource(R.string.detail_button_report))
            }
        }
    }
}


@Composable
private fun PhotoSection(recyclingPoint: RecyclingPoint?) {

    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CinzentoClaro)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (recyclingPoint?.imgUrl.isNullOrBlank()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painterResource(R.drawable.camera), null, tint = Color.Gray, modifier = Modifier.size(48.dp)) // Verifica se tens este drawable ou usa um Vector
                    Text("Sem imagem", color = Color.Gray)
                }
            } else {
                // AsyncImage logic here
                Text("Imagem carregada")
            }
        }
    }
}

@Composable
private fun MyTtitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun getBinStringRes(type: String?): Int {
    return when (type) {
        "Blue bin" -> R.string.bin_blue
        "Green bin" -> R.string.bin_green
        "Yellow bin" -> R.string.bin_yellow
        "Red bin" -> R.string.bin_red
        "Black bin" -> R.string.bin_black
        else -> R.string.bin_unknown
    }
}