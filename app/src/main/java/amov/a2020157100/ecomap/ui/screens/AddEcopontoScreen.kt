package amov.a2020157100.ecomap.ui.screens

import amov.a2020157100.ecomap.ui.composables.AppBottomBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import amov.a2020157100.ecomap.ui.composables.AppBottomBar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment



/*
    val id: String,

    //localização
    val latitude: Double,
    val longitude: Double,

    //info
    val tipo: Tipo,
    val picture: String?,
    val condicao: Condicao,
    val observacoes: String?,
    val estado: Estado,

    val nConfirmados: Int = 0,
    val nEliminados: Int = 0,

    AZUL → Blue bin → for paper and cardboard

VERDE → Green bin → for glass

AMARELO → Yellow bin → for plastic and metal packaging

VERMELHO → Red bin → for hazardous waste (ou às vezes batteries/electronics, depende do país)

INDIFERENCIADO → Grey bin (ou Black bin, dependendo do sistema) → for general waste / no

 */

val blueBinColor = Color(0xFF2196F3)
val greenBinColor = Color(0xFF4CAF50)
val yellowBinColor = Color(0xFFFFEB3B)
val redBinColor = Color(0xFFC0172F)
val blackBinColor = Color(0xFF1A1A19)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEcopontoScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    //localização
    val latitude = remember { mutableStateOf("") }
    val longitude= remember { mutableStateOf("") }
    //info
    val tipo = remember { mutableStateOf("") }
    val picture= remember { mutableStateOf("") }
    val condicao= remember { mutableStateOf("") }
    val observacoes= remember { mutableStateOf("") }
    val estado= remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Mantive a sua estrutura original para o título
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Branco) // Use a sua cor definida
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "New Recycling point",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Black, // Use a sua cor definida
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Branco, // Use a sua cor definida
                    titleContentColor = Color.Black // Pode usar 'Black' se estiver definida
                ),
                navigationIcon = { // <-- 1. SETA ADICIONADA AQUI
                    IconButton(onClick = {
                        navController.popBackStack() // Ação para voltar atrás
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Voltar" // Importante para acessibilidade
                        )
                    }
                }
                // 2. Para NÃO ter o botão "Guardar", basta não definir o parâmetro 'actions'.
                // O seu código já estava correto nessa parte.
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(CinzentoClaro)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ){
                item{
                    EcoPointTypeSection(
                        selectedType = tipo.value,
                        onTypeSelected = { tipo.value = it }
                    )
                }
            }

        },

    )


}

@Composable
private fun MyTtitle(title: String){
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun EcoPointTypeSection(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val types = mapOf(
        "Blue bin" to blueBinColor,
        "Green bin" to greenBinColor,
        "Yellow bin" to yellowBinColor,
        "Red bin" to redBinColor,
        "Black bin" to blackBinColor
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MyTtitle("Eco-Point Type *")

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(types.keys.toList()) { type ->
                    val isSelected = selectedType == type
                    val buttonColor = types[type] ?: Color.Gray

                    val containerColor = if (isSelected) buttonColor else Color.Transparent
                    val contentColor = if (isSelected) Color.White else buttonColor
                    val border = if (isSelected) null else BorderStroke(2.dp, buttonColor)

                    Button(
                        onClick = { onTypeSelected(type) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = containerColor,
                            contentColor = contentColor
                        ),
                        border = border
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(type)
                    }
                }
            }
        }
    }
}

