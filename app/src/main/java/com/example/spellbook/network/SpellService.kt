package com.dmlo.spellbook.network

import com.dmlo.spellbook.network.response.SpellResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

interface ISpellService {
    suspend fun getAllSpells(): List<SpellResponse>
    suspend fun getSpellById(id: String): SpellResponse?
}

class SpellService : ISpellService {
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val spellsCollection by lazy { firestore.collection("spells") }

    /**
     * Busca todas as magias da coleção "spells" ordenadas pelo nome.
     */
    override suspend fun getAllSpells(): List<SpellResponse> {
        return try {
            val snapshot = spellsCollection
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()
            snapshot.toObjects(SpellResponse::class.java)
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Busca uma magia pelo campo "_id".
     */
    override suspend fun getSpellById(id: String): SpellResponse? {
        return try {
            val snapshot = spellsCollection.whereEqualTo("_id", id).get().await()
            snapshot.toObjects(SpellResponse::class.java).firstOrNull()
        } catch (_: Exception) {
            null
        }
    }
}
