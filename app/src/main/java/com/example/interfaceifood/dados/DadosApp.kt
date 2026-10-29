package com.example.interfaceifood.dados

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.interfaceifood.model.Categoria
import com.example.interfaceifood.model.Loja


object DadosApp {

    val categorias = mutableStateListOf(
        Categoria(1, "Pizza", "Pizzas tradicionais e especiais"),
        Categoria(2, "Lanches", "Hambúrgueres e sanduíches"),
        Categoria(3, "Japonesa", "Sushi, temaki e yakisoba"),
        Categoria(4, "Brasileira", "Pratos feitos e comida caseira"),
        Categoria(5, "Doces", "Bolos, açaí e sobremesas")
    )

    val lojas = mutableStateListOf(
        Loja(1, "Mc Donalds", 2, 4.8, "20-30 min", 0.0),
        Loja(2, "Yaki i'n house", 3, 4.9, "35-45 min", 4.99),
        Loja(3, "Dalle Pizza", 1, 4.7, "30-40 min", 2.50),
        Loja(4, "Maias Pizzaria", 1, 4.9, "30-40 min", 5.0),
        Loja(5, "Burguer King", 2, 4.8, "15-25 min", 0.0)
    )


    var proximoIdLoja = 6
    var proximoIdCategoria = 6


    val pedidoLojaId = mutableStateOf(0)
    val etapaPedido = mutableStateOf(0)
    val etapas = listOf("Pedido confirmado", "Em preparo", "A caminho", "Entregue")

    fun buscarLoja(id: Int): Loja? {
        return lojas.find { it.id == id }
    }

    fun buscarCategoria(id: Int): Categoria? {
        return categorias.find { it.id == id }
    }

    fun nomeCategoria(id: Int): String {
        val categoria = buscarCategoria(id)
        if (categoria == null) {
            return "Sem categoria"
        }
        return categoria.nome
    }


    fun atualizarLoja(lojaEditada: Loja) {
        val posicao = lojas.indexOfFirst { it.id == lojaEditada.id }
        if (posicao != -1) {
            lojas[posicao] = lojaEditada
        }
    }
}