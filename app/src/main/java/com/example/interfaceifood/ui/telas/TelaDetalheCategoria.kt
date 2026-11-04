package com.example.interfaceifood.ui.telas

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.interfaceifood.ui.componentes.CardLoja
import com.example.interfaceifood.ui.componentes.nomeCategoria
import com.example.interfaceifood.viewmodel.AppViewModelFactory
import com.example.interfaceifood.viewmodel.DetalheCategoriaViewModel


@Composable
fun TelaDetalheCategoria(
    navController: NavController,
    factory: AppViewModelFactory,
    id: Int,
    viewModel: DetalheCategoriaViewModel = viewModel(factory = factory.comId(id))
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val categoria = uiState.categoria
    if (categoria == null) {
        val mensagem = if (uiState.carregando) "Carregando..." else "Categoria não encontrada"
        Text(mensagem, modifier = Modifier.padding(16.dp))
        return
    }

    val lojasDaCategoria = uiState.lojasDaCategoria

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
                    value = uiState.nomeEditado,
                    onValueChange = { viewModel.alterarNome(it) },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.descricaoEditada,
                    onValueChange = { viewModel.alterarDescricao(it) },
                    label = { Text("Descrição") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (viewModel.salvarAlteracoes()) {
                            Toast.makeText(context, "Categoria atualizada!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "O nome não pode ficar vazio", Toast.LENGTH_SHORT).show()
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
            CardLoja(loja, nomeCategoria(uiState.categorias, loja.categoriaId)) {
                navController.navigate("detalheLoja/${loja.id}")
            }
        }
    }
}