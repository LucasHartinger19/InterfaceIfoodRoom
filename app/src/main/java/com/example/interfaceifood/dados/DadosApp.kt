package com.example.interfaceifood.dados

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.interfaceifood.model.Categoria
import com.example.interfaceifood.model.ItemPedido
import com.example.interfaceifood.model.Loja
import com.example.interfaceifood.model.Pedido
import com.example.interfaceifood.model.Produto
import com.example.interfaceifood.model.STATUS_ANDAMENTO
import com.example.interfaceifood.model.STATUS_CANCELADO
import com.example.interfaceifood.model.STATUS_ENTREGUE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object DadosApp {

    val categorias = mutableStateListOf<Categoria>()
    val lojas = mutableStateListOf<Loja>()
    val produtos = mutableStateListOf<Produto>()
    val historicoPedidos = mutableStateListOf<Pedido>()


    val pedidoAtual = mutableStateOf<Pedido?>(null)
    val itensPedidoAtual = mutableStateListOf<ItemPedido>()

    val pedidoLojaId = mutableStateOf(0)
    val etapaPedido = mutableStateOf(0)
    val etapas = listOf("Pedido confirmado", "Em preparo", "A caminho", "Entregue")

    private lateinit var categoriaDAO: CategoriaDAO
    private lateinit var lojaDAO: LojaDAO
    private lateinit var produtoDAO: ProdutoDAO
    private lateinit var pedidoDAO: PedidoDAO

    private val escopo = CoroutineScope(Dispatchers.Main)

    fun iniciar(context: Context) {
        val db = AppDatabase.getDatabase(context)
        categoriaDAO = db.categoriaDAO()
        lojaDAO = db.lojaDAO()
        produtoDAO = db.produtoDAO()
        pedidoDAO = db.pedidoDAO()

        escopo.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (categoriaDAO.buscarCategorias().isEmpty()) {
                        categoriasIniciais().forEach { categoriaDAO.inserir(it) }
                        lojasIniciais().forEach { lojaDAO.inserir(it) }
                        produtosIniciais().forEach { produtoDAO.inserir(it) }
                    }
                }
                carregar()
            } catch (e: Exception) {
                Log.e("Banco", "Erro ao iniciar: ${e.message}")
            }
        }
    }

    private suspend fun carregar() {
        val listaCategorias = withContext(Dispatchers.IO) { categoriaDAO.buscarCategorias() }
        val listaLojas = withContext(Dispatchers.IO) { lojaDAO.buscarLojas() }
        val listaProdutos = withContext(Dispatchers.IO) { produtoDAO.buscarProdutos() }
        val listaPedidos = withContext(Dispatchers.IO) { pedidoDAO.buscarPedidos() }
        val emAndamento = withContext(Dispatchers.IO) { pedidoDAO.buscarPedidoEmAndamento() }

        categorias.clear(); categorias.addAll(listaCategorias)
        lojas.clear(); lojas.addAll(listaLojas)
        produtos.clear(); produtos.addAll(listaProdutos)
        historicoPedidos.clear(); historicoPedidos.addAll(listaPedidos)

        if (emAndamento != null) {
            val itens = withContext(Dispatchers.IO) { pedidoDAO.buscarItens(emAndamento.id) }
            mostrarPedido(emAndamento, itens)
        }
    }

    private fun categoriasIniciais() = listOf(
        Categoria(1, "Pizza", "Pizzas tradicionais e especiais"),
        Categoria(2, "Lanches", "Hambúrgueres e sanduíches"),
        Categoria(3, "Japonesa", "Sushi, temaki e yakisoba"),
        Categoria(4, "Brasileira", "Pratos feitos e comida caseira"),
        Categoria(5, "Doces", "Bolos, açaí e sobremesas")
    )

    private fun lojasIniciais() = listOf(
        Loja(1, "Mc Donalds", 2, 4.8, "20-30 min", 0.0),
        Loja(2, "Yaki i'n house", 3, 4.9, "35-45 min", 4.99),
        Loja(3, "Dalle Pizza", 1, 4.7, "30-40 min", 2.50),
        Loja(4, "Maias Pizzaria", 1, 4.9, "30-40 min", 5.0),
        Loja(5, "Burguer King", 2, 4.8, "15-25 min", 0.0)
    )

    private fun produtosIniciais() = listOf(
        Produto(lojaId = 1, nome = "Big Mac", descricao = "Dois hambúrgueres, alface, queijo e molho especial", preco = 29.90),
        Produto(lojaId = 1, nome = "McFritas Média", descricao = "Batata frita crocante", preco = 12.90),
        Produto(lojaId = 2, nome = "Combo 20 peças", descricao = "Sushi e sashimi variados", preco = 69.90),
        Produto(lojaId = 2, nome = "Temaki Salmão", descricao = "Salmão, arroz e cebolinha", preco = 27.00),
        Produto(lojaId = 3, nome = "Pizza Calabresa G", descricao = "Calabresa, cebola e azeitona", preco = 54.90),
        Produto(lojaId = 3, nome = "Pizza Margherita G", descricao = "Muçarela, tomate e manjericão", preco = 52.90),
        Produto(lojaId = 4, nome = "Pizza Quatro Queijos G", descricao = "Muçarela, provolone, parmesão e catupiry", preco = 59.90),
        Produto(lojaId = 4, nome = "Refrigerante 2L", descricao = "Coca-Cola ou Guaraná", preco = 14.00),
        Produto(lojaId = 5, nome = "Whopper", descricao = "Hambúrguer grelhado no fogo", preco = 31.90),
        Produto(lojaId = 5, nome = "Onion Rings", descricao = "Anéis de cebola empanados", preco = 13.90)
    )


    fun adicionarCategoria(categoria: Categoria) {
        escopo.launch {
            try {
                val novoId = withContext(Dispatchers.IO) { categoriaDAO.inserir(categoria) }
                categorias.add(categoria.copy(id = novoId.toInt()))
            } catch (e: Exception) {
                Log.e("Banco", "Erro ao inserir categoria: ${e.message}")
            }
        }
    }

    fun removerCategoria(categoria: Categoria) {
        categorias.remove(categoria)
        escopo.launch(Dispatchers.IO) { categoriaDAO.deletar(categoria) }
    }

    fun atualizarCategoria(editada: Categoria) {
        val posicao = categorias.indexOfFirst { it.id == editada.id }
        if (posicao != -1) categorias[posicao] = editada
        escopo.launch(Dispatchers.IO) { categoriaDAO.atualizar(editada) }
    }

    fun adicionarLoja(loja: Loja) {
        escopo.launch {
            try {
                val novoId = withContext(Dispatchers.IO) { lojaDAO.inserir(loja) }
                lojas.add(loja.copy(id = novoId.toInt()))
            } catch (e: Exception) {
                Log.e("Banco", "Erro ao inserir loja: ${e.message}")
            }
        }
    }

    fun removerLoja(loja: Loja) {
        lojas.remove(loja)
        produtos.removeAll { it.lojaId == loja.id }
        escopo.launch(Dispatchers.IO) {
            produtoDAO.deletarPorLoja(loja.id)
            lojaDAO.deletar(loja)
        }
    }

    fun atualizarLoja(lojaEditada: Loja) {
        val posicao = lojas.indexOfFirst { it.id == lojaEditada.id }
        if (posicao != -1) lojas[posicao] = lojaEditada
        escopo.launch(Dispatchers.IO) { lojaDAO.atualizar(lojaEditada) }
    }

    fun produtosDaLoja(lojaId: Int): List<Produto> {
        return produtos.filter { it.lojaId == lojaId }
    }

    fun adicionarProduto(produto: Produto) {
        escopo.launch {
            try {
                val novoId = withContext(Dispatchers.IO) { produtoDAO.inserir(produto) }
                produtos.add(produto.copy(id = novoId.toInt()))
            } catch (e: Exception) {
                Log.e("Banco", "Erro ao inserir produto: ${e.message}")
            }
        }
    }

    fun removerProduto(produto: Produto) {
        produtos.remove(produto)
        escopo.launch(Dispatchers.IO) { produtoDAO.deletar(produto) }
    }

    fun atualizarProduto(editado: Produto) {
        val posicao = produtos.indexOfFirst { it.id == editado.id }
        if (posicao != -1) produtos[posicao] = editado
        escopo.launch(Dispatchers.IO) { produtoDAO.atualizar(editado) }
    }


    // Cria um item a partir de um produto (para montar o carrinho)
    fun criarItem(produto: Produto, quantidade: Int): ItemPedido {
        return ItemPedido(
            produtoId = produto.id,
            nomeProduto = produto.nome,
            precoUnitario = produto.preco,
            quantidade = quantidade
        )
    }


    fun fazerPedido(loja: Loja, itens: List<ItemPedido> = emptyList(), valorManual: Double = 0.0) {
        val subtotal = if (itens.isEmpty()) valorManual
        else itens.sumOf { it.precoUnitario * it.quantidade }

        val pedido = Pedido(
            lojaId = loja.id,
            subtotal = subtotal,
            taxaEntrega = loja.taxaEntrega,
            total = subtotal + loja.taxaEntrega
        )


        pedidoAtual.value?.let { finalizarPedido(STATUS_CANCELADO) }

        escopo.launch {
            try {
                val (pedidoSalvo, itensSalvos) = withContext(Dispatchers.IO) {
                    val novoId = pedidoDAO.inserirPedido(pedido).toInt()
                    val itensComId = itens.map { it.copy(pedidoId = novoId) }
                    pedidoDAO.inserirItens(itensComId)
                    Pair(pedido.copy(id = novoId), itensComId)
                }
                historicoPedidos.add(0, pedidoSalvo)
                mostrarPedido(pedidoSalvo, itensSalvos)
            } catch (e: Exception) {
                Log.e("Banco", "Erro ao salvar pedido: ${e.message}")
            }
        }
    }

    fun avancarEtapa() {
        val pedido = pedidoAtual.value ?: return
        if (pedido.etapa >= etapas.size - 1) return

        val novaEtapa = pedido.etapa + 1
        val status = if (novaEtapa == etapas.size - 1) STATUS_ENTREGUE else STATUS_ANDAMENTO
        salvarPedido(pedido.copy(etapa = novaEtapa, status = status))
    }

    fun cancelarPedido() {
        finalizarPedido(STATUS_CANCELADO)
    }

    private fun finalizarPedido(status: String) {
        val pedido = pedidoAtual.value ?: return
        salvarPedido(pedido.copy(status = status))
        pedidoAtual.value = null
        itensPedidoAtual.clear()
        pedidoLojaId.value = 0
        etapaPedido.value = 0
    }

    private fun salvarPedido(pedido: Pedido) {
        if (pedido.status == STATUS_ANDAMENTO || pedido.status == STATUS_ENTREGUE) {
            pedidoAtual.value = pedido
            etapaPedido.value = pedido.etapa
        }
        val posicao = historicoPedidos.indexOfFirst { it.id == pedido.id }
        if (posicao != -1) historicoPedidos[posicao] = pedido
        escopo.launch(Dispatchers.IO) { pedidoDAO.atualizarPedido(pedido) }
    }

    private fun mostrarPedido(pedido: Pedido, itens: List<ItemPedido>) {
        pedidoAtual.value = pedido
        itensPedidoAtual.clear(); itensPedidoAtual.addAll(itens)
        pedidoLojaId.value = pedido.lojaId
        etapaPedido.value = pedido.etapa
    }

    fun buscarLoja(id: Int): Loja? = lojas.find { it.id == id }
    fun buscarCategoria(id: Int): Categoria? = categorias.find { it.id == id }
    fun buscarProduto(id: Int): Produto? = produtos.find { it.id == id }
    fun nomeCategoria(id: Int): String = buscarCategoria(id)?.nome ?: "Sem categoria"
}
