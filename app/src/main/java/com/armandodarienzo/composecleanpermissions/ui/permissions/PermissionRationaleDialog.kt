package com.armandodarienzo.composecleanpermissions.ui.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * A reusable Composable Dialog for displaying a permission rationale message
 * and handling user actions.
 *
 * @param showDialog State that controls the visibility of the dialog.
 * @param message The rationale message explaining why the permissions are needed.
 * @param onAskAgainClick Lambda function invoked when the "Ask Again" button is clicked.
 * This typically triggers another permission request attempt.
 * @param onDismissClick Lambda function invoked when the dialog is dismissed (e.g., by clicking outside or pressing back),
 * or when the user clicks the "Dismiss" button.
 */
@Composable
fun PermissionRationaleDialog(
    showDialog: Boolean,
    message: String,
    //permissions: List<String>,
    onAskAgainClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = {
            onDismissClick()
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = elevatedCardElevation()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Permission Required",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                onDismissClick()
                            }
                        ) {
                            Text("Dismiss")
                        }
                        Button(
                            onClick = {
                                onAskAgainClick()
                            }
                        ) {
                            Text("Ask again")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PermissionRationaleDialogPreview(){
    PermissionRationaleDialog(
        showDialog = true,
        message = "These permissions are needed for a connection with the device",
        onAskAgainClick = {},
        onDismissClick = {}
    )
}