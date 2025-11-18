package amov.a2020157100.ecomap.ui.screens



import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.model.RecyclingPoint
import amov.a2020157100.ecomap.model.Status
import amov.a2020157100.ecomap.ui.viewmodels.FirebaseViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    navController: NavHostController,
    recyclingPointId: String
) {
    LaunchedEffect(recyclingPointId) {
        viewModel.getRecyclingPoint(recyclingPointId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedRecyclingPoint()
        }
    }

    val recyclingPoint by viewModel.selectedRecyclingPoint
    val binNameRes = getBinStringRes(recyclingPoint?.type)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (recyclingPoint != null) stringResource(binNameRes)
                        else stringResource(R.string.detail_loading)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = stringResource(R.string.detail_back_cd)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco,
                    titleContentColor = Color.Black
                )
            )
        },
        content = { paddingValues ->
            if (recyclingPoint == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(CinzentoClaro),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        PhotoSection(recyclingPoint)
                    }
                    item {
                        StatusSection(recyclingPoint)
                    }
                    item {
                        LocationSection(recyclingPoint)
                    }
                    item {
                        NotesSection(recyclingPoint)
                    }
                    item {
                        VerificationSection(
                            viewModel = viewModel,
                            recyclingPoint = recyclingPoint
                        )
                    }
                }
            }
        }
    )
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
private fun PhotoSection(recyclingPoint: RecyclingPoint?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle(stringResource(R.string.detail_image_title))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CinzentoClaro)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (recyclingPoint?.imgUrl.isNullOrBlank()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.camera), //
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.detail_no_image),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    } else {
                        // TODO: Carregar a imagem com Coil/Glide
                        // Por agora, mostrar um placeholder
                        Icon(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = stringResource(R.string.detail_image_title),
                            tint = Green,
                            modifier = Modifier.size(64.dp)
                        )
                        // AsyncImage(model = recyclingPoint.imgUrl, contentDescription = "Eco-Point Photo")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusSection(recyclingPoint: RecyclingPoint?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle(stringResource(R.string.detail_status_title))
            val statusText = if (recyclingPoint?.status == Status.PENDING.name) {
                stringResource(R.string.list_status_pending)
            } else {
                stringResource(R.string.list_status_verified)
            }
            val statusColor = if (recyclingPoint?.status == Status.PENDING.name) {
                pendingColor
            } else {
                verifiedColor
            }
            StatusBadge(text = statusText, color = statusColor)
        }
    }
}

@Composable
private fun LocationSection(recyclingPoint: RecyclingPoint?) {
    val locationText = String.format(
        Locale.US,
        "%.6f, %.6f",
        recyclingPoint?.latatitude ?: 0.0,
        recyclingPoint?.longitude ?: 0.0
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle(stringResource(R.string.detail_location_title))
            OutlinedTextField(
                value = locationText,
                onValueChange = { },
                readOnly = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Green
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = CinzentoClaro,
                    unfocusedContainerColor = CinzentoClaro,
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* TODO mudar isto */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CinzentoEscuro)
            ) {
                Text(stringResource(R.string.detail_view_on_map))
            }
        }
    }
}

@Composable
private fun NotesSection(recyclingPoint: RecyclingPoint?) {
    val notes = recyclingPoint?.notes
    if (!notes.isNullOrBlank()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Branco)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MyTtitle(stringResource(R.string.detail_notes_title))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun VerificationSection(
    viewModel: FirebaseViewModel,
    recyclingPoint: RecyclingPoint?
) {
    if (recyclingPoint == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyTtitle(stringResource(R.string.detail_verify_title))
            Text(
                text = stringResource(R.string.detail_verify_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = CinzentoEscuro,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.reportEcoponto(recyclingPoint.id) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red.copy(alpha = 0.1f),
                        contentColor = Red
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.detail_button_report))
                }
                Button(
                    onClick = { viewModel.confirmEcoponto(recyclingPoint.id) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green,
                        contentColor = Branco
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.detail_button_confirm))
                }
            }
        }
    }
}

// Helper Functions
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