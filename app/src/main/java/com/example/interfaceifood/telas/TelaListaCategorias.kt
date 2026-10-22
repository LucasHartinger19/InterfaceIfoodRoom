package com.example.interfaceifood.telas

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.interfaceifood.dados.DadosApp
import com.example.interfaceifood.model.Categoria


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TelaListaCategorias(navController: NavController) {
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        DadosApp.carregar()
    }

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Column {
                Text("Nova categoria", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))


                TextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (nome == "") {
                            Toast.makeText(context, "Digite o nome da categoria", Toast.LENGTH_SHORT).show()
                        } else {
                            DadosApp.adicionarCategoria(Categoria(0, nome, descricao))
                            nome = ""
                            descricao = ""
                            Toast.makeText(context, "Categoria adicionada!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar categoria")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Toque para ver detalhes. Segure para remover.")
            }
        }


        items(DadosApp.categorias) { categoria ->
            val quantidadeLojas = DadosApp.lojas.count { it.categoriaId == categoria.id }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { navController.navigate("detalheCategoria/${categoria.id}") },
                        onLongClick = {

                            if (quantidadeLojas > 0) {
                                Toast.makeText(context, "Remova as lojas dessa categoria primeiro", Toast.LENGTH_SHORT).show()
                            } else {
                                DadosApp.removerCategoria(categoria)
                                Toast.makeText(context, "Categoria removida", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(categoria.nome, fontWeight = FontWeight.Bold)
                    Text(categoria.descricao)
                    Text("Lojas: " + quantidadeLojas)
                }
            }
        }
    }
}