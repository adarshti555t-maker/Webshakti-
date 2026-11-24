package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.AgencyCatalog
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.model.WebsiteToolType
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

private data class CommandEntry(
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onSelect: () -> Unit
)

@Composable
fun GlobalCommandSearchDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onNavigatePage: (AppPage) -> Unit,
    onOpenWebsiteTool: (WebsiteToolType) -> Unit
) {
    val isHi = language == AppLanguage.HI
    var query by remember { mutableStateOf("") }

    val allEntries = remember(language) {
        buildList {
            AppPage.entries.forEach { page ->
                add(
                    CommandEntry(
                        title = AgencyCatalog.pageTitle(page, language),
                        subtitle = if (isHi) "पेज नेविगेशन" else "Open section",
                        category = if (isHi) "पेज" else "PAGE",
                        icon = pageIcon(page),
                        onSelect = { onNavigatePage(page) }
                    )
                )
            }
            AgencyCatalog.websiteTools.forEach { tool ->
                add(
                    CommandEntry(
                        title = if (isHi) tool.titleHi else tool.titleEn,
                        subtitle = if (isHi) tool.subtitleHi else tool.subtitleEn,
                        category = if (isHi) "वेब टूल" else "WEB TOOL",
                        icon = Icons.Filled.AutoAwesome,
                        onSelect = { onOpenWebsiteTool(tool.type) }
                    )
                )
            }
            AgencyCatalog.websiteTeamRoles.forEach { role ->
                add(
                    CommandEntry(
                        title = if (isHi) role.titleHi else role.titleEn,
                        subtitle = if (isHi) role.roleHi else role.roleEn,
                        category = if (isHi) "टीम" else "CREATORS",
                        icon = Icons.Filled.Groups,
                        onSelect = { onNavigatePage(AppPage.TEAM) }
                    )
                )
            }
        }
    }

    val filtered = remember(query, allEntries) {
        val q = query.trim().lowercase()
        if (q.isBlank()) allEntries.take(10)
        else allEntries.filter {
            it.title.lowercase().contains(q) ||
                it.subtitle.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = NavySurface,
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.2.dp, ElectricBlue.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = CyanPulse)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHi) "वेबसाइट टूल्स खोजें" else "Search Website Generators",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            if (isHi) "बिज़नेस, ई-कॉमर्स, SEO या पोर्टफ़ोलियो खोजें..." else "Search business, e-commerce, SEO, portfolio..."
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPulse,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (filtered.isEmpty()) {
                        item {
                            Text(
                                text = if (isHi) "कोई मेल नहीं मिला।" else "No matching website tools found.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(filtered) { entry ->
                            Surface(
                                color = NavySurface,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, NavyBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onDismiss()
                                        entry.onSelect()
                                    }
                                    .minimumInteractiveComponentSize()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = entry.icon,
                                        contentDescription = null,
                                        tint = CyanPulse,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = entry.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = entry.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = NavyElevated,
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = entry.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyanPulse,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
