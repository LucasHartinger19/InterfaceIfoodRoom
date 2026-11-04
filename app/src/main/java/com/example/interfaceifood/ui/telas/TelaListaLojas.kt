package com.example.interfaceifood.ui.telas

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.interfaceifood.ui.componentes.nomeCategoria
import com.example.interfaceifood.ui.componentes.textoTaxa
import com.example.interfaceifood.viewmodel.AppViewModelFactory
import com.example.interfaceifood.viewmodel.LojasViewModel


@Composable
fun TelaListaLojas(
    navController: NavController,
    factory: AppViewModelFactory,
    viewModel: LojasViewModel = viewModel(factory = factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Column {
                Text("Nova loja", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = { viewModel.alterarNome(it) },
                    label = { Text("Nome da loja") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.avaliacao,
                    onValueChange = { viewModel.alterarAvaliacao(it) },
                    label = { Text("Avaliação (ex: 4.5)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.tempo,
                    onValueChange = { viewModel.alterarTempo(it) },
                    label = { Text("Tempo de entrega (ex: 20-30 min)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.taxa,
                    onValueChange = { viewModel.alterarTaxa(it) },
                    label = { Text("Taxa de entrega (0 = grátis)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Categoria:")


                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.categorias) { categoria ->
                        FilterChip(
                            selected = uiState.categoriaId == categoria.id,
                            onClick = { viewModel.selecionarCategoria(categoria.id) },
                            label = { Text(categoria.nome) }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (viewModel.adicionarLoja()) {
                            Toast.makeText(context, "Loja adicionada!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Digite o nome da loja", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar loja")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Minhas lojas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }


        items(uiState.lojas) { loja ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("detalheLoja/${loja.id}") }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(loja.nome, fontWeight = FontWeight.Bold)
                        Text(loja.avaliacao.toString() + " - " + nomeCategoria(uiState.categorias, loja.categoriaId))
                        Text(loja.tempoEntrega + " - " + textoTaxa(loja.taxaEntrega))
                    }


                    Checkbox(
                        checked = loja.favorita,
                        onCheckedChange = { marcado ->
                            viewModel.alterarFavorita(loja, marcado)
                        }
                    )


                    IconButton(onClick = { viewModel.removerLoja(loja) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remover")
                    }
                }
            }
        }
    }
}