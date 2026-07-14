package edu.cit.ballener.lakbayayos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.cit.ballener.lakbayayos.ui.screens.AdminBookingsScreen
import edu.cit.ballener.lakbayayos.ui.screens.AdminDashboardScreen
import edu.cit.ballener.lakbayayos.ui.screens.AdminInventoryScreen
import edu.cit.ballener.lakbayayos.ui.screens.BookingsScreen
import edu.cit.ballener.lakbayayos.ui.screens.LoginScreen
import edu.cit.ballener.lakbayayos.ui.screens.PartsScreen
import edu.cit.ballener.lakbayayos.ui.screens.ProfileScreen
import edu.cit.ballener.lakbayayos.ui.screens.RegisterScreen
import edu.cit.ballener.lakbayayos.ui.screens.UserDashboardScreen
import edu.cit.ballener.lakbayayos.ui.theme.LakbayAyosTheme
import edu.cit.ballener.lakbayayos.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            LakbayAyosTheme {
                LakbayAyosApp()
            }
        }
    }
}

@Composable
fun LakbayAyosApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { role ->
                    val destination = if (role == "admin") "admin_dashboard" else "dashboard"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("dashboard") {
            UserDashboardScreen(
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                onNavigateToParts = { navController.navigate("parts") },
                onNavigateToBookings = { navController.navigate("bookings") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                onNavigateToInventory = { navController.navigate("admin_inventory") },
                onNavigateToBookings = { navController.navigate("admin_bookings") }
            )
        }

        composable("parts") {
            PartsScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("bookings") {
            BookingsScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("admin_inventory") {
            AdminInventoryScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("admin_bookings") {
            AdminBookingsScreen(onBack = { navController.popBackStack() })
        }
    }
}