package org.example.dash.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.example.dash.ui.theme.AppColors

@Composable
fun AddLinkDialog(
    dialogState: AddLinkDialogState,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onSave: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.Surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add New Link",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )

                    TextButton(
                        onClick = onDismiss,
                        enabled = !dialogState.isLoading
                    ) {
                        Text(
                            text = "Close",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Error card
                dialogState.error?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.ErrorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.ErrorText,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            TextButton(onClick = onErrorDismiss) {
                                Text(
                                    text = "Dismiss",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.ErrorText
                                )
                            }
                        }
                    }
                }

                // Name field
                OutlinedTextField(
                    value = dialogState.linkName,
                    onValueChange = onNameChange,
                    label = { Text("Link Name", color = AppColors.TextSecondary) },
                    placeholder = { Text("e.g., GitHub", color = AppColors.TextDisabled) },
                    isError = dialogState.nameError != null,
                    supportingText = dialogState.nameError?.let { 
                        { Text(it, color = AppColors.ErrorText) } 
                    },
                    enabled = !dialogState.isLoading,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppColors.Background,
                        unfocusedContainerColor = AppColors.Background,
                        disabledContainerColor = AppColors.Background,
                        focusedBorderColor = AppColors.BorderFocused,
                        unfocusedBorderColor = AppColors.Border,
                        errorBorderColor = AppColors.ErrorText,
                        focusedTextColor = AppColors.TextPrimary,
                        unfocusedTextColor = AppColors.TextPrimary,
                        disabledTextColor = AppColors.TextDisabled,
                        cursorColor = AppColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // URL field
                OutlinedTextField(
                    value = dialogState.linkUrl,
                    onValueChange = onUrlChange,
                    label = { Text("URL", color = AppColors.TextSecondary) },
                    placeholder = { Text("https://example.com", color = AppColors.TextDisabled) },
                    isError = dialogState.urlError != null,
                    supportingText = dialogState.urlError?.let { 
                        { Text(it, color = AppColors.ErrorText) } 
                    },
                    enabled = !dialogState.isLoading,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppColors.Background,
                        unfocusedContainerColor = AppColors.Background,
                        disabledContainerColor = AppColors.Background,
                        focusedBorderColor = AppColors.BorderFocused,
                        unfocusedBorderColor = AppColors.Border,
                        errorBorderColor = AppColors.ErrorText,
                        focusedTextColor = AppColors.TextPrimary,
                        unfocusedTextColor = AppColors.TextPrimary,
                        disabledTextColor = AppColors.TextDisabled,
                        cursorColor = AppColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save button
                Button(
                    onClick = onSave,
                    enabled = !dialogState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        contentColor = AppColors.OnPrimary,
                        disabledContainerColor = AppColors.Primary.copy(alpha = 0.5f),
                        disabledContentColor = AppColors.OnPrimary.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (dialogState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AppColors.OnPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Link",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
