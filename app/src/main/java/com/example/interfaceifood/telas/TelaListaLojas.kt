package com.example.interfaceifood.telas

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.interfaceifood.dados.DadosApp
import com.example.interfaceifood.model.Loja
import com.example.interfaceifood.model.textoTaxa


@Composable
fun TelaListaLojas(navController: NavController) {
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        DadosApp.carregar()
    }

    var nome by remember { mutableStateOf("") }
    var avaliacao by remember { mutableStateOf("") }
    var tempo by remember { mutableStateOf("") }
    var taxa by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf(1) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Column {
                Text("Nova loja", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome da loja") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = avaliacao,
                    onValueChange = { avaliacao = it },
                    label = { Text("Avaliação (ex: 4.5)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tempo,
                    onValueChange = { tempo = it },
                    label = { Text("Tempo de entrega (ex: 20-30 min)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = taxa,
                    onValueChange = { taxa = it },
                    label = { Text("Taxa de entrega (0 = grátis)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Categoria:")


                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(DadosApp.categorias) { categoria ->
                        FilterChip(
                            selected = categoriaId == categoria.id,
                            onClick = { categoriaId = categoria.id },
                            label = { Text(categoria.nome) }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (nome == "") {
                            Toast.makeText(context, "Digite o nome da loja", Toast.LENGTH_SHORT).show()
                        } else {
                            val novaLoja = Loja(
                                0, // o Room gera o id (autoGenerate)
                                nome,
                                categoriaId,
                                avaliacao.toDoubleOrNull() ?: 0.0,
                                tempo,
                                taxa.toDoubleOrNull() ?: 0.0
                            )
                            DadosApp.adicionarLoja(novaLoja)

                            nome = ""
                            avaliacao = ""
                            tempo = ""
                            taxa = ""
                            Toast.makeText(context, "Loja adicionada!", Toast.LENGTH_SHORT).show()
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


        items(DadosApp.lojas) { loja ->
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
                        Text(loja.avaliacao.toString() + " - " + DadosApp.nomeCategoria(loja.categoriaId))
                        Text(loja.tempoEntrega + " - " + textoTaxa(loja.taxaEntrega))
                    }


                    Checkbox(
                        checked = loja.favorita,
                        onCheckedChange = { marcado ->
                            DadosApp.atualizarLoja(loja.copy(favorita = marcado))
                        }
                    )


                    IconButton(onClick = { DadosApp.removerLoja(loja) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remover")
                    }
                }
            }
        }
    }
}