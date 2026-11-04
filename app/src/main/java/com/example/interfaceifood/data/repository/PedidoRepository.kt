package com.example.interfaceifood.data.repository

import com.example.interfaceifood.data.local.ItemPedido
import com.example.interfaceifood.data.local.Pedido
import com.example.interfaceifood.data.local.PedidoDAO
import kotlinx.coroutines.flow.Flow

class PedidoRepository(private val pedidoDao: PedidoDAO) {

    fun buscarTodos(): Flow<List<Pedido>> = pedidoDao.buscarTodos()

    suspend fun buscarPedidoEmAndamento(): Pedido? = pedidoDao.buscarPedidoEmAndamento()

    suspend fun inserir(pedido: Pedido): Long = pedidoDao.inserirPedido(pedido)

    suspend fun atualizar(pedido: Pedido) = pedidoDao.atualizarPedido(pedido)

    suspend fun deletar(pedido: Pedido) = pedidoDao.deletarPedido(pedido)

    suspend fun inserirItens(itens: List<ItemPedido>) = pedidoDao.inserirItens(itens)

    suspend fun buscarItens(pedidoId: Int): List<ItemPedido> = pedidoDao.buscarItens(pedidoId)
}
