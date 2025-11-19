package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.model.RecyclingPoint
import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.ui.MainActivity
import amov.a2020157100.ecomap.ui.composables.AppBottomBar
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import amov.a2020157100.ecomap.ui.viewmodels.LocationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import android.location.Location
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val recyclingPoints = viewModel.recyclingPoints.value
    val currentLocation by locationViewModel.currentLocation

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Paper", "Glass", "Plastic", "Metal") // As per mockup

    val filteredPoints = remember(recyclingPoints, selectedFilter) {
        if (selectedFilter == "All") {
            recyclingPoints
        } else {
            recyclingPoints.filter {
                // This logic assumes your `type` string contains the filter word
                // e.g., "Blue bin" contains "Paper" (if we map it)
                // Let's assume a simple mapping for the filter
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Branco)
                    ) {
                        Text(
                            text = stringResource(R.string.list_title),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Green,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        )
                        FilterChips(
                            filters = filters,
                            selectedFilter = selectedFilter,
                            onFilterSelected = { selectedFilter = it }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco,
                    titleContentColor = Color.Black
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(CinzentoClaro),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPoints) { point ->
                    RecyclingPointItem(
                        recyclingPoint = point,
                        currentLocation = currentLocation,
                        onViewDetails = {
                            navController.navigate("${MainActivity.DETAIL_SCREEN}/${point.id}")
                        }
                    )
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
            AppBottomBar(navController = navController)
        }
    )
}

@Composable
private fun FilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = (filter == selectedFilter),
                onClick = { onFilterSelected(filter) },
                label = { Text(text = filter) }, // TODO: Use string resources
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Green,
                    selectedLabelColor = Branco
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Green,
                    selected = false,
                    enabled = false
                )
            )
        }
    }
}

@Composable
private fun RecyclingPointItem(
    recyclingPoint: RecyclingPoint,
    currentLocation: Location?,
    onViewDetails: () -> Unit
) {
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

                if (recyclingPoint.notes?.isNotBlank() == true) {
                    Text(
                        text = recyclingPoint.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
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

//TODO meter em composables partilhados
@Composable
private fun StatusBadge(
    text: String,
    color: Color
) {
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