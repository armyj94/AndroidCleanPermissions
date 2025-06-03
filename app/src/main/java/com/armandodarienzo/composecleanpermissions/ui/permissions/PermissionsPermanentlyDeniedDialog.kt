package com.armandodarienzo.composecleanpermissions.ui.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.armandodarienzo.composecleanpermissions.ui.base.getLocalizedPermissionDisplayNames

/**
 * A reusable Composable Dialog for informing the user that permissions have been
 * permanently denied and directing them to OS settings.
 *
 * @param showDialog Boolean that controls the visibility of the dialog.
 * @param permanentlyDeniedPermissions The list of permission names that have been permanently denied.
 * @param onGoToSettingsClick Lambda function invoked when the "Go to Settings" button is clicked.
 * This lambda should typically navigate the user to the app's settings screen.
 * @param onDismissClick Lambda function invoked when the dialog is dismissed (e.g., by clicking outside or pressing back),
 * or when the user clicks the "Dismiss" button.
 */
@Composable
fun PermissionsPermanentlyDeniedDialog(
    showDialog: Boolean,
    permanentlyDeniedPermissions: List<String>,
    onGoToSettingsClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    val context = LocalContext.current

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
                        text = "Permissions Permanently Denied",
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "These permissions required for the feature have been permanently denied. Please allow them in the OS settings:",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    context.getLocalizedPermissionDisplayNames(permanentlyDeniedPermissions).forEach{ permissionGroupName ->
                        Text(
                            text = "- $permissionGroupName", // Format as a list item
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                    }


                    Spacer(modifier = Modifier.height(16.dp))

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
                                onGoToSettingsClick() // Trigger the go to settings action
                            }
                        ) {
                            Text("Go to Settings")
                        }
                    }
                }
            }
        }
    }
}