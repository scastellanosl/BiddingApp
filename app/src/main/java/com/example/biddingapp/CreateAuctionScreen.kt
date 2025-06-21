// CreateAuctionScreen.kt
package com.example.biddingapp // Asegúrate de que este sea el paquete correcto

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biddingapp.ui.theme.BiddingAppTheme
import com.example.biddingapp.viewmodel.CreateAuctionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Pantalla para crear una nueva subasta
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAuctionScreen(
    onBack: () -> Unit,
    viewModel: CreateAuctionViewModel = viewModel() // Inyecta el ViewModel
) {
    // Obtiene el contexto actual para usar DatePickerDialog
    val context = LocalContext.current

    // Observa el estado del ViewModel
    val auctionName by viewModel.auctionName.collectAsStateWithLifecycle()
    val minimumOffer by viewModel.minimumOffer.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val imageUrl by viewModel.imageUrl.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()

    // Manejo de mensajes de éxito/error temporales
    if (successMessage != null) {
        LaunchedEffect(successMessage) {
            println("Éxito: $successMessage")
            kotlinx.coroutines.delay(2000)
            viewModel.clearSuccessMessage() // Llama a la función pública del ViewModel
            onBack() // Navega de vuelta después del éxito
        }
    }

    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            println("Error: $errorMessage")
            kotlinx.coroutines.delay(3000)
            viewModel.clearErrorMessage() // Llama a la función pública del ViewModel
        }
    }

    // Calendario para el DatePickerDialog (inicialmente con la fecha actual)
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Formateador de fecha para mostrar en la UI y para enviar a la API
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato consistente con tu API/db.json

    // DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
            val formattedDate = dateFormatter.format(newCalendar.time)
            viewModel.updateSelectedDate(formattedDate) // Actualiza el ViewModel con la fecha seleccionada
        }, year, month, day
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Crear nueva Subasta",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .background(Color(0xFFF8F8F8))
        ) {
            // Mensajes de estado
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp))
            }


            Text("Nombre Subasta", fontSize = 16.sp, color = Color(0xFF555555), modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = auctionName,
                onValueChange = { viewModel.updateAuctionName(it) }, // Actualiza el ViewModel
                label = { Text("Nombre Subasta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors( // <-- ¡CONFIGURACIÓN DE COLORES RESTAURADA Y CORREGIDA!
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White, // Typo corregido aquí
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                )
            )
            Spacer(Modifier.height(24.dp))

            Text("Fecha de subasta", fontSize = 16.sp, color = Color(0xFF555555), modifier = Modifier.padding(bottom = 8.dp))
            Button(
                onClick = {
                    datePickerDialog.show() // <-- Ahora mostramos el selector de fecha!
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Muestra la fecha seleccionada por el ViewModel
                    Text(selectedDate.ifEmpty { "Seleccionar Fecha" }, color = Color(0xFF333333), fontSize = 16.sp)
                    Icon(Icons.Filled.DateRange, contentDescription = "Seleccionar fecha", tint = Color(0xFF555555))
                }
            }
            Spacer(Modifier.height(24.dp))

            Text("URL de Imagen (opcional)", fontSize = 16.sp, color = Color(0xFF555555), modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { viewModel.updateImageUrl(it) },
                label = { Text("Ej: https://example.com/image.jpg") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri), // Tipo de teclado para URLs
                colors = OutlinedTextFieldDefaults.colors( // <-- ¡CONFIGURACIÓN DE COLORES RESTAURADA Y CORREGIDA!
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                )
            )
            Spacer(Modifier.height(24.dp))


            Text("Oferta Mínima", fontSize = 16.sp, color = Color(0xFF555555), modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = minimumOffer,
                onValueChange = { viewModel.updateMinimumOffer(it) }, // Actualiza el ViewModel
                label = { Text("100.000") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors( // <-- ¡CONFIGURACIÓN DE COLORES RESTAURADA Y CORREGIDA!
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                )
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.createAuction(onBack) }, // Llama a la función del ViewModel
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF06292)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(14.dp),
                enabled = !isLoading // Deshabilita el botón mientras se está cargando
            ) {
                Text("Guardar", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateAuctionScreen() {
    BiddingAppTheme{
        CreateAuctionScreen(onBack = {})
    }
}
