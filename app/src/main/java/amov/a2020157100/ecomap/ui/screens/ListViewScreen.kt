package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.model.RecyclingPoint
import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.ui.MainActivity
import amov.a2020157100.ecomap.ui.composables.AppBottomBar
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import android.content.res.Configuration
import android.location.Location
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Cores (Mantém as tuas)
val pendingColor = Color(0xFFFBC02D)
val verifiedColor = Green
val deleteColor = Red


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewScreen(
    viewModel: FirebaseViewModel,
    locationViewModel: LocationViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        viewModel.getRecyclingPoints()
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val recyclingPoints = viewModel.recyclingPoints.value
    val currentLocation by locationViewModel.currentLocation

    // Estado do Filtro vindo do ViewModel
    val selectedFilter = viewModel.selectedFilter.value
    val filters = listOf("All", "Paper", "Glass", "Plastic", "Metal")

    // Lógica de filtragem
    val filteredPoints = remember(recyclingPoints, selectedFilter) {
        if (selectedFilter == "All") {
            recyclingPoints
        } else {
            recyclingPoints.filter {
                when (selectedFilter) {
                    "Paper" -> it.type == "Blue bin"
                    "Glass" -> it.type == "Green bin"
                    "Plastic" -> it.type == "Yellow bin"
                    "Metal" -> it.type == "Red bin"
                    else -> true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.list_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Green,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco,
                    titleContentColor = Color.Black
                )
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
                    // --- LAYOUT LANDSCAPE ---
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Coluna da Esquerda (Filtros)
                        Column(
                            modifier = Modifier
                                .width(280.dp) // Largura fixa para o painel lateral
                                .fillMaxHeight()
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Em landscape, usamos o cartão mas se calhar queremos ele sempre aberto ou expansível?
                            // Vamos manter expansível para consistência, mas podes forçar 'expanded = true' se preferires.
                            FilterAccordion(
                                filters = filters,
                                selectedFilter = selectedFilter,
                                onFilterSelect = { viewModel.selectedFilter.value = it }
                            )
                        }

                        // Coluna da Direita (Lista)
                        RecyclingPointsList(
                            points = filteredPoints,
                            currentLocation = currentLocation,
                            navController = navController,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // --- LAYOUT PORTRAIT ---
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Acordeão de Filtros no topo
                        Box(modifier = Modifier.padding(16.dp)) {
                            FilterAccordion(
                                filters = filters,
                                selectedFilter = selectedFilter,
                                onFilterSelect = { viewModel.selectedFilter.value = it }
                            )
                        }

                        // Lista
                        RecyclingPointsList(
                            points = filteredPoints,
                            currentLocation = currentLocation,
                            navController = navController,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Green,
                onClick = { navController.navigate(MainActivity.ADDECOPONTO_SCREEN) }
            ) {
                Icon(
                    Icons.Filled.Add,
                    tint = Branco,
                    contentDescription = stringResource(R.string.list_add_ecopoint_cd)
                )
            }
        },
        bottomBar = {
            AppBottomBar(navController, onSignOut = {viewModel.signOut()})
        }
    )
}

// --- NOVO COMPONENTE: ACORDEÃO DE FILTROS ---
@Composable
fun FilterAccordion(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit
) {
    // rememberSaveable para manter aberto/fechado se rodares o ecrã
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Animação da setinha a rodar
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, label = "rotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // CABEÇALHO (Clicável)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = Green
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Filtrar Ecopontos",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        // Mostra qual está selecionado se o cartão estiver fechado
                        if (!expanded) {
                            Text(
                                text = "Selecionado: $selectedFilter",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    modifier = Modifier.rotate(rotationState),
                    tint = Color.Gray
                )
            }

            // CONTEÚDO EXPANSÍVEL
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    HorizontalDivider(color = CinzentoClaro)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostra as opções (Usando FlowRow se tiveres a dependência ou simples Column/Row)
                    // Como não sei se tens FlowLayout, vou usar uma Column simples com RadioButtons ou Chips
                    // Vamos usar Chips num layout de grelha adaptado ou simples Column

                    filters.forEach { filter ->
                        FilterOptionRow(
                            text = filter,
                            isSelected = filter == selectedFilter,
                            onClick = {
                                onFilterSelect(filter)
                                // Opcional: fechar ao selecionar?
                                // expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterOptionRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Green)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Green else Black
        )
    }
}

// --- RESTO DOS COMPONENTES (IGUAIS AO ANTERIOR) ---

@Composable
fun RecyclingPointsList(
    points: List<RecyclingPoint>,
    currentLocation: Location?,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(points) { point ->
            RecyclingPointItem(
                recyclingPoint = point,
                currentLocation = currentLocation,
                onViewDetails = {
                    navController.navigate("${MainActivity.DETAIL_SCREEN}/${point.id}")
                }
            )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun RecyclingPointItem(
    recyclingPoint: RecyclingPoint,
    currentLocation: Location?,
    onViewDetails: () -> Unit
) {
    // ... (Mantém o código do Item igual ao que enviei anteriormente) ...
    val binColor = getBinColor(recyclingPoint.type)
    val binName = getBinStringRes(recyclingPoint.type)

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
        colors = CardDefaults.cardColors(containerColor = Branco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(binColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.recycable),
                    contentDescription = null,
                    tint = binColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = stringResource(binName),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (distance != null) {
                            stringResource(R.string.list_distance_metres, distance)
                        } else {
                            "-- m"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (recyclingPoint.status == Status.PENDING.name) {
                        StatusBadge(
                            text = stringResource(R.string.list_status_pending),
                            color = pendingColor
                        )
                    } else if (recyclingPoint.status == Status.DELETE.name) {
                        StatusBadge(
                            text = stringResource(R.string.list_status_deleting),
                            color = deleteColor
                        )
                    } else if (recyclingPoint.status == Status.FINAL.name) {
                        StatusBadge(
                            text = stringResource(R.string.list_status_verified),
                            color = verifiedColor
                        )
                    }
                }
            }
            IconButton(onClick = onViewDetails) {
                Icon(
                    imageVector = Icons.Outlined.Visibility,
                    contentDescription = stringResource(R.string.list_view_details_cd),
                    tint = Color.Gray
                )
            }
        }
    }
}

// Funções auxiliares (StatusBadge, getBinColor, etc.) mantêm-se iguais
@Composable
private fun StatusBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun getBinColor(type: String): Color {
    return when (type) {
        "Blue bin" -> blueBinColor
        "Green bin" -> greenBinColor
        "Yellow bin" -> yellowBinColor
        "Red bin" -> redBinColor
        "Black bin" -> blackBinColor
        else -> Color.Gray
    }
}

@Composable
private fun getBinStringRes(type: String): Int {
    return when (type) {
        "Blue bin" -> R.string.bin_blue
        "Green bin" -> R.string.bin_green
        "Yellow bin" -> R.string.bin_yellow
        "Red bin" -> R.string.bin_red
        "Black bin" -> R.string.bin_black
        else -> R.string.bin_unknown
    }
}