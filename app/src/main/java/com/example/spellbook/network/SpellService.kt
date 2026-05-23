package com.dmlo.spellbook.network

import com.dmlo.spellbook.network.response.SpellResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class SpellService {
    private val firestore = FirebaseFirestore.getInstance()
    private val spellsCollection = firestore.collection("spells")

    /**
     * Busca todas as magias da coleção "spells" ordenadas pelo nome.
     */
    suspend fun getAllSpells(): List<SpellResponse> {
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
    suspend fun getSpellById(id: String): SpellResponse? {
        return try {
            val snapshot = spellsCollection.whereEqualTo("_id", id).get().await()
            snapshot.toObjects(SpellResponse::class.java).firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
