package amov.a2020157100.ecomap.model

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.screens.Branco
import amov.a2020157100.ecomap.ui.screens.CinzentoClaro
import amov.a2020157100.ecomap.ui.screens.LightGreen
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
public fun PhotoSection(
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp), // **Adicionado uma altura para ser visível**
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CinzentoClaro),
                border = BorderStroke(1.dp, LightGreen),
                onClick = { /* TODO: Abrir Câmara/Galeria */ } // Torná-lo clicável!
            ){
                Box(
                    modifier = Modifier.fillMaxSize(), // Preenche o Card pai
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ){
                        Icon(
                            painter = painterResource(R.drawable.camera), // Use o seu ícone
                            contentDescription = "Camara",
                            tint = Color.Black,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to take a photo",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                }
            }

        }
    }
}
