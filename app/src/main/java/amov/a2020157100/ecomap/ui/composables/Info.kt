package amov.a2020157100.ecomap.ui.composables

import amov.a2020157100.ecomap.R
import amov.a2020157100.ecomap.ui.screens.Black
import amov.a2020157100.ecomap.ui.screens.Green
import amov.a2020157100.ecomap.ui.screens.Red
import amov.a2020157100.ecomap.ui.screens.blackBinColor
import amov.a2020157100.ecomap.ui.screens.blueBinColor
import amov.a2020157100.ecomap.ui.screens.greenBinColor
import amov.a2020157100.ecomap.ui.screens.pendingColor
import amov.a2020157100.ecomap.ui.screens.redBinColor
import amov.a2020157100.ecomap.ui.screens.yellowBinColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Ecopontos
//type color string
val types = mapOf(
    "Blue bin" to blueBinColor, // Ecoponto Azul(pt) Blue Recycling Bin(En)
    "Green bin" to greenBinColor, //Ecoponto Verde(pt) Green Recycling Bin(En)
    "Yellow bin" to yellowBinColor, // Ecoponto Amarelo(pt) Yellow Recycling Bin(En)
    "Red bin" to redBinColor,       //Pilhão(pt)    Red Battery Recycling Bin(En)
)

//fim ecoponto


//Estado do Ecoponto


//condição do ecoponto

/*
    val conditionOptions = listOf(
        "BOM" to Pair("Bom", Green),
        "CHEIO" to Pair("Cheio", pendingColor),
        "DANIFICADO" to Pair("Danificado", Red),
        "DESAPARECIDO" to Pair("Desaparecido", Black.copy(alpha = 0.6f))
    )
*/
//details



@Composable
fun getBinStringRes(type: String?): Int {
    return when (type) {
        "Blue bin" -> R.string.bin_blue
        "Green bin" -> R.string.bin_green
        "Yellow bin" -> R.string.bin_yellow
        "Red bin" -> R.string.bin_red
        "Black bin" -> R.string.bin_black
        else -> R.string.bin_unknown
    }
}



//List

// Funções auxiliares (StatusBadge, getBinColor, etc.) mantêm-se iguais
@Composable
fun StatusBadge(text: String, color: Color) {
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
fun getBinColor(type: String): Color {
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
fun getConditionDisplay(state: String): Pair<Int, Color> {
    val displayText = when (state) {
        "BOM" -> R.string.binState_good
        "CHEIO" -> R.string.binState_full
        "DANIFICADO" -> R.string.binState_damaged
        "DESAPARECIDO" -> R.string.binState_missing
        else -> R.string.binState_unknown
    }
    val color = when (state) {
        "BOM" -> Green
        "CHEIO" -> pendingColor
        "DANIFICADO" -> Red
        "DESAPARECIDO" -> Black.copy(alpha = 0.6f)
        else -> Color.Gray
    }
    return Pair(displayText, color)
}
