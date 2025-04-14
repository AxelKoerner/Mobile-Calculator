package com.example.mcexample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.mcexample.ui.theme.MCExampleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun McBaseLayout(
    title: String,
    content: @Composable (
        innerPadding: PaddingValues,
    ) -> Unit
) {
    MCExampleTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    title = { Text(title) }
                )
            },
            modifier = Modifier.fillMaxSize()) { innerPadding ->
            content(innerPadding)
        }
    }
}