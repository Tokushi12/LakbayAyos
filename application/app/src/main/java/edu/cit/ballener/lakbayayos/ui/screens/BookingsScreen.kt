package edu.cit.ballener.lakbayayos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.cit.ballener.lakbayayos.data.model.Booking
import edu.cit.ballener.lakbayayos.ui.theme.*
import edu.cit.ballener.lakbayayos.viewmodel.AuthViewModel
import edu.cit.ballener.lakbayayos.viewmodel.BookingsViewModel

@Composable
fun BookingsScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    bookingsViewModel: BookingsViewModel = viewModel()
) {
    val userId = authViewModel.userId

    LaunchedEffect(userId) {
        userId?.let { bookingsViewModel.loadForUser(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) { Text("< Back", color = TextSecondary) }

        Text("Your Appointments", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            "MY BOOKINGS",
            color = RacingRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        bookingsViewModel.errorMessage?.let {
            Text(it, color = ErrorRed, fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
        }

        if (bookingsViewModel.loading) {
            Text("Loading bookings...", color = TextSecondary)
        } else if (bookingsViewModel.bookings.isEmpty()) {
            Text("You have no bookings yet.", color = TextSecondary)
        } else {
            LazyColumn {
                items(bookingsViewModel.bookings) { booking ->
                    BookingCard(
                        booking = booking,
                        onCancel = {
                            userId?.let { bookingsViewModel.cancelBooking(booking.id, it) {} }
                        },
                        actionInProgress = bookingsViewModel.actionInProgress
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun BookingCard(booking: Booking, onCancel: () -> Unit, actionInProgress: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDark)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(booking.bookingDate, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                StatusBadge(booking.status)
            }
            if (booking.status == "pending") {
                Button(
                    onClick = onCancel,
                    enabled = !actionInProgress,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text("Cancel") }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        booking.items.forEach { item ->
            Text(
                "${item.partName} (${item.partCategory}) x ${item.quantity}",
                color = TextPrimary,
                fontSize = 13.sp
            )
        }

        booking.adminNotes?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Note: $it", color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "approved" -> SuccessGreen
        "declined", "cancelled" -> ErrorRed
        else -> PendingAmber
    }
    Text(
        status.uppercase(),
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp)
    )
}
