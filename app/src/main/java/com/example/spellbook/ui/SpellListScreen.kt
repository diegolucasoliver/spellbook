package com.dmlo.spellbook.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmlo.spellbook.network.response.SpellResponse
import com.dmlo.spellbook.network.response.SpellSchool
import com.dmlo.spellbook.network.response.SpellType
import com.dmlo.spellbook.ui.theme.SpellBookTheme
import com.dmlo.spellbook.viewmodel.SortOrder
import com.dmlo.spellbook.viewmodel.SpellViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellListScreen(
    viewModel: SpellViewModel,
    onSpellClick: (String) -> Unit
) {
    val spells by viewModel.spells.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val filterType by viewModel.filterType.collectAsState()
    val filterSchool by viewModel.filterSchool.collectAsState()
    val filterCircle by viewModel.filterCircle.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    SpellListContent(
        spells = spells,
        isLoading = isLoading,
        filterType = filterType,
        filterSchool = filterSchool,
        filterCircle = filterCircle,
        sortOrder = sortOrder,
        searchQuery = searchQuery,
        onTypeSelected = viewModel::setFilterType,
        onSchoolSelected = viewModel::setFilterSchool,
        onCircleSelected = viewModel::setFilterCircle,
        onSortOrderChanged = viewModel::setSortOrder,
        onSearchQueryChanged = viewModel::setSearchQuery,
        onSpellClick = onSpellClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellListContent(
    spells: List<SpellResponse>,
    isLoading: Boolean,
    filterType: SpellType?,
    filterSchool: SpellSchool?,
    filterCircle: Int?,
    sortOrder: SortOrder,
    searchQuery: String,
    onTypeSelected: (SpellType?) -> Unit,
    onSchoolSelected: (SpellSchool?) -> Unit,
    onCircleSelected: (Int?) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSpellClick: (String) -> Unit
) {
    var isSearchExpanded by remember { mutableStateOf(searchQuery.isNotEmpty()) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isEmpty()) {
            isSearchExpanded = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchExpanded) {
                        TextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChanged,
                            placeholder = { Text("Pesquisar magia...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        Text("SpellBook")
                    }
                },
                actions = {
                    if (isSearchExpanded) {
                        IconButton(onClick = {
                            isSearchExpanded = false
                            onSearchQueryChanged("")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar pesquisa")
                        }
                    } else {
                        IconButton(onClick = { isSearchExpanded = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                FilterSection(
                    selectedType = filterType,
                    onTypeSelected = onTypeSelected,
                    selectedSchool = filterSchool,
                    onSchoolSelected = onSchoolSelected,
                    selectedCircle = filterCircle,
                    onCircleSelected = onCircleSelected,
                    currentSortOrder = sortOrder,
                    onSortOrderChanged = onSortOrderChanged
                )

                if (!isLoading) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(spells) { spell ->
                            SpellItem(spell, onClick = { 
                                onSpellClick(spell.documentId)
                            })
                        }
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    selectedType: SpellType?,
    onTypeSelected: (SpellType?) -> Unit,
    selectedSchool: SpellSchool?,
    onSchoolSelected: (SpellSchool?) -> Unit,
    selectedCircle: Int?,
    onCircleSelected: (Int?) -> Unit,
    currentSortOrder: SortOrder,
    onSortOrderChanged: (SortOrder) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterDropdown(
                label = "Tipo",
                options = SpellType.entries,
                selectedOption = selectedType,
                onOptionSelected = onTypeSelected,
                getDisplayName = { it.displayName },
                modifier = Modifier.weight(1f)
            )

            FilterDropdown(
                label = "Círculo",
                options = (1..5).toList(),
                selectedOption = selectedCircle,
                onOptionSelected = { onCircleSelected(it) },
                getDisplayName = { it.toString() },
                modifier = Modifier.weight(1f)
            )

            FilterDropdown(
                label = "Escola",
                options = SpellSchool.entries,
                selectedOption = selectedSchool,
                onOptionSelected = onSchoolSelected,
                getDisplayName = { it.displayName },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ordenar por:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = {
                    val nextOrder = if (currentSortOrder == SortOrder.NAME) SortOrder.CIRCLE else SortOrder.NAME
                    onSortOrderChanged(nextOrder)
                }
            ) {
                Text(
                    text = if (currentSortOrder == SortOrder.NAME) "Nome" else "Círculo",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterDropdown(
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T?) -> Unit,
    getDisplayName: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        val value = selectedOption?.let { getDisplayName(it) } ?: "Todos"
        
        BasicTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            interactionSource = interactionSource,
            singleLine = true,
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            colors = OutlinedTextFieldDefaults.colors(),
                            shape = OutlinedTextFieldDefaults.shape,
                        )
                    }
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todos") },
                onClick = {
                    onOptionSelected(null)
                    expanded = false
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(getDisplayName(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SpellItem(spell: SpellResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = spell.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = spell.school?.displayName ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = spell.circle.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterDropdownPreview() {
    SpellBookTheme {
        FilterDropdown(
            label = "Tipo",
            options = SpellType.entries,
            selectedOption = SpellType.ARCANE,
            onOptionSelected = {},
            getDisplayName = { it.displayName }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SpellItemPreview() {
    SpellBookTheme {
        SpellItem(spell = MockData.spell, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SpellListContentPreview() {
    SpellBookTheme {
        SpellListContent(
            spells = MockData.spells,
            isLoading = false,
            filterType = null,
            filterSchool = null,
            filterCircle = null,
            sortOrder = SortOrder.NAME,
            searchQuery = "",
            onTypeSelected = {},
            onSchoolSelected = {},
            onCircleSelected = {},
            onSortOrderChanged = {},
            onSearchQueryChanged = {},
            onSpellClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterSectionPreview() {
    SpellBookTheme {
        FilterSection(
            selectedType = SpellType.ARCANE,
            onTypeSelected = {},
            selectedSchool = SpellSchool.EVOCATION,
            onSchoolSelected = {},
            selectedCircle = 1,
            onCircleSelected = {},
            currentSortOrder = SortOrder.NAME,
            onSortOrderChanged = {}
        )
    }
}
