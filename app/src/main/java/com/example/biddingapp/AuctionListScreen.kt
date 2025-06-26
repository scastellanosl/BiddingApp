// AuctionListScreen.kt
package com.example.biddingapp // Asegúrate de que este sea el paquete correcto

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biddingapp.data.model.model.Auction
import com.example.biddingapp.ui.theme.BiddingAppTheme
import com.example.biddingapp.viewmodel.AuctionListViewModel

// Pantalla de la lista de subastas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionListScreen(
    onNavigateToDetail: (Auction) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: AuctionListViewModel = viewModel()
) {
    // Observa el StateFlows del ViewModel. collectAsStateWithLifecycle es recomendado
    // para observar LiveData/StateFlows en Compose de forma segura para el ciclo de vida.
    val auctions by viewModel.auctions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Efecto para lanzar la carga inicial de subastas cuando el composable entra en el árbol.
    // 'Unit' como clave asegura que se ejecuta solo una vez.
    LaunchedEffect(Unit) {
        viewModel.fetchAuctions()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Subastas",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp) // Añadido padding horizontal
                .background(Color(0xFFF8F8F8)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sección de búsqueda
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp) // padding vertical
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    label = { Text("Buscar por nombre o fecha") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    trailingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") }
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.performSearch() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF06292)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Buscar", color = Color.White)
                }
            }

            // Encabezados de la tabla
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8E8E8), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nombre", fontWeight = FontWeight.Bold, color = Color(0xFF333333), modifier = Modifier.weight(2f), fontSize = 14.sp)
                Text("Oferta Máxima", fontWeight = FontWeight.Bold, color = Color(0xFF333333), modifier = Modifier.weight(1.5f), fontSize = 14.sp)
                Text("Inscritos", fontWeight = FontWeight.Bold, color = Color(0xFF333333), modifier = Modifier.weight(1f), fontSize = 14.sp)
                Text("Fecha Final", fontWeight = FontWeight.Bold, color = Color(0xFF333333), modifier = Modifier.weight(1.5f), fontSize = 14.sp)
            }
            Spacer(Modifier.height(8.dp))

            // Mostrar estado de carga, error o la lista de subastas
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(16.dp))
                    Button(onClick = { viewModel.fetchAuctions() }) {
                        Text("Reintentar")
                    }
                }
            } else if (auctions.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay subastas disponibles.", modifier = Modifier.padding(16.dp))
                }
            } else {
                // LAZYCOLUMN CON LA LISTA DE SUBASTAS
                LazyColumn(
                    modifier = Modifier.weight(1f) // Ocupa el espacio restante
                ) {
                    // Asegúrate de que 'items' esté importado de 'androidx.compose.foundation.lazy.items'
                    items(auctions) { auction ->
                        AuctionItem(auction = auction, onClick = onNavigateToDetail)
                    }
                }
            }


            // Botón "Nueva"
            Button(
                onClick = onNavigateToCreate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF06292)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                Text("Nueva", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// AuctionItem y Preview para AuctionListScreen... (mantenerlos igual)
// Componente para un elemento individual de la lista de subastas
@Composable
fun AuctionItem(auction: Auction, onClick: (Auction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick(auction) }
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        auction.name?.let { Text(it, color = Color(0xFF333333), fontSize = 16.sp, modifier = Modifier.weight(2f)) }
        Text("$${auction.maxOffer}", color = Color(0xFF333333), fontSize = 16.sp, modifier = Modifier.weight(1.5f))
        Text(auction.inscriptions.toString(), color = Color(0xFF333333), fontSize = 16.sp, modifier = Modifier.weight(1f))
        auction.endDate?.let { Text(it, color = Color(0xFF333333), fontSize = 16.sp, modifier = Modifier.weight(1.5f)) }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAuctionListScreen() {
    BiddingAppTheme  {
        AuctionListScreen(onNavigateToDetail = {}, onNavigateToCreate = {})
    }
}
