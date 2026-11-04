package com.example.interfaceifood.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.interfaceifood.ui.componentes.CardLoja
import com.example.interfaceifood.ui.componentes.nomeCategoria
import com.example.interfaceifood.viewmodel.AppViewModelFactory
import com.example.interfaceifood.viewmodel.BuscaViewModel


@Composable
fun TelaBusca(
    navController: NavController,
    factory: AppViewModelFactory,
    viewModel: BuscaViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val resultados = uiState.resultados

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = uiState.textoBusca,
            onValueChange = { viewModel.alterarTextoBusca(it) },
            placeholder = { Text("O que você quer comer?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.filtros) { filtro ->
                FilterChip(
                    selected = uiState.filtroSelecionado == filtro,
                    onClick = { viewModel.selecionarFiltro(filtro) },
                    label = { Text(filtro) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Lojas encontradas: " + resultados.size,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(resultados) { loja ->
                CardLoja(loja, nomeCategoria(uiState.categorias, loja.categoriaId)) {
                    navController.navigate("detalheLoja/${loja.id}")
                }
            }
        }
    }
}