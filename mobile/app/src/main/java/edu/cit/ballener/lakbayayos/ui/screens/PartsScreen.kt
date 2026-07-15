package edu.cit.ballener.lakbayayos.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.cit.ballener.lakbayayos.data.model.Part
import edu.cit.ballener.lakbayayos.ui.theme.*
import edu.cit.ballener.lakbayayos.viewmodel.AuthViewModel
import edu.cit.ballener.lakbayayos.viewmodel.PartsViewModel
import java.util.Calendar

@Composable
fun PartsScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    partsViewModel: PartsViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    var bookingDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) { Text("< Back", color = TextSecondary) }

        Text("Search Parts", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            "MOTOR PARTS",
            color = RacingRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search by name or category") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { partsViewModel.search(query) },
                colors = ButtonDefaults.buttonColors(containerColor = RacingRed)
            ) { Text("Go") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        partsViewModel.errorMessage?.let {
            Text(it, color = ErrorRed, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
        }
        partsViewModel.successMessage?.let {
            Text(it, color = SuccessGreen, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        if (partsViewModel.loading) {
            Text("Loading parts...", color = TextSecondary)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(partsViewModel.parts) { part ->
                    PartRow(part = part, onAdd = { partsViewModel.addToCart(part) })
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        if (partsViewModel.cart.isNotEmpty()) {
            Divider(color = BorderDark, modifier = Modifier.padding(vertical = 12.dp))
            Text("Your Selection", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            partsViewModel.cart.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(item.category, color = TextSecondary, fontSize = 12.sp)
                    }
                    IconButtonText("-") { partsViewModel.updateQuantity(item.partId, item.quantity - 1) }
                    Text("${item.quantity}", color = TextPrimary, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButtonText("+") { partsViewModel.updateQuantity(item.partId, item.quantity + 1) }
                    IconButtonText("x") { partsViewModel.removeFromCart(item.partId) }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (bookingDate.isNotEmpty()) {
                Text("Date: $bookingDate", color = TextPrimary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row {
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                bookingDate = "%04d-%02d-%02d".format(year, month + 1, day)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            datePicker.minDate = System.currentTimeMillis()
                        }.show()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Choose Date") }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val id = authViewModel.userId
                        if (id != null && bookingDate.isNotEmpty()) {
                            partsViewModel.submitBooking(id, bookingDate) { bookingDate = "" }
                        }
                    },
                    enabled = bookingDate.isNotEmpty() && !partsViewModel.submitting,
                    colors = ButtonDefaults.buttonColors(containerColor = RacingRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (partsViewModel.submitting) "Booking..." else "Book")
                }
            }
        }
    }
}

@Composable
private fun PartRow(part: Part, onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDark)
            .padding(14.dp)
    ) {
        if (!part.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = part.imageUrl,
                contentDescription = part.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 10.dp)
            )
        }
        Text(part.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(part.category, color = RacingRed, fontSize = 11.sp)
        Text("${part.stockQuantity} in stock", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
            Text("Add to Booking")
        }
    }
}

@Composable
private fun IconButtonText(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, contentPadding = PaddingValues(4.dp)) {
        Text(label, color = TextPrimary)
    }
}