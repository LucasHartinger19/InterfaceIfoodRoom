package com.example.interfaceifood.telas

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.interfaceifood.dados.DadosApp
import com.example.interfaceifood.navegacao.Rotas


@Composable
fun TelaAcompanharPedido(navController: NavController) {
    val context = LocalContext.current
    val loja = DadosApp.buscarLoja(DadosApp.pedidoLojaId.value)
    val etapaAtual = DadosApp.etapaPedido.value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        if (loja == null) {

            Text("Você ainda não fez nenhum pedido.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate(Rotas.LOJAS) }) {
                Text("Escolher uma loja")
            }
        } else {
            Text("PREVISÃO DE ENTREGA", style = MaterialTheme.typography.labelSmall)
            Text(loja.tempoEntrega, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Status do pedido", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    for (i in DadosApp.etapas.indices) {
                        LinhaStatus(DadosApp.etapas[i], i <= etapaAtual)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Informações", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("detalheLoja/${loja.id}") }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(loja.nome, fontWeight = FontWeight.Bold)
                    Text("Pedido #" + (9800 + loja.id))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Cleiton Souza", fontWeight = FontWeight.Bold)
                    Text("Entregador parceiro - Moto")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (etapaAtual < DadosApp.etapas.size - 1) DadosApp.avancarEtapa()
                    else Toast.makeText(context, "O pedido já foi entregue!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Atualizar status")
            }

            Button(
                onClick = {
                    DadosApp.cancelarPedido()
                    Toast.makeText(context, "Pedido cancelado", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar pedido")
            }


            Button(
                onClick = {
                    Toast.makeText(context, "Um atendente vai falar com você", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Precisa de ajuda?")
            }
        }
    }
}

@Composable
fun LinhaStatus(nome: String, concluido: Boolean) {
    val cor = if (concluido) Color.Red else Color.Gray
    Text(nome, color = cor, fontWeight = if (concluido) FontWeight.Bold else FontWeight.Normal)
    Spacer(modifier = Modifier.height(8.dp))
}