package com.unilasalle.ecommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.unilasalle.ecommerce.ui.screens.ProductDetailScreen
import com.unilasalle.ecommerce.ui.screens.ProductListScreen
import com.unilasalle.ecommerce.ui.theme.ECommerceAppTheme
import com.unilasalle.ecommerce.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECommerceAppTheme {
                // 1. Le NavController gère l'historique de navigation (Back stack)
                val navController = rememberNavController()

                // 2. On instancie le ViewModel ici pour qu'il vive tant que l'activité est vivante.
                // Cela permet de partager les données entre la liste et le détail sans refaire d'appel API.
                val productViewModel: ProductViewModel = viewModel()

                // 3. Le NavHost définit les routes possibles
                NavHost(navController = navController, startDestination = "product_list") {

                    // --- ROUTE 1 : LA LISTE ---
                    composable("product_list") {
                        ProductListScreen(
                            viewModel = productViewModel,
                            onProductClick = { productId ->
                                // Navigation vers le détail avec l'ID en paramètre
                                navController.navigate("product_detail/$productId")
                            }
                        )
                    }

                    // --- ROUTE 2 : LE DÉTAIL ---
                    // On définit un argument "productId" de type Int
                    composable(
                        route = "product_detail/{productId}",
                        arguments = listOf(navArgument("productId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        // On récupère l'ID passé dans la route
                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0

                        ProductDetailScreen(
                            productId = productId,
                            viewModel = productViewModel,
                            onBackClick = {
                                // Retour en arrière
                                navController.popBackStack()
                            },
                            onAddToCart = {
                                // TODO : On implémentera le Panier à l'étape suivante !
                            }
                        )
                    }
                }
            }
        }
    }
}