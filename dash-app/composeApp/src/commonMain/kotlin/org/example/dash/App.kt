package org.example.dash

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.example.dash.ui.dashboard.DashboardScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        DashboardScreen()
    }
}