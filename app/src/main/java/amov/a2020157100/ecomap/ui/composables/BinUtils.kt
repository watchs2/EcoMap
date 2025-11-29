package amov.a2020157100.ecomap.ui.composables

import amov.a2020157100.ecomap.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amov.a2020157100.ecomap.ui.theme.BinBlue
import amov.a2020157100.ecomap.ui.theme.BinGreen
import amov.a2020157100.ecomap.ui.theme.BinYellow
import amov.a2020157100.ecomap.ui.theme.BinRed
import amov.a2020157100.ecomap.ui.theme.BinBlack

import amov.a2020157100.ecomap.ui.theme.StatusFull
import amov.a2020157100.ecomap.ui.theme.StatusGood
import amov.a2020157100.ecomap.ui.theme.StatusDamaged
import amov.a2020157100.ecomap.ui.theme.StatusMissing


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
        "Blue bin" -> BinBlue
        "Green bin" -> BinGreen
        "Yellow bin" -> BinYellow
        "Red bin" -> BinRed
        "Black bin" -> BinBlack
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
        "BOM" -> StatusGood
        "CHEIO" -> StatusFull
        "DANIFICADO" -> StatusDamaged
        "DESAPARECIDO" -> StatusMissing //StatusMissing.copy(alpha = 0.6f)
        else -> Color.Gray
    }
    return Pair(displayText, color)
}
