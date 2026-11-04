package com.example.interfaceifood.data.repository

import com.example.interfaceifood.data.local.Loja
import com.example.interfaceifood.data.local.LojaDAO
import kotlinx.coroutines.flow.Flow

class LojaRepository(private val lojaDao: LojaDAO) {

    fun buscarTodos(): Flow<List<Loja>> = lojaDao.buscarTodos()

    fun buscarPorId(id: Int): Flow<Loja?> = lojaDao.buscarPorId(id)

    suspend fun inserir(loja: Loja): Long = lojaDao.inserir(loja)

    suspend fun atualizar(loja: Loja) = lojaDao.atualizar(loja)

    suspend fun deletar(loja: Loja) = lojaDao.deletar(loja)
}
