package com.example.interfaceifood.dados

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.interfaceifood.model.ItemPedido
import com.example.interfaceifood.model.Pedido

@Dao
interface PedidoDAO {


    @Insert
    suspend fun inserirPedido(pedido: Pedido): Long

    @Update
    suspend fun atualizarPedido(pedido: Pedido)

    @Delete
    suspend fun deletarPedido(pedido: Pedido)


    @Query("SELECT * FROM pedidos ORDER BY dataHora DESC")
    suspend fun buscarPedidos(): List<Pedido>

    @Query("SELECT * FROM pedidos WHERE status = 'Em andamento' ORDER BY id DESC LIMIT 1")
    suspend fun buscarPedidoEmAndamento(): Pedido?


    @Insert
    suspend fun inserirItens(itens: List<ItemPedido>)

    @Query("SELECT * FROM itens_pedido WHERE pedidoId = :pedidoId")
    suspend fun buscarItens(pedidoId: Int): List<ItemPedido>

    @Query("DELETE FROM itens_pedido WHERE pedidoId = :pedidoId")
    suspend fun deletarItens(pedidoId: Int)
}