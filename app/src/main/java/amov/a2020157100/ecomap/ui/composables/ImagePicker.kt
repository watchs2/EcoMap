package amov.a2020157100.ecomap.ui.composables


import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.utils.camera.FileUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerSelector(
    currentImagePath: String?,
    onImageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraPath by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val path = FileUtils.createFileFromUri(context, uri)
            onImageSelected(path)
        }
        showImageSourceDialog = false
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraPath != null) {
            onImageSelected(tempCameraPath!!)
        }
        showImageSourceDialog = false
    }

    fun launchCamera() {
        val path = FileUtils.getTempFilename(context)
        tempCameraPath = path
        val file = File(path)
        val authority = "amov.a2020157100.ecomap.utils.camera.FileUtils"
        val uri = FileProvider.getUriForFile(context, authority, file)

        cameraLauncher.launch(uri)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary) // Substituído Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.picker_photo_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background), // Substituído Color(0xFFEAEDEF) (BackgroundGray)
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { showImageSourceDialog = true } // Abre o diálogo
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (currentImagePath != null) {
                        AsyncImage(
                            model = currentImagePath,
                            contentDescription = stringResource(R.string.picker_photo_label),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    null,
                                    modifier = Modifier.padding(8.dp),
                                    tint = MaterialTheme.colorScheme.primary // Substituído Color(0xFF2E7C32)
                                )
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painterResource(R. drawable.camera),
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.picker_tap_camera), color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text(stringResource(R.string.picker_source_title)) },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.picker_camera)) },
                        leadingContent = { Icon(Icons.Default.CameraAlt, null) },
                        modifier = Modifier.clickable { launchCamera() }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.picker_gallery)) },
                        leadingContent = { Icon(Icons.Default.PhotoLibrary, null) },
                        modifier = Modifier.clickable {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}