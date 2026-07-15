package edu.cit.ballener.lakbayayos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import edu.cit.ballener.lakbayayos.viewmodel.BookingsViewModel

private val STATUS_FILTERS = listOf("all", "pending", "approved", "declined", "cancelled")

@Composable
fun AdminBookingsScreen(
    onBack: () -> Unit,
    bookingsViewModel: BookingsViewModel = viewModel()
) {
    var statusFilter by remember { mutableStateOf("all") }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }

    LaunchedEffect(Unit) { bookingsViewModel.loadAll() }

    val filtered = if (statusFilter == "all") {
        bookingsViewModel.bookings
    } else {
        bookingsViewModel.bookings.filter { it.status == statusFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) { Text("< Back", color = TextSecondary) }

        Text("Manage Appointments", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "BOOKINGS",
            color = RacingRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            STATUS_FILTERS.forEach { status ->
                FilterChip(
                    selected = statusFilter == status,
                    onClick = { statusFilter = status },
                    label = { Text(status.replaceFirstChar { it.uppercase() }) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RacingRed,
                        selectedLabelColor = TextPrimary
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        bookingsViewModel.errorMessage?.let {
            Text(it, color = ErrorRed, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        if (bookingsViewModel.loading) {
            Text("Loading bookings...", color = TextSecondary)
        } else if (filtered.isEmpty()) {
            Text("No bookings found for this filter.", color = TextSecondary)
        } else {
            LazyColumn {
                items(filtered) { booking ->
                    AdminBookingRow(booking = booking, onClick = { selectedBooking = booking })
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    selectedBooking?.let { booking ->
        BookingDetailsDialog(
            booking = booking,
            actionInProgress = bookingsViewModel.actionInProgress,
            onDismiss = { selectedBooking = null },
            onApprove = { notes ->
                bookingsViewModel.updateStatus(booking.id, "approved", notes) { selectedBooking = null }
            },
            onDecline = { notes ->
                bookingsViewModel.updateStatus(booking.id, "declined", notes) { selectedBooking = null }
            },
            onCancel = { notes ->
                bookingsViewModel.updateStatus(booking.id, "cancelled", notes) { selectedBooking = null }
            }
        )
    }
}

@Composable
private fun AdminBookingRow(booking: Booking, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDark)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(booking.userFullName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(booking.bookingDate, color = TextSecondary, fontSize = 12.sp)
                StatusBadge(booking.status)
            }
            TextButton(onClick = onClick) { Text("View Details", color = RacingRed) }
        }
    }
}

@Composable
private fun BookingDetailsDialog(
    booking: Booking,
    actionInProgress: Boolean,
    onDismiss: () -> Unit,
    onApprove: (String?) -> Unit,
    onDecline: (String?) -> Unit,
    onCancel: (String?) -> Unit
) {
    var notes by remember { mutableStateOf(booking.adminNotes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Booking Details", color = TextPrimary) },
        text = {
            Column {
                Text("Customer: ${booking.userFullName}", color = TextPrimary, fontSize = 14.sp)
                Text("Date: ${booking.bookingDate}", color = TextPrimary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(booking.status)
                Spacer(modifier = Modifier.height(12.dp))

                Text("Parts Requested", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                booking.items.forEach {
                    Text("${it.partName} (${it.partCategory}) x ${it.quantity}", color = TextPrimary, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Admin notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            if (booking.status == "pending") {
                TextButton(onClick = { onApprove(notes.ifBlank { null }) }, enabled = !actionInProgress) {
                    Text("Approve", color = SuccessGreen)
                }
            } else if (booking.status == "approved") {
                TextButton(onClick = { onCancel(notes.ifBlank { null }) }, enabled = !actionInProgress) {
                    Text("Cancel Booking", color = ErrorRed)
                }
            }
        },
        dismissButton = {
            Row {
                if (booking.status == "pending") {
                    TextButton(onClick = { onDecline(notes.ifBlank { null }) }, enabled = !actionInProgress) {
                        Text("Decline", color = ErrorRed)
                    }
                }
                TextButton(onClick = onDismiss) { Text("Close", color = TextSecondary) }
            }
        }
    )
}
