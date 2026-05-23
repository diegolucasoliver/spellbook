package com.dmlo.spellbook.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboGif
import com.github.takahirom.roborazzi.captureRoboImage
import com.dmlo.spellbook.ui.theme.SpellBookTheme
import com.dmlo.spellbook.viewmodel.SortOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
class SnapshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingIndicatorGif() {
        composeTestRule.setContent {
            SpellBookTheme {
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        
        // Captura a animação do loading
        val file = File("src/test/snapshots/loading_animation.gif")
        composeTestRule.onRoot().captureRoboGif(composeTestRule, file) {
            composeTestRule.mainClock.advanceTimeBy(1000)
        }
    }

    @Test
    fun spellItemSnapshot() {
        composeTestRule.setContent {
            SpellBookTheme {
                SpellItem(spell = MockData.spell, onClick = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/spell_item.png")
    }

    @Test
    fun spellListContentSnapshot() {
        composeTestRule.setContent {
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
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/spell_list.png")
    }

    @Test
    fun spellDetailContentSnapshot() {
        composeTestRule.setContent {
            SpellBookTheme {
                SpellDetailContent(
                    spell = MockData.spell,
                    isLoading = false,
                    onBack = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/spell_detail.png")
    }

    @Test
    fun filterSectionSnapshot() {
        composeTestRule.setContent {
            SpellBookTheme {
                FilterSection(
                    selectedType = null,
                    onTypeSelected = {},
                    selectedSchool = null,
                    onSchoolSelected = {},
                    selectedCircle = null,
                    onCircleSelected = {},
                    currentSortOrder = SortOrder.NAME,
                    onSortOrderChanged = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/filter_section.png")
    }

    @Test
    fun filterDropdownSnapshot() {
        composeTestRule.setContent {
            SpellBookTheme {
                FilterDropdown(
                    label = "Tipo",
                    options = com.dmlo.spellbook.network.response.SpellType.entries,
                    selectedOption = com.dmlo.spellbook.network.response.SpellType.ARCANE,
                    onOptionSelected = {},
                    getDisplayName = { it.displayName }
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/filter_dropdown.png")
    }

    @Test
    fun spellBookAppSnapshot() {
        composeTestRule.setContent {
            SpellBookTheme {
                com.dmlo.spellbook.SpellBookApp(viewModel = com.dmlo.spellbook.viewmodel.SpellViewModel())
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/app_root.png")
    }
}
