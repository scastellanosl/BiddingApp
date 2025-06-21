package com.example.biddingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biddingapp.ui.theme.BiddingAppTheme

// Importaciones para tus pantallas Composable
import com.example.biddingapp.AuctionListScreen
import com.example.biddingapp.AuctionDetailScreen
import com.example.biddingapp.CreateAuctionScreen
// No necesitamos importar Auction aquí si solo pasamos el ID a AuctionDetailScreen
// import com.example.biddingapp.data.model.Auction // Esta importación ya no será necesaria aquí

/**
 * Define las rutas de navegación para la aplicación.
 * Este objeto contiene las constantes de ruta y una función de utilidad
 * para construir la ruta de detalle con los argumentos correctamente codificados.
 */
object Screen {
    // Rutas base para cada pantalla
    const val AuctionList = "auction_list"
    // Los argumentos en la ruta de detalle ahora incluyen el ID de la subasta
    const val AuctionDetail = "auction_detail/{auctionId}" // <-- ¡Ruta simplificada para solo pasar el ID!
    const val CreateAuction = "create_auction"

    /**
     * Construye la ruta completa para navegar a la pantalla de detalle de una subasta.
     * Ahora solo necesitamos pasar el ID de la subasta, la pantalla de detalle lo cargará completo.
     * @param auctionId El ID de la subasta.
     * @return La cadena de ruta URL codificada para la navegación.
     */
    fun createAuctionDetailRoute(auctionId: String): String { // <-- ¡Función simplificada!
        val encodedId = java.net.URLEncoder.encode(auctionId, "UTF-8")
        return "auction_detail/$encodedId"
    }
}

/**
 * Composable raíz que configura el sistema de navegación para la aplicación de subastas.
 * Utiliza NavHost para definir las diferentes pantallas y cómo se navega entre ellas.
 */
@Composable
fun AuctionAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.AuctionList) {
        composable(Screen.AuctionList) {
            AuctionListScreen(
                // Lambda para navegar a la pantalla de detalle de una subasta.
                onNavigateToDetail = { auction ->
                    // Verificamos que el ID no sea nulo antes de navegar
                    auction.id?.let { id ->
                        navController.navigate(Screen.createAuctionDetailRoute(id)) // <-- ¡Ahora pasamos el ID!
                    }
                },
                // Lambda para navegar a la pantalla de creación de subastas.
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateAuction)
                }
            )
        }
        // Define el composable para la pantalla de detalle de subasta.
        // Ahora solo extraemos el auctionId
        composable(Screen.AuctionDetail) { backStackEntry ->
            // Extrae el ID de la subasta de los argumentos de navegación
            val auctionId = backStackEntry.arguments?.getString("auctionId") ?: return@composable // Retornar si el ID es nulo

            // Pasamos el ID directamente a AuctionDetailScreen.
            // AuctionDetailScreen será responsable de cargar los detalles completos de la subasta.
            AuctionDetailScreen(
                auctionId = auctionId, // <-- ¡Ahora pasamos solo el ID!
                onBack = { navController.popBackStack() } // Vuelve a la pantalla anterior
            )
        }
        // Define el composable para la pantalla de creación de subastas.
        composable(Screen.CreateAuction) {
            CreateAuctionScreen(
                onBack = { navController.popBackStack() } // Vuelve a la pantalla anterior
            )
        }
    }
}

/**
 * La actividad principal de la aplicación.
 * Es el punto de entrada de la UI de Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiddingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuctionAppNavigation()
                }
            }
        }
    }
}
