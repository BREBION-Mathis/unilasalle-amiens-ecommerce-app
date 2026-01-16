package com.unilasalle.ecommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.unilasalle.ecommerce.ui.screens.CartScreen
import com.unilasalle.ecommerce.ui.screens.ProductDetailScreen
import com.unilasalle.ecommerce.ui.screens.ProductListScreen
import com.unilasalle.ecommerce.ui.theme.ECommerceAppTheme
import com.unilasalle.ecommerce.viewmodel.CartViewModel
import com.unilasalle.ecommerce.viewmodel.ProductUiState
import com.unilasalle.ecommerce.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECommerceAppTheme {
                // 1. Allows you to manage the navigation history
                val navController = rememberNavController()

                // ViewModel instanciations
                val productViewModel: ProductViewModel = viewModel()
                val cartViewModel: CartViewModel = viewModel()

                val cartCount by cartViewModel.totalCount.collectAsState()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route


                // 3. Routes management
                Scaffold(
                    floatingActionButton = {
                        // On n'affiche le bouton Panier que sur la liste des produits
                        if (currentRoute == "product_list") {
                            FloatingActionButton(
                                onClick = { navController.navigate("cart") }
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (cartCount > 0) {
                                            Badge { Text("$cartCount") }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Panier")
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(navController = navController, startDestination = "product_list") {

                            // Fist route: products list
                            composable("product_list") {
                                ProductListScreen(
                                    viewModel = productViewModel,
                                    onProductClick = { productId ->
                                        navController.navigate("product_detail/$productId")
                                    }
                                )
                            }

                            // Seconde route: product details
                            composable(
                                route = "product_detail/{productId}",
                                arguments = listOf(navArgument("productId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val productId = backStackEntry.arguments?.getInt("productId") ?: 0

                                // We keep the same instance of ProductViewModel to avoid multiple API calls
                                val products by productViewModel.uiState.collectAsState()
                                val product = (products as? ProductUiState.Success)
                                    ?.products?.find { it.id == productId }

                                ProductDetailScreen(
                                    productId = productId,
                                    viewModel = productViewModel,
                                    onBackClick = { navController.popBackStack() },

                                    onAddToCart = {
                                        if (product != null) {
                                            cartViewModel.addToCart(product)
                                        }
                                    }
                                )
                            }

                            // Third route : cart
                            composable("cart") {
                                CartScreen(
                                    viewModel = cartViewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}