package edu.cit.ballener.lakbayayos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import edu.cit.ballener.lakbayayos.data.api.SupabaseStorageClient
import edu.cit.ballener.lakbayayos.data.model.Part
import edu.cit.ballener.lakbayayos.ui.theme.*
import edu.cit.ballener.lakbayayos.viewmodel.AuthViewModel
import edu.cit.ballener.lakbayayos.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

@Composable
fun AdminInventoryScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    inventoryViewModel: InventoryViewModel = viewModel()
) {
    var showForm by remember { mutableStateOf(false) }
    var editingPart by remember { mutableStateOf<Part?>(null) }
    var deleteTarget by remember { mutableStateOf<Part?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) { Text("< Back", color = TextSecondary) }

        Text("Motor Parts", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            "INVENTORY",
            color = RacingRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        Button(
            onClick = { editingPart = null; showForm = true },
            colors = ButtonDefaults.buttonColors(containerColor = RacingRed)
        ) { Text("+ Add Part") }

        Spacer(modifier = Modifier.height(16.dp))

        inventoryViewModel.errorMessage?.let {
            Text(it, color = ErrorRed, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        if (inventoryViewModel.loading) {
            Text("Loading parts...", color = TextSecondary)
        } else {
            LazyColumn {
                items(inventoryViewModel.parts) { part ->
                    InventoryRow(
                        part = part,
                        onEdit = { editingPart = part; showForm = true },
                        onToggleAvailability = { inventoryViewModel.toggleAvailability(part) },
                        onStockChange = { newStock -> inventoryViewModel.adjustStock(part, newStock) },
                        onDelete = { deleteTarget = part }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showForm) {
        PartFormDialog(
            existing = editingPart,
            authViewModel = authViewModel,
            submitting = inventoryViewModel.submitting,
            onDismiss = { showForm = false },
            onSubmit = { name, category, stock, available, imageUrl ->
                if (editingPart != null) {
                    inventoryViewModel.updatePart(editingPart!!.id, name, category, stock, available, imageUrl) { success ->
                        if (success) showForm = false
                    }
                } else {
                    inventoryViewModel.createPart(name, category, stock, available, imageUrl) { success ->
                        if (success) showForm = false
                    }
                }
            }
        )
    }

    deleteTarget?.let { part ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = SurfaceDark,
            title = { Text("Delete Part", color = TextPrimary) },
            text = {
                Text(
                    "Delete \"${part.name}\"? This cannot be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    inventoryViewModel.deletePart(part) { deleteTarget = null }
                }) { Text("Delete", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun InventoryRow(
    part: Part,
    onEdit: () -> Unit,
    onToggleAvailability: () -> Unit,
    onStockChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
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
            Row(modifier = Modifier.weight(1f)) {
                if (!part.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = part.imageUrl,
                        contentDescription = part.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 10.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        part.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    Text(part.category, color = RacingRed, fontSize = 11.sp, maxLines = 1)
                }
            }
            TextButton(onClick = onEdit) { Text("Edit", color = TextPrimary) }
        }

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onDelete) { Text("Delete", color = ErrorRed) }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { onStockChange(part.stockQuantity - 1) }) { Text("-", color = TextPrimary) }
            Text("${part.stockQuantity} in stock", color = TextPrimary, fontSize = 13.sp)
            TextButton(onClick = { onStockChange(part.stockQuantity + 1) }) { Text("+", color = TextPrimary) }

            Spacer(modifier = Modifier.weight(1f))

            Text(if (part.isAvailable) "Available" else "Unavailable", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = part.isAvailable,
                onCheckedChange = { onToggleAvailability() },
                colors = SwitchDefaults.colors(checkedTrackColor = RacingRed)
            )
        }
    }
}

@Composable
private fun PartFormDialog(
    existing: Part?,
    authViewModel: AuthViewModel,
    submitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String, Int, Boolean, String?) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "") }
    var stock by remember { mutableStateOf(existing?.stockQuantity?.toString() ?: "") }
    var isAvailable by remember { mutableStateOf(existing?.isAvailable ?: true) }

    // imageUrl holds whatever will actually be saved. pickedImageUri holds a
    // locally picked photo not yet uploaded. uploading tracks progress.
    var imageUrl by remember { mutableStateOf(existing?.imageUrl ?: "") }
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedImageUri = uri
            uploadError = null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text(if (existing != null) "Edit Part" else "Add New Part", color = TextPrimary) },
        text = {
            Column {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Name") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = category, onValueChange = { category = it },
                    label = { Text("Category") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = stock, onValueChange = { stock = it.filter { c -> c.isDigit() } },
                    label = { Text("Stock quantity") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text("Photo", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                val previewModel: Any? = pickedImageUri ?: imageUrl.ifBlank { null }
                if (previewModel != null) {
                    AsyncImage(
                        model = previewModel,
                        contentDescription = "Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(bottom = 8.dp)
                    )
                }

                uploadError?.let {
                    Text(it, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                }

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uploading) "Uploading..." else "Choose Photo from Device")
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = RacingRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Available for booking", color = TextPrimary, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val stockValue = stock.toIntOrNull() ?: 0
                    val token = authViewModel.accessToken

                    if (pickedImageUri != null && token != null) {
                        uploading = true
                        uploadError = null
                        coroutineScope.launch {
                            try {
                                val uploadedUrl = SupabaseStorageClient.uploadPartImage(
                                    context, pickedImageUri!!, token
                                )
                                imageUrl = uploadedUrl
                                pickedImageUri = null
                                onSubmit(name, category, stockValue, isAvailable, imageUrl)
                            } catch (e: Exception) {
                                uploadError = e.message ?: "Failed to upload photo."
                            } finally {
                                uploading = false
                            }
                        }
                    } else {
                        onSubmit(name, category, stockValue, isAvailable, imageUrl)
                    }
                },
                enabled = !submitting && !uploading && name.isNotBlank() && category.isNotBlank()
            ) { Text(if (submitting || uploading) "Saving..." else "Save", color = RacingRed) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}