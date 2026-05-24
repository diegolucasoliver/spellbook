package com.dmlo.spellbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.res.stringResource
import com.dmlo.spellbook.R
import com.dmlo.spellbook.network.ISpellService
import com.dmlo.spellbook.network.response.SpellResponse
import com.dmlo.spellbook.ui.SpellDetailScreen
import com.dmlo.spellbook.ui.SpellListScreen
import com.dmlo.spellbook.ui.theme.SpellBookTheme
import com.dmlo.spellbook.viewmodel.SpellViewModel
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val viewModel: SpellViewModel by viewModels()

        // Autenticação anônima para garantir que o usuário está "logado" no Firebase
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Logado com sucesso, agora podemos carregar os dados
                    viewModel.loadSpells()
                } else {
                    val errorMessage = getString(R.string.error_auth, task.exception?.message ?: "")
                    viewModel.setErrorMessage(errorMessage)
                }
            }

        setContent {
            SpellBookTheme {
                SpellBookApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellBookApp(viewModel: SpellViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.setErrorMessage(null)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "list") {
            composable("list") {
                SpellListScreen(
                    viewModel = viewModel,
                    onSpellClick = { spellId ->
                        navController.navigate("detail/$spellId")
                    }
                )
            }
            composable(
                route = "detail/{spellId}",
                arguments = listOf(navArgument("spellId") { type = NavType.StringType })
            ) { backStackEntry ->
                val spellId = backStackEntry.arguments?.getString("spellId") ?: ""
                SpellDetailScreen(
                    spellId = spellId,
                    viewModel = viewModel,
                    onBack = { 
                        navController.popBackStack()
                        viewModel.clearSelectedSpell()
                    }
                )
            }
        }
        
        // SnackbarHost flutuando sobre a navegação
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SpellBookAppPreview() {
    val fakeService = object : ISpellService {
        override suspend fun getAllSpells(): List<SpellResponse> = emptyList()
        override suspend fun getSpellById(id: String): SpellResponse? = null
    }
    // Usamos remember para o Preview evitar recriações e silenciar o aviso de lint
    val viewModel = remember { SpellViewModel(fakeService) }
    SpellBookTheme {
        SpellBookApp(viewModel = viewModel)
    }
}
