package org.example.dash.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.dash.domain.model.DashLinkDomain
import org.example.dash.ui.utils.extractInitials
import org.example.dash.ui.utils.getColorForName

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel { DashboardViewModel() }
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        SearchBar(
            searchText = state.searchText,
            onSearchTextChanged = { query ->
                viewModel.processIntent(DashboardIntent.SearchQueryEntered(query))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Something went wrong",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            state.links.isNullOrEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.searchText.isEmpty()) 
                            "No links available" 
                        else 
                            "No links found for \"${state.searchText}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
            else -> {
                LinksGrid(
                    links = state.links ?: emptyList(),
                    onLinkClick = { url ->
                        viewModel.processIntent(DashboardIntent.LinkClicked(url))
                    }
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        modifier = modifier,
        placeholder = { Text("Search links...") },
        leadingIcon = {
            Text("🔍", fontSize = 20.sp)
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF1976D2),
            unfocusedBorderColor = Color(0xFFE0E0E0)
        )
    )
}

@Composable
fun LinksGrid(
    links: List<DashLinkDomain>,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(links) { link ->
            LinkItem(
                link = link,
                onClick = { onLinkClick(link.url) }
            )
        }
    }
}

@Composable
fun LinkItem(
    link: DashLinkDomain,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initials = extractInitials(link.name)
    val backgroundColor = Color(getColorForName(link.name))

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = link.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
