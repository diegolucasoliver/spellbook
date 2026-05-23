package com.dmlo.spellbook.ui

import com.dmlo.spellbook.network.response.EnhancementResponse
import com.dmlo.spellbook.network.response.RequirementsResponse
import com.dmlo.spellbook.network.response.SpellResponse
import com.dmlo.spellbook.network.response.SpellSchool
import com.dmlo.spellbook.network.response.SpellType

object MockData {
    val spell = SpellResponse(
        documentId = "id_1",
        id = "spell_abencoar_alimentos",
        name = "Abençoar Alimentos",
        school = SpellSchool.TRANSMUTATION,
        type = SpellType.DIVINE,
        circle = 1,
        requirements = RequirementsResponse(
            execution = "padrão",
            range = "curto",
            target = "alimento para 1 criatura",
            duration = "cena"
        ),
        resume = "Purifica refeição, que também fornece bônus temporários.",
        baseDescription = "Você purifica e abençoa uma porção de comida ou dose de bebida. Isso torna um alimento sujo, estragado ou envenenado próprio para consumo.",
        enhancements = listOf(
            EnhancementResponse(0, "Truque: o alimento é purificado."),
            EnhancementResponse(1, "+1 PM: aumenta o número de alvos em +1.")
        )
    )

    val spells = listOf(
        spell,
        spell.copy(
            documentId = "id_2",
            name = "Bola de Fogo",
            type = SpellType.ARCANE,
            circle = 2,
            school = SpellSchool.EVOCATION
        )
    )
}
