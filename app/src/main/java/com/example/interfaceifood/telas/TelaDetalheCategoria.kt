package com.example.interfaceifood.telas

import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.example.interfaceifood.componentes.CardLoja
import com.example.interfaceifood.dados.DadosApp
import com.example.interfaceifood.model.Categoria


@Composable
fun TelaDetalheCategoria(navController: NavController, id: Int) {
    val context = LocalContext.current


    var categoriaBanco by remember { mutableStateOf<Categoria?>(null) }
    var carregando by remember { mutableStateOf(true) }


    var nomeEditado by remember { mutableStateOf("") }
    var descricaoEditada by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        val encontrada = DadosApp.buscarCategoriaNoBanco(id)
        categoriaBanco = encontrada
        if (encontrada != null) {
            nomeEditado = encontrada.nome
            descricaoEditada = encontrada.descricao
        }
        carregando = false
    }

    val categoria = categoriaBanco
    if (categoria == null) {
        val mensagem = if (carregando) "Carregando..." else "Categoria não encontrada"
        Text(mensagem, modifier = Modifier.padding(16.dp))
        return
    }

    val lojasDaCategoria = DadosApp.lojas.filter { it.categoriaId == categoria.id }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column {
                Text(categoria.nome, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                Text(categoria.descricao)
                Text("Quantidade de lojas: " + lojasDaCategoria.size)
            }
        }


        item {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Editar categoria", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = nomeEditado,
                    onValueChange = { nomeEditado = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descricaoEditada,
                    onValueChange = { descricaoEditada = it },
                    label = { Text("Descrição") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (nomeEditado == "") {
                            Toast.makeText(context, "O nome não pode ficar vazio", Toast.LENGTH_SHORT).show()
                        } else {
                            val editada = categoria.copy(nome = nomeEditado, descricao = descricaoEditada)
                            DadosApp.atualizarCategoria(editada)
                            categoriaBanco = editada
                            Toast.makeText(context, "Categoria atualizada!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar alterações")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Lojas desta categoria", fontWeight = FontWeight.Bold)
            }
        }

        items(lojasDaCategoria) { loja ->
            CardLoja(loja) {
                navController.navigate("detalheLoja/${loja.id}")
            }
        }
    }
}