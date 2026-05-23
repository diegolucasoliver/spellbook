package com.dmlo.spellbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmlo.spellbook.network.ISpellService
import com.dmlo.spellbook.network.SpellService
import com.dmlo.spellbook.network.response.SpellResponse
import com.dmlo.spellbook.network.response.SpellSchool
import com.dmlo.spellbook.network.response.SpellType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class SortOrder { NAME, CIRCLE }

class SpellViewModel(private val spellService: ISpellService = SpellService()) : ViewModel() {

    private val _spells = MutableStateFlow<List<SpellResponse>>(emptyList())
    
    private val _filterType = MutableStateFlow<SpellType?>(null)
    val filterType: StateFlow<SpellType?> = _filterType

    private val _filterSchool = MutableStateFlow<SpellSchool?>(null)
    val filterSchool: StateFlow<SpellSchool?> = _filterSchool

    private val _filterCircle = MutableStateFlow<Int?>(null)
    val filterCircle: StateFlow<Int?> = _filterCircle

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortOrder = MutableStateFlow(SortOrder.NAME)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    val spells: StateFlow<List<SpellResponse>> = combine(
        _spells, _filterType, _filterSchool, _filterCircle, _sortOrder, _searchQuery
    ) { args ->
        val spells = args[0] as List<SpellResponse>
        val type = args[1] as SpellType?
        val school = args[2] as SpellSchool?
        val circle = args[3] as Int?
        val sortOrder = args[4] as SortOrder
        val query = args[5] as String

        val filtered = spells.filter { spell ->
            val matchesType = when (type) {
                null -> true
                SpellType.ARCANE -> spell.type == SpellType.ARCANE || spell.type == SpellType.UNIVERSAL
                SpellType.DIVINE -> spell.type == SpellType.DIVINE || spell.type == SpellType.UNIVERSAL
                else -> spell.type == type
            }
            val matchesQuery = query.isBlank() || spell.name.contains(query, ignoreCase = true)

            matchesType &&
            matchesQuery &&
            (school == null || spell.school == school) &&
            (circle == null || spell.circle == circle)
        }
        when (sortOrder) {
            SortOrder.NAME -> filtered.sortedBy { it.name }
            SortOrder.CIRCLE -> filtered.sortedWith(compareBy<SpellResponse> { it.circle }.thenBy { it.name })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedSpell = MutableStateFlow<SpellResponse?>(null)
    val selectedSpell: StateFlow<SpellResponse?> = _selectedSpell

    fun loadSpells() {
        viewModelScope.launch {
            _isLoading.value = true
            _spells.value = spellService.getAllSpells()
            _isLoading.value = false
        }
    }

    fun setFilterType(type: SpellType?) {
        _filterType.value = type
        _searchQuery.value = ""
    }

    fun setFilterSchool(school: SpellSchool?) {
        _filterSchool.value = school
        _searchQuery.value = ""
    }

    fun setFilterCircle(circle: Int?) {
        _filterCircle.value = circle
        _searchQuery.value = ""
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun loadSpellById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Tenta achar na lista local primeiro para ser instantâneo
            val localSpell = _spells.value.find { it.documentId == id }
            if (localSpell != null) {
                _selectedSpell.value = localSpell
            } else {
                // Se não achar (ex: abriu direto via link), busca no banco
                _selectedSpell.value = spellService.getSpellById(id)
            }
            _isLoading.value = false
        }
    }

    fun clearSelectedSpell() {
        _selectedSpell.value = null
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }
}
