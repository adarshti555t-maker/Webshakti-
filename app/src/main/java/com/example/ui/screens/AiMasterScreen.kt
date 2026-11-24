package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.AiMasterMessage
import com.example.model.AiMasterStatus
import com.example.model.AppLanguage
import com.example.ui.components.Extruded3DCard
import com.example.ui.components.neuralGridBackground
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAccentWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen

@Composable
fun AiMasterScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    messages: List<AiMasterMessage>,
    status: AiMasterStatus,
    onAskQuestion: (String) -> Unit,
    onClearChat: () -> Unit,
    onDismissError: () -> Unit,
    onShowMessage: (String) -> Unit,
    onWhatsAppClick: () -> Unit
) {
    val isHi = language == AppLanguage.HI
    val clipboardManager = LocalClipboardManager.current
    var inputDraft by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val isBusy = status is AiMasterStatus.Loading || status is AiMasterStatus.Streaming

    val quickPrompts = remember(language) {
        if (isHi) {
            listOf(
                "मेरी डाउनलोड की गई HTML वेबसाइट को ऑनलाइन लाइव कैसे होस्ट करें?",
                "वेबसाइट मोबाइल पर सही दिखे इसके लिए CSS कैसे लिखें?",
                "अपनी बिज़नेस वेबसाइट पर SEO और Google रैंकिंग कैसे बढ़ाएँ?",
                "डोमेन नाम, DNS और SSL सर्टिफिकेट क्या होता है? आसान भाषा में बताएँ",
                "HTML फॉर्म को WhatsApp या ईमेल से कैसे जोड़ें?"
            )
        } else {
            listOf(
                "How do I host my downloaded .html website online for free or custom domain?",
                "How to fix responsive mobile layout issues in HTML & CSS?",
                "Give me an SEO checklist to rank a new business website on Google.",
                "Explain Domain, DNS, and SSL setup step by step for beginners.",
                "How can I connect my website inquiry button to WhatsApp?"
            )
        }
    }

    LaunchedEffect(messages.size, status) {
        if (messages.isNotEmpty() || status is AiMasterStatus.Streaming) {
            val targetIndex = if (status is AiMasterStatus.Streaming || status is AiMasterStatus.Loading) {
                messages.size + 1
            } else {
                messages.size
            }
            if (targetIndex > 0) {
                listState.animateScrollToItem(targetIndex.coerceAtMost(messages.size + 1))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neuralGridBackground(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 1040.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top 3D AI Master Hero Strip
            Extruded3DCard(
                depthOffset = 5.dp,
                cornerRadius = 20.dp,
                accentGlow = ElectricBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricBlue.copy(alpha = 0.12f))
                                .border(1.dp, ElectricBlue.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Psychology,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHi) "AI मास्टर" else "AI Master",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = SuccessEmerald.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = if (isHi) "लाइव Gemini AI" else "Live Gemini AI",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SuccessEmerald,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isHi) {
                                    "अपने काम, वेबसाइट, कोडिंग, SEO या बिज़नेस का कोई भी संदेह यहाँ दूर करें"
                                } else {
                                    "Clear any doubt about your work, website design, HTML/CSS, SEO, or hosting"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        if (messages.isNotEmpty()) {
                            IconButton(
                                onClick = onClearChat,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("ai_master_clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteSweep,
                                    contentDescription = if (isHi) "चैट साफ़ करें" else "Clear conversation",
                                    tint = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Doubt Starter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPrompts.forEach { suggestion ->
                            Surface(
                                onClick = { if (!isBusy) onAskQuestion(suggestion) },
                                color = NavyElevated,
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, CyanPulse.copy(alpha = 0.45f)),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = CyanPulse,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Conversation Feed
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                if (messages.isEmpty() && status is AiMasterStatus.Idle) {
                    item {
                        Surface(
                            color = NavySurface,
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, NavyBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = if (isHi) {
                                        "नमस्ते! मैं आपका AI Master हूँ"
                                    } else {
                                        "Welcome! I am your AI Master"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ElectricBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHi) {
                                        "आप मुझसे हिंदी, Hinglish या English में अपना कोई भी सवाल पूछ सकते हैं—जैसे वेबसाइट कोड ठीक करना, डाउनलोड किए HTML को लाइव करना, SEO बढ़ाना, या ऑनलाइन काम से जुड़ा कोई भी संदेह।"
                                    } else {
                                        "Ask me anything in English, Hindi, or Hinglish—fixing website code, launching your downloaded HTML site online, improving SEO, or solving any work doubt step-by-step."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = onWhatsAppClick,
                                    border = BorderStroke(1.dp, WhatsAppGreen),
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = null,
                                        tint = WhatsAppGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isHi) "टीम WhatsApp पर पूछें ($whatsAppNumber)" else "Or Chat with Team on WhatsApp ($whatsAppNumber)",
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    AiMasterChatBubble(
                        message = msg,
                        isHi = isHi,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(msg.text))
                            onShowMessage(if (isHi) "उत्तर कॉपी हो गया" else "Answer copied to clipboard")
                        }
                    )
                }

                if (status is AiMasterStatus.Loading) {
                    item(key = "loading_indicator") {
                        Surface(
                            color = NavyElevated,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.4.dp,
                                    color = ElectricBlue
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (isHi) "AI Master आपके संदेह का उत्तर तैयार कर रहा है..." else "AI Master is analyzing your question...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                if (status is AiMasterStatus.Streaming) {
                    item(key = "streaming_bubble") {
                        AiMasterChatBubble(
                            message = AiMasterMessage(
                                id = "streaming",
                                isUser = false,
                                text = status.partialText + " ▍",
                                timestamp = if (isHi) "टाइप कर रहा है..." else "Streaming..."
                            ),
                            isHi = isHi,
                            onCopy = null
                        )
                    }
                }

                if (status is AiMasterStatus.Error) {
                    item(key = "error_banner") {
                        Surface(
                            color = ErrorCoral.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ErrorCoral.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.ErrorOutline,
                                        contentDescription = null,
                                        tint = ErrorCoral,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isHi) "उत्तर प्राप्त करने में समस्या" else "Unable to reach AI Master",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = ErrorCoral,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = status.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = onDismissError) {
                                        Text(if (isHi) "बंद करें" else "Dismiss", color = TextSecondary)
                                    }
                                    val lastUserMsg = messages.lastOrNull { it.isUser }?.text
                                    if (!lastUserMsg.isNullOrBlank()) {
                                        Button(
                                            onClick = { onAskQuestion(lastUserMsg) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                                        ) {
                                            Text(if (isHi) "पुनः प्रयास करें" else "Retry", color = OnAccentWhite)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Dock
            Surface(
                color = NavySurface,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.2.dp, ElectricBlue.copy(alpha = 0.45f)),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputDraft,
                        onValueChange = { inputDraft = it },
                        placeholder = {
                            Text(
                                text = if (isHi) "यहाँ अपना संदेह या सवाल लिखें..." else "Type your work or website doubt here...",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_master_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val q = inputDraft.trim()
                            if (q.isNotBlank() && !isBusy) {
                                inputDraft = ""
                                onAskQuestion(q)
                            }
                        },
                        enabled = inputDraft.isNotBlank() && !isBusy,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            disabledContainerColor = NavyBorder
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                        shape = CircleShape,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("ai_master_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = if (isHi) "सवाल भेजें" else "Send doubt to AI Master",
                            tint = OnAccentWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiMasterChatBubble(
    message: AiMasterMessage,
    isHi: Boolean,
    onCopy: (() -> Unit)?
) {
    val containerColor = if (message.isUser) NavyElevated else NavySurface
    val borderColor = if (message.isUser) ElectricBlue.copy(alpha = 0.45f) else DeepSapphire.copy(alpha = 0.35f)
    val badgeColor = if (message.isUser) ElectricBlue else DeepSapphire

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = containerColor,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isUser) 18.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 18.dp
            ),
            border = BorderStroke(1.dp, borderColor),
            shadowElevation = if (message.isUser) 1.dp else 3.dp,
            modifier = Modifier.widthIn(max = 680.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (message.isUser) {
                            if (isHi) "आपका सवाल" else "You"
                        } else {
                            if (isHi) "AI मास्टर" else "AI Master"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                if (!message.isUser && onCopy != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onCopy,
                            border = BorderStroke(1.dp, NavyBorder),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = null,
                                tint = CyanPulse,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "कॉपी करें" else "Copy",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
