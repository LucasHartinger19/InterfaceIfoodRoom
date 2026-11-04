package com.example.interfaceifood.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDAO {

    @Insert
    suspend fun inserir(produto: Produto): Long

    @Query("SELECT * FROM produtos")
    fun buscarTodos(): Flow<List<Produto>>

    @Query("SELECT * FROM produtos WHERE lojaId = :lojaId")
    suspend fun buscarPorLoja(lojaId: Int): List<Produto>

    @Query("SELECT * FROM produtos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Produto?

    @Update
    suspend fun atualizar(produto: Produto)

    @Delete
    suspend fun deletar(produto: Produto)


    @Query("DELETE FROM produtos WHERE lojaId = :lojaId")
    suspend fun deletarPorLoja(lojaId: Int)
}