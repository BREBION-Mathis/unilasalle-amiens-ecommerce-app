package com.unilasalle.ecommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.unilasalle.ecommerce.ui.screens.CartScreen
import com.unilasalle.ecommerce.ui.screens.OrderHistoryScreen
import com.unilasalle.ecommerce.ui.screens.ProductDetailScreen
import com.unilasalle.ecommerce.ui.screens.ProductListScreen
import com.unilasalle.ecommerce.ui.theme.ECommerceAppTheme
import com.unilasalle.ecommerce.viewmodel.CartViewModel
import com.unilasalle.ecommerce.viewmodel.OrderViewModel
import com.unilasalle.ecommerce.viewmodel.ProductUiState
import com.unilasalle.ecommerce.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECommerceAppTheme {
                // Avoid black screen at launch
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val productViewModel: ProductViewModel = viewModel()
                    val cartViewModel: CartViewModel = viewModel()
                    val orderViewModel: OrderViewModel = viewModel() // NOUVEAU

                    val cartCount by cartViewModel.totalCount.collectAsState()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    Scaffold(
                        floatingActionButton = {
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

                                // List of products
                                composable("product_list") {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        ProductListScreen(
                                            viewModel = productViewModel,
                                            onProductClick = { productId ->
                                                navController.navigate("product_detail/$productId")
                                            }
                                        )

                                        // Orders history
                                        SmallFloatingActionButton(
                                            onClick = { navController.navigate("history") },
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(16.dp),
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Icon(Icons.Default.DateRange, contentDescription = "Historique")
                                        }
                                    }
                                }

                                // Products details
                                composable(
                                    route = "product_detail/{productId}",
                                    arguments = listOf(navArgument("productId") { type = NavType.IntType })
                                ) { backStackEntry ->
                                    val productId = backStackEntry.arguments?.getInt("productId") ?: 0
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

                                // Basket
                                composable("cart") {
                                    CartScreen(
                                        viewModel = cartViewModel,
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }

                                // History
                                composable("history") {
                                    OrderHistoryScreen(
                                        viewModel = orderViewModel,
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
}