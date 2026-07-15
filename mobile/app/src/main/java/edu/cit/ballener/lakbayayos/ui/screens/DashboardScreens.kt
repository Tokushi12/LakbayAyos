package edu.cit.ballener.lakbayayos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.ballener.lakbayayos.ui.theme.*
import edu.cit.ballener.lakbayayos.viewmodel.AuthViewModel

data class DashboardCardItem(
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

@Composable
fun UserDashboardScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToParts: () -> Unit = {},
    onNavigateToBookings: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val cards = listOf(
        DashboardCardItem(
            title = "Search Parts",
            description = "Find motor parts by name or category and book a service date.",
            onClick = onNavigateToParts
        ),
        DashboardCardItem(
            title = "My Bookings",
            description = "View the status of your appointments and cancel pending ones.",
            onClick = onNavigateToBookings
        ),
        DashboardCardItem(
            title = "Edit Profile",
            description = "Update your name, phone number, or reset your password.",
            onClick = onNavigateToProfile
        )
    )

    DashboardScaffold(
        title = "Dashboard",
        subtitle = "WELCOME",
        cards = cards,
        authViewModel = authViewModel,
        onLogout = onLogout
    )
}

@Composable
fun AdminDashboardScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToInventory: () -> Unit = {},
    onNavigateToBookings: () -> Unit = {}
) {
    val cards = listOf(
        DashboardCardItem(
            title = "Motor Parts Inventory",
            description = "Add new parts, adjust stock, and toggle availability for booking.",
            onClick = onNavigateToInventory
        ),
        DashboardCardItem(
            title = "Manage Bookings",
            description = "Review appointment requests, view customer details, and confirm or decline.",
            onClick = onNavigateToBookings
        )
    )

    DashboardScaffold(
        title = "Dashboard",
        subtitle = "ADMIN",
        cards = cards,
        authViewModel = authViewModel,
        onLogout = onLogout
    )
}

@Composable
private fun DashboardScaffold(
    title: String,
    subtitle: String,
    cards: List<DashboardCardItem>,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Lakbay Ayos", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = RacingRed)
            ) {
                Text("Log out")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(title, color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(
            subtitle,
            color = RacingRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
        )

        cards.forEach { card ->
            DashboardCard(card)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DashboardCard(item: DashboardCardItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDark)
            .clickable { item.onClick() }
            .padding(20.dp)
    ) {
        Text(item.title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(item.description, color = TextSecondary, fontSize = 14.sp)
    }
}

@Preview(showBackground = true, name = "User Dashboard")
@Composable
fun UserDashboardScreenPreview() {
    LakbayAyosTheme {
        UserDashboardScreen(authViewModel = AuthViewModel(), onLogout = {})
    }
}

@Preview(showBackground = true, name = "Admin Dashboard")
@Composable
fun AdminDashboardScreenPreview() {
    LakbayAyosTheme {
        AdminDashboardScreen(authViewModel = AuthViewModel(), onLogout = {})
    }
}