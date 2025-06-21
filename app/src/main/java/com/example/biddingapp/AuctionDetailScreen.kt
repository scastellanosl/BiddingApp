// AuctionDetailScreen.kt
package com.example.biddingapp // Asegúrate de que este sea el paquete correcto

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.biddingapp.data.model.model.Bid
import com.example.biddingapp.ui.theme.BiddingAppTheme
import com.example.biddingapp.ui.viewmodel.AuctionDetailViewModel // Asegúrate de que esta importación sea correcta

// Pantalla de detalle de una subasta
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionDetailScreen(
    auctionId: String,
    onBack: () -> Unit,
    viewModel: AuctionDetailViewModel = viewModel()
) {
    val auctionDetail by viewModel.auctionDetail.collectAsStateWithLifecycle()
    val bidsForAuction by viewModel.bidsForAuction.collectAsStateWithLifecycle() // <-- Nuevo estado para las pujas
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()

    var bidAmount by remember { mutableStateOf("") }
    var userNameInput by remember { mutableStateOf("") }

    LaunchedEffect(auctionId) {
        viewModel.fetchAuctionDetail(auctionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        auctionDetail?.name ?: "Cargando...",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(16.dp))
                Button(onClick = { viewModel.fetchAuctionDetail(auctionId) }) {
                    Text("Reintentar")
                }
                LaunchedEffect(errorMessage) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearErrorMessage()
                }
            }
        } else if (auctionDetail == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Detalles de la subasta no encontrados.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .background(Color(0xFFF8F8F8)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                successMessage?.let {
                    Text(it, color = Color.Green, modifier = Modifier.padding(bottom = 8.dp))
                    LaunchedEffect(it) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.clearSuccessMessage()
                    }
                }

                if (auctionDetail?.imageUrl != null && auctionDetail!!.imageUrl!!.startsWith("http")) {
                    AsyncImage(
                        model = auctionDetail!!.imageUrl,
                        contentDescription = "Imagen del elemento a subastar",
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.placeholder_image),
                        error = painterResource(id = R.drawable.placeholder_image),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_image),
                        contentDescription = "Imagen del elemento a subastar",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .padding(24.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    auctionDetail?.description?.let {
                        Text("Descripción: $it", fontSize = 16.sp, color = Color(0xFF555555), modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Text("Oferta Máxima: $${auctionDetail!!.maxOffer}", fontSize = 18.sp, color = Color(0xFF555555), modifier = Modifier.padding(bottom = 8.dp))
                    Text("Inscritos: ${auctionDetail!!.inscriptions}", fontSize = 18.sp, color = Color(0xFF555555), modifier = Modifier.padding(bottom = 8.dp))
                    Text("Fecha Final: ${auctionDetail!!.endDate}", fontSize = 18.sp, color = Color(0xFF555555))

                    if (auctionDetail?.isActive == false) {
                        Spacer(Modifier.height(16.dp))
                        Text("Ganador: ${auctionDetail?.winnerId ?: "N/A"}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Text("Puja Ganadora: $${auctionDetail?.winningBid ?: "N/A"}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
                Spacer(Modifier.height(16.dp))

                if (auctionDetail?.isActive == true) {
                    // Campo para el nombre del usuario que hace la puja
                    OutlinedTextField(
                        value = userNameInput,
                        onValueChange = { userNameInput = it },
                        label = { Text("Tu Nombre (empleado)") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                        )
                    )
                    Spacer(Modifier.height(16.dp)) // Espacio después del nuevo campo

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Puja/Oferta:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333), modifier = Modifier.padding(end = 8.dp))
                        OutlinedTextField(
                            value = bidAmount,
                            onValueChange = { bidAmount = it },
                            label = { Text("Monto") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val bid = bidAmount.toDoubleOrNull()
                                if (userNameInput.isBlank()) {
                                    viewModel.clearErrorMessage()
                                    viewModel._errorMessage.value = "Por favor, ingresa tu nombre."
                                } else if (bid != null && bid > (auctionDetail?.maxOffer ?: 0.0)) {
                                    auctionDetail?.id?.let { id ->
                                        viewModel.placeBid(id, bid, userNameInput)
                                    }
                                } else {
                                    viewModel.clearErrorMessage()
                                    viewModel._errorMessage.value = "La puja debe ser un número válido y mayor que la oferta actual."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF06292)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("Guardar", color = Color.White)
                        }
                    }
                } else {
                    Text(
                        "La subasta ha finalizado.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))

                // --- TABLA DE INSCRITOS / PUJAS ---
                if (bidsForAuction.isNotEmpty()) {
                    Text(
                        "Pujas Registradas",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    // Encabezados de la tabla
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8E8E8), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Empleado", fontWeight = FontWeight.Bold, color = Color(0xFF333333), modifier = Modifier.weight(1.5f))
                        Text("Monto", fontWeight = FontWeight.Bold, color = Color(0xFF333333), modifier = Modifier.weight(1f))
                        Text("", modifier = Modifier.weight(0.7f)) // Espacio para el botón Guardar
                    }
                    Spacer(Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(200.dp) // Altura fija para la tabla
                    ) {
                        items(bidsForAuction) { bid ->
                            BidItemRow(bid = bid, onUpdateBid = { bidId, newAmount ->
                                viewModel.updateBidAmount(bidId, newAmount)
                            })
                        }
                    }
                } else {
                    Text(
                        "No hay pujas registradas para esta subasta.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))

                // Botones de acción (Finalizar, Eliminar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { auctionDetail?.id?.let { viewModel.finishAuction(it) } },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF06292)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(12.dp),
                        enabled = auctionDetail?.isActive == true
                    ) {
                        Text("Finalizar", color = Color.White, fontSize = 16.sp)
                    }
                    Button(
                        onClick = { auctionDetail?.id?.let { viewModel.deleteAuction(it) } },
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text("Eliminar", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// Componente individual para una fila de la puja en la tabla
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidItemRow(bid: Bid, onUpdateBid: (bidId: String, newAmount: String) -> Unit) {
    var editedAmount by remember { mutableStateOf(bid.amount.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(bid.userId, color = Color(0xFF333333), fontSize = 16.sp, modifier = Modifier.weight(1.5f))
        OutlinedTextField(
            value = editedAmount,
            onValueChange = { editedAmount = it },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = Color(0xFF333333)) // Estilo para el texto
        )
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = { onUpdateBid(bid.id, editedAmount) },
            modifier = Modifier.weight(0.7f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF06292)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text("Subir", color = Color.White, fontSize = 14.sp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAuctionDetailScreen() {
    BiddingAppTheme {
        AuctionDetailScreen(
            auctionId = "auction_test_id", // Un ID de prueba
            onBack = {}
        )
    }
}
