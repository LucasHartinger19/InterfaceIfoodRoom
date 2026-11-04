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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.example.interfaceifood.ui.navegacao.Rotas
import com.example.interfaceifood.viewmodel.AppViewModelFactory
import com.example.interfaceifood.viewmodel.InicioViewModel


@Composable
fun TelaInicial(
    navController: NavController,
    factory: AppViewModelFactory,
    viewModel: InicioViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("ENTREGAR EM", style = MaterialTheme.typography.labelSmall)
        Text("Avenida Batel, n° 630", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Rotas.BUSCA) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("O que você quer comer? Buscar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Categorias", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))


        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.categorias) { categoria ->
                AssistChip(
                    onClick = { navController.navigate("detalheCategoria/${categoria.id}") },
                    label = { Text(categoria.nome) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Lojas Recomendadas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))


        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.lojas) { loja ->
                CardLoja(loja, nomeCategoria(uiState.categorias, loja.categoriaId)) {
                    navController.navigate("detalheLoja/${loja.id}")
                }
            }
        }
    }
}