package com.dmlo.spellbook.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dmlo.spellbook.network.response.SpellResponse
import com.dmlo.spellbook.viewmodel.SpellViewModel

import androidx.compose.ui.tooling.preview.Preview
import com.dmlo.spellbook.ui.theme.SpellBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellDetailScreen(
    spellId: String,
    viewModel: SpellViewModel,
    onBack: () -> Unit
) {
    val spell by viewModel.selectedSpell.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(spellId) {
        viewModel.loadSpellById(spellId)
        viewModel.setSearchQuery("")
    }

    SpellDetailContent(
        spell = spell,
        isLoading = isLoading,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellDetailContent(
    spell: SpellResponse?,
    isLoading: Boolean,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SpellBook") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (spell != null) {
                val currentSpell = spell
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = currentSpell.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${currentSpell.type?.displayName ?: ""} ${currentSpell.circle} - ${currentSpell.school?.displayName ?: ""}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SectionTitle("Requisitos")
                    InfoRow("Execução", currentSpell.requirements.execution)
                    InfoRow("Alcance", currentSpell.requirements.range)
                    currentSpell.requirements.target?.let { InfoRow("Alvo", it) }
                    currentSpell.requirements.area?.let { InfoRow("Área", it) }
                    currentSpell.requirements.effect?.let { InfoRow("Efeito", it) }
                    InfoRow("Duração", currentSpell.requirements.duration)
                    currentSpell.requirements.resistance?.let { InfoRow("Resistência", it) }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SectionTitle("Descrição")
                    Text(
                        text = currentSpell.baseDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (currentSpell.enhancements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionTitle("Aprimoramentos")
                        currentSpell.enhancements.forEachIndexed { index, enhancement ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = "+${enhancement.additionalPmCost} PM",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = enhancement.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (index < currentSpell.enhancements.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(top = 8.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Magia não encontrada.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpellDetailContentPreview() {
    SpellBookTheme {
        SpellDetailContent(
            spell = MockData.spell,
            isLoading = false,
            onBack = {}
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SectionTitlePreview() {
    SpellBookTheme {
        SectionTitle(title = "Requisitos")
    }
}

@Preview(showBackground = true)
@Composable
fun InfoRowPreview() {
    SpellBookTheme {
        InfoRow(label = "Alcance", value = "Curto")
    }
}
