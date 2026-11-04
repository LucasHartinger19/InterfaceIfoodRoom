package com.example.interfaceifood.data.repository

import com.example.interfaceifood.data.local.Produto
import com.example.interfaceifood.data.local.ProdutoDAO
import kotlinx.coroutines.flow.Flow

class ProdutoRepository(private val produtoDao: ProdutoDAO) {

    fun buscarTodos(): Flow<List<Produto>> = produtoDao.buscarTodos()

    suspend fun buscarPorLoja(lojaId: Int): List<Produto> = produtoDao.buscarPorLoja(lojaId)

    suspend fun inserir(produto: Produto): Long = produtoDao.inserir(produto)

    suspend fun atualizar(produto: Produto) = produtoDao.atualizar(produto)

    suspend fun deletar(produto: Produto) = produtoDao.deletar(produto)

    suspend fun deletarPorLoja(lojaId: Int) = produtoDao.deletarPorLoja(lojaId)
}
