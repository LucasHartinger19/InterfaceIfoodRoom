package com.example.interfaceifood.ui.telas

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import com.example.interfaceifood.ui.navegacao.Rotas
import com.example.interfaceifood.viewmodel.AppViewModelFactory
import com.example.interfaceifood.viewmodel.DetalheLojaViewModel

@Composable
fun TelaDetalheLoja(
    navController: NavController,
    factory: AppViewModelFactory,
    id: Int,
    viewModel: DetalheLojaViewModel = viewModel(factory = factory.comId(id))
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val loja = uiState.loja
    if (loja == null) {
        val mensagem = if (uiState.carregando) "Carregando..." else "Loja não encontrada"
        Text(mensagem, modifier = Modifier.padding(16.dp))
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(loja.nome, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
        Text("Avaliação: " + loja.avaliacao)
        Text("Tempo de entrega: " + loja.tempoEntrega)
        Text(textoTaxa(loja.taxaEntrega))

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Categoria", style = MaterialTheme.typography.labelSmall)
                Text(nomeCategoria(uiState.categorias, loja.categoriaId), fontWeight = FontWeight.Bold)
                Button(onClick = { navController.navigate("detalheCategoria/${loja.categoriaId}") }) {
                    Text("Ver categoria")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = loja.favorita,
                onCheckedChange = { marcado -> viewModel.alterarFavorita(marcado) }
            )
            Text("Loja favorita")
        }

        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = uiState.novaNota,
            onValueChange = { viewModel.alterarNovaNota(it) },
            label = { Text("Nova nota (0 a 5)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (viewModel.salvarNota()) {
                    Toast.makeText(context, "Nota atualizada!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Digite uma nota entre 0 e 5", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar nota")
        }

        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = uiState.valorPedido,
            onValueChange = { viewModel.alterarValorPedido(it) },
            label = { Text("Valor do pedido (R$)") },
            modifier = Modifier.fillMaxWidth()
        )
        Text("Total com entrega: R$ " + "%.2f".format(uiState.total), fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.fazerPedido()
                navController.navigate(Rotas.PEDIDO)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fazer pedido")
        }
    }
}