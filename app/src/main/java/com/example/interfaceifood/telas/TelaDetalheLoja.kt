package com.example.interfaceifood.telas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.interfaceifood.dados.DadosApp
import com.example.interfaceifood.model.textoTaxa
import com.example.interfaceifood.navegacao.Rotas

@Composable
fun TelaDetalheLoja(navController: NavController, id: Int) {
    val loja = DadosApp.buscarLoja(id)
    var valorPedido by remember { mutableStateOf("") }

    if (loja == null) {
        Text("Loja não encontrada", modifier = Modifier.padding(16.dp))
        return
    }


    val total = (valorPedido.toDoubleOrNull() ?: 0.0) + loja.taxaEntrega

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(loja.nome, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
        Text("Avaliação: " + loja.avaliacao)
        Text("Tempo de entrega: " + loja.tempoEntrega)
        Text(textoTaxa(loja.taxaEntrega))

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Categoria", style = MaterialTheme.typography.labelSmall)
                Text(DadosApp.nomeCategoria(loja.categoriaId), fontWeight = FontWeight.Bold)
                Button(onClick = { navController.navigate("detalheCategoria/${loja.categoriaId}") }) {
                    Text("Ver categoria")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = loja.favorita,
                onCheckedChange = { marcado ->
                    DadosApp.atualizarLoja(loja.copy(favorita = marcado))
                }
            )
            Text("Loja favorita")
        }

        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = valorPedido,
            onValueChange = { valorPedido = it },
            label = { Text("Valor do pedido (R$)") },
            modifier = Modifier.fillMaxWidth()
        )
        Text("Total com entrega: R$ " + "%.2f".format(total), fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                DadosApp.fazerPedido(loja, valorManual = valorPedido.toDoubleOrNull() ?: 0.0)
                navController.navigate(Rotas.PEDIDO)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fazer pedido")
        }
    }
}