package com.example.interfaceifood.telas

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.interfaceifood.componentes.CardLoja
import com.example.interfaceifood.dados.DadosApp
import com.example.interfaceifood.navegacao.Rotas


@Composable
fun TelaInicial(navController: NavController) {

    LaunchedEffect(Unit) {
        DadosApp.carregar()
    }

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
            items(DadosApp.categorias) { categoria ->
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
            items(DadosApp.lojas) { loja ->
                CardLoja(loja) {
                    navController.navigate("detalheLoja/${loja.id}")
                }
            }
        }
    }
}