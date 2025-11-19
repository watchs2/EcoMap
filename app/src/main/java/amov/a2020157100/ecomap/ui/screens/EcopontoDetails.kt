package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.model.RecyclingPoint
import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

    val reportState = remember { mutableStateOf("") } // Estado reportado (e.g., "CHEIO", "BOM")
    val reportNotes = remember { mutableStateOf(recyclingPoint?.condition?.notes ?: "") } // Notas

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

                    item { MainSection(viewModel, recyclingPoint,currentLocation) }
                    item {
                        ReportSection(
                            recyclingPoint = recyclingPoint,
                            selectedState = reportState.value,
                            notes = reportNotes.value,
                            onStateSelected = { reportState.value = it },
                            onNotesChange = { reportNotes.value = it },
                            currentLocation= currentLocation,
                            onSubmitReport = {
                                viewModel.updateEcopontoCondicion(recyclingPointId, reportState.value, reportNotes.value, null)
                            }
                        )
                    }

                 
                }

        }
    )
}

@Composable
private fun MainSection(
    viewModel: FirebaseViewModel,
    recyclingPoint: RecyclingPoint?,
    currentLocation: Location?,
) {
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

            // --- 1. Secção de Status Principal ---
            Text(
                text = "Estado de Verificação",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
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

            // Reutiliza a função StatusBadge
            StatusBadge(text = statusText, color = statusColor)


            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

            // --- 2. Secção de Localização e Distância ---
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

            // --- 3. Secção de Notas (Se existirem) ---
            if (!recyclingPoint.notes.isNullOrBlank()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

                Text(
                    text = stringResource(R.string.detail_notes_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CinzentoClaro.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = recyclingPoint.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Black.copy(alpha = 0.8f),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            // --- 3. Secção de  Report---
            if(recyclingPoint.condition != null){
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)
                Text(
                    text = "Último Reporte de Condição",
                    style =MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )/*
                Text(
                    text = "Último Reporte de Condição",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CinzentoEscuro
                )*/
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    Text(
                        text = "Condição Reportada",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val (displayText, color) = getConditionDisplay(recyclingPoint.condition.state)
                    StatusBadge(text = displayText, color = color)
                    if (!recyclingPoint.condition.notes.isNullOrBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

                        Text(
                            text = stringResource(R.string.detail_notes_title),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = recyclingPoint.condition.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Black.copy(alpha = 0.8f)
                        )
                    }
                }
            }


            // --- 4. Secção de Ações da Comunidade ---
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

            if (recyclingPoint.status == Status.PENDING.name || recyclingPoint.status == Status.DELETE.name) {

                Text(
                    text = "Ações da Comunidade",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Botão de Confirmação (Visível apenas em PENDING)
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
                    }

                    // Botão de Remover (Visível em PENDING e DELETE)
                    if (recyclingPoint.status == Status.PENDING.name || recyclingPoint.status == Status.DELETE.name) {
                        Button(
                            onClick = { viewModel.deleteEcoponto(recyclingPoint.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Red)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Remover")
                                val currentVotes = recyclingPoint.idsVoteRemove?.size ?: 0
                                // Se estiver em DELETE, já tem o voto inicial. Se estiver em PENDING, ainda não tem.
                                val votesDisplay = if (recyclingPoint.status == Status.DELETE.name) {
                                    "${currentVotes}/3"
                                } else {
                                    "${currentVotes}/3"
                                }
                                Text("Votos: $votesDisplay", fontSize = 10.sp, lineHeight = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportSection(
    recyclingPoint: RecyclingPoint?,
    onStateSelected: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSubmitReport: () -> Unit,
    selectedState: String,
    notes: String,
    currentLocation: Location?
) {
    if (recyclingPoint == null || currentLocation == null) return
    val ecopontoLocation = Location("").apply {
        latitude = recyclingPoint.latatitude
        longitude = recyclingPoint.longitude
    }
    if( currentLocation.distanceTo(ecopontoLocation).toInt() > 5) return

    val conditionOptions = remember {
        listOf(
            "BOM" to Pair("Bom", Green), // Estado positivo
            "CHEIO" to Pair("Cheio", pendingColor), // Alerta/Aviso
            "DANIFICADO" to Pair("Danificado", Red), // Estado negativo/Problema
            "DESAPARECIDO" to Pair("Desaparecido", Black.copy(alpha = 0.6f)) // Problema Grave
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // --- Título e Descrição ---
            Text(
                text = stringResource(R.string.detail_verify_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.detail_verify_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

            // --- Seleção de Estado ---
            Text(
                text = "Estado Atual do Ecoponto *",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                conditionOptions.forEach { (stateKey, stateInfo) ->
                    val (displayText, color) = stateInfo
                    ReportStateChip(
                        text = displayText,
                        color = color,
                        isSelected = stateKey == selectedState,
                        onClick = { onStateSelected(stateKey) }
                    )
                }
            }

            // --- Secção de Notas (Opcional) ---
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.detail_notes_title) + " (Opcional)",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                value = notes,
                onValueChange = onNotesChange,
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Adicione observações sobre a condição...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = LightGreen,
                    cursorColor = Green,
                    focusedContainerColor = Branco,
                    unfocusedContainerColor = Branco
                )
            )

            // TODO: Inserir aqui a lógica da foto, se necessário. Por enquanto, só a UI de texto/botão.


            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CinzentoClaro)

            // --- Botão de Reportar ---
            Button(
                onClick = onSubmitReport,
                enabled = selectedState.isNotBlank(), // O utilizador tem de selecionar um estado
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    disabledContainerColor = Green.copy(alpha = 0.5f)
                )
            ) {
                Icon(Icons.Outlined.Flag, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.detail_button_report), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportStateChip(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text, color = if (isSelected) Branco else Black.copy(alpha = 0.8f)) },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = CinzentoClaro,
            selectedContainerColor = color, // Cor do estado para o fundo
            selectedLabelColor = Branco,
            labelColor = Black.copy(alpha = 0.8f)
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = color,
            selected = isSelected,
            enabled = false
        )
    )
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

@Composable
private fun getConditionDisplay(state: String): Pair<String, Color> {
    // Mapeamento das strings de estado para o texto de display e cor
    val displayText = when (state) {
        "BOM" -> "Bom"
        "CHEIO" -> "Cheio"
        "DANIFICADO" -> "Danificado"
        "DESAPARECIDO" -> "Desaparecido"
        else -> "Desconhecido"
    }

    val color = when (state) {
        "BOM" -> Green
        "CHEIO" -> pendingColor // Usar amarelo para CHEIO
        "DANIFICADO" -> Red
        "DESAPARECIDO" -> Black.copy(alpha = 0.6f) // Usar cinza escuro para DESAPARECIDO
        else -> Color.Gray
    }
    return Pair(displayText, color)
}