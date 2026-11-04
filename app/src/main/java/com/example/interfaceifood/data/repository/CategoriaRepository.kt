package com.example.interfaceifood.data.repository

import com.example.interfaceifood.data.local.Categoria
import com.example.interfaceifood.data.local.CategoriaDAO
import kotlinx.coroutines.flow.Flow

class CategoriaRepository(private val categoriaDao: CategoriaDAO) {

    fun buscarTodos(): Flow<List<Categoria>> = categoriaDao.buscarTodos()

    fun buscarPorId(id: Int): Flow<Categoria?> = categoriaDao.buscarPorId(id)

    suspend fun inserir(categoria: Categoria): Long = categoriaDao.inserir(categoria)

    suspend fun atualizar(categoria: Categoria) = categoriaDao.atualizar(categoria)

    suspend fun deletar(categoria: Categoria) = categoriaDao.deletar(categoria)
}
