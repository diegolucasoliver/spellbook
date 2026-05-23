package com.dmlo.spellbook.network.response

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

enum class SpellType(val displayName: String) {
    @PropertyName("Arcana") ARCANE("Arcana"),
    @PropertyName("Divina") DIVINE("Divina"),
    @PropertyName("Universal") UNIVERSAL("Universal")
}

enum class SpellSchool(val displayName: String) {
    @PropertyName("Abjuração") ABJURATION("Abjuração"),
    @PropertyName("Adivinhação") DIVINATION("Adivinhação"),
    @PropertyName("Convocação") CONJURATION("Convocação"),
    @PropertyName("Encantamento") ENCHANTMENT("Encantamento"),
    @PropertyName("Evocação") EVOCATION("Evocação"),
    @PropertyName("Ilusão") ILLUSION("Ilusão"),
    @PropertyName("Necromancia") NECROMANCY("Necromancia"),
    @PropertyName("Transmutação") TRANSMUTATION("Transmutação")
}

data class SpellResponse(
    @DocumentId
    var documentId: String = "",

    @get:PropertyName("_id")
    @set:PropertyName("_id")
    var id: String = "",

    var name: String = "",
    var school: SpellSchool? = null,
    var type: SpellType? = null,
    var circle: Int = 0,
    var requirements: RequirementsResponse = RequirementsResponse(),
    var resume: String = "",

    @get:PropertyName("base_description")
    @set:PropertyName("base_description")
    var baseDescription: String = "",

    var enhancements: List<EnhancementResponse> = emptyList()
)

data class RequirementsResponse(
    var execution: String = "",
    var range: String = "",
    var target: String? = null,
    var area: String? = null,
    var effect: String? = null,
    var duration: String = "",
    var resistance: String? = null
)

data class EnhancementResponse(
    @get:PropertyName("additional_pm_cost")
    @set:PropertyName("additional_pm_cost")
    var additionalPmCost: Int = 0,

    var description: String = ""
)
