package com.example.interfaceifood.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val descricao: String
)

@Entity(tableName = "lojas")
data class Loja(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val categoriaId: Int,
    val avaliacao: Double,
    val tempoEntrega: String,
    val taxaEntrega: Double,
    val favorita: Boolean = false
)


@Entity(tableName = "produtos")
data class Produto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val lojaId: Int,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val disponivel: Boolean = true
)

@Entity(tableName = "pedidos")
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val lojaId: Int,
    val dataHora: Long = System.currentTimeMillis(),
    val etapa: Int = 0,
    val status: String = STATUS_ANDAMENTO,
    val subtotal: Double,
    val taxaEntrega: Double,
    val total: Double
)

const val STATUS_ANDAMENTO = "Em andamento"
const val STATUS_ENTREGUE = "Entregue"
const val STATUS_CANCELADO = "Cancelado"


@Entity(tableName = "itens_pedido")
data class ItemPedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pedidoId: Int = 0,
    val produtoId: Int,
    val nomeProduto: String,
    val precoUnitario: Double,
    val quantidade: Int
)


fun textoTaxa(taxa: Double): String {
    if (taxa == 0.0) {
        return "Entrega Grátis"
    }
    return "Entrega R$ " + taxa
}

fun textoPreco(valor: Double): String {
    return "R$ " + "%.2f".format(valor)
}
