package com.example.demoactivity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demoactivity.R
import com.example.demoactivity.presentation.CurrencyListScreen
import com.example.demoactivity.presentation.CurrencyViewModel
import com.example.demoactivity.presentation.DemoViewModel
import com.example.demoactivity.ui.theme.DemoActivityTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoActivityTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    val context = LocalContext.current
    val demoViewModel: DemoViewModel = hiltViewModel()
    val currencyViewModel: CurrencyViewModel = hiltViewModel()
    val currencyUiState by currencyViewModel.uiState.collectAsState()

    when (currentScreen) {
        Screen.Main -> {
            DemoScreen(
                onClearDatabase = {
                    demoViewModel.clearDatabase(
                        onSuccess = { resId ->
                            Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
                        },
                        onError = { resId ->
                            Toast.makeText(context, context.getString(resId), Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onInsertToDatabase = {
                    demoViewModel.insertToDatabase(
                        onSuccess = { resId ->
                            Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
                        },
                        onError = { resId, errorMessage ->
                            val message = if (errorMessage != null) {
                                context.getString(resId, errorMessage)
                            } else {
                                context.getString(resId)
                            }
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onNavigateToCryptos = { currentScreen = Screen.Cryptos },
                onNavigateToFiats = { currentScreen = Screen.Fiats },
                onNavigateToBoth = { currentScreen = Screen.Both }
            )
        }
        Screen.Cryptos -> {
            CurrencyListScreen(
                currencies = currencyUiState.cryptos,
                onBack = { currentScreen = Screen.Main },
                isLoading = currencyUiState.isLoading
            )
        }
        Screen.Fiats -> {
            CurrencyListScreen(
                currencies = currencyUiState.fiats,
                onBack = { currentScreen = Screen.Main },
                isLoading = currencyUiState.isLoading
            )
        }
        Screen.Both -> {
            CurrencyListScreen(
                currencies = currencyUiState.currencies,
                onBack = { currentScreen = Screen.Main },
                isLoading = currencyUiState.isLoading
            )
        }
    }
}

enum class Screen {
    Main, Cryptos, Fiats, Both
}

@Composable
fun DemoScreen(
    onClearDatabase: () -> Unit,
    onInsertToDatabase: () -> Unit,
    onNavigateToCryptos: () -> Unit,
    onNavigateToFiats: () -> Unit,
    onNavigateToBoth: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Button(
                onClick = onClearDatabase,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.button_clear_database))
            }

            Button(
                onClick = onInsertToDatabase,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.button_insert_database))
            }

            Button(
                onClick = onNavigateToCryptos,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.button_see_cryptos))
            }

            Button(
                onClick = onNavigateToFiats,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.button_see_fiats))
            }

            Button(
                onClick = onNavigateToBoth,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.button_see_both))
            }
        }
    }
}
