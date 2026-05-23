package com.dmlo.spellbook.viewmodel

import app.cash.turbine.test
import com.dmlo.spellbook.network.SpellService
import com.dmlo.spellbook.network.response.SpellResponse
import com.dmlo.spellbook.network.response.SpellSchool
import com.dmlo.spellbook.network.response.SpellType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SpellViewModelTest {

    private val spellService = mockk<SpellService>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SpellViewModel

    private val mockSpells = listOf(
        SpellResponse(documentId = "1", name = "A", type = SpellType.ARCANE, circle = 1, school = SpellSchool.ABJURATION),
        SpellResponse(documentId = "2", name = "B", type = SpellType.DIVINE, circle = 2, school = SpellSchool.DIVINATION),
        SpellResponse(documentId = "3", name = "C", type = SpellType.UNIVERSAL, circle = 1, school = SpellSchool.ABJURATION)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { spellService.getAllSpells() } returns mockSpells
        viewModel = SpellViewModel(spellService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSpells updates spells state`() = runTest {
        viewModel.spells.test {
            assertEquals(emptyList<SpellResponse>(), awaitItem())
            
            viewModel.loadSpells()
            testDispatcher.scheduler.advanceUntilIdle()

            val list = awaitItem()
            assertEquals(3, list.size)
        }
    }

    @Test
    fun `filterType Arcana includes Universal spells`() = runTest {
        viewModel.spells.test {
            assertEquals(emptyList<SpellResponse>(), awaitItem())
            
            viewModel.loadSpells()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(3, awaitItem().size)

            viewModel.setFilterType(SpellType.ARCANE)
            
            val list = awaitItem()
            assertEquals(2, list.size)
            assertEquals(true, list.all { it.type == SpellType.ARCANE || it.type == SpellType.UNIVERSAL })
        }
    }

    @Test
    fun `filterCircle 1 only shows circle 1 spells`() = runTest {
        viewModel.spells.test {
            assertEquals(emptyList<SpellResponse>(), awaitItem())
            
            viewModel.loadSpells()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(3, awaitItem().size)

            viewModel.setFilterCircle(1)

            val list = awaitItem()
            assertEquals(2, list.size)
            assertEquals(true, list.all { it.circle == 1 })
        }
    }

    @Test
    fun `searchQuery filters spells by name`() = runTest {
        viewModel.spells.test {
            assertEquals(emptyList<SpellResponse>(), awaitItem())
            
            viewModel.loadSpells()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(3, awaitItem().size)

            viewModel.setSearchQuery("B")

            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("B", list[0].name)
        }
    }

    @Test
    fun `filterSchool ABJURATION only shows spells from school ABJURATION`() = runTest {
        viewModel.spells.test {
            assertEquals(emptyList<SpellResponse>(), awaitItem())
            
            viewModel.loadSpells()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(3, awaitItem().size)

            viewModel.setFilterSchool(SpellSchool.ABJURATION)

            val list = awaitItem()
            assertEquals(2, list.size)
            assertEquals(true, list.all { it.school == SpellSchool.ABJURATION })
        }
    }

    @Test
    fun `loadSpellById updates selectedSpell state`() = runTest {
        viewModel.loadSpells()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadSpellById("2")
        
        viewModel.selectedSpell.test {
            assertEquals(null, awaitItem())
            val spell = awaitItem()
            assertEquals("2", spell?.documentId)
            assertEquals("B", spell?.name)
        }
    }

    @Test
    fun `sortOrder CIRCLE sorts by circle then name`() = runTest {
        viewModel.spells.test {
            assertEquals(emptyList<SpellResponse>(), awaitItem())
            
            viewModel.loadSpells()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(3, awaitItem().size)

            viewModel.setSortOrder(SortOrder.CIRCLE)

            val list = awaitItem()
            assertEquals("A", list[0].name) // Circle 1
            assertEquals("C", list[1].name) // Circle 1
            assertEquals("B", list[2].name) // Circle 2
        }
    }
}
