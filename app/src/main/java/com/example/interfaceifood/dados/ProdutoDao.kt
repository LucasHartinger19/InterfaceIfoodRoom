package com.example.interfaceifood.dados

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.interfaceifood.model.Produto

@Dao
interface ProdutoDAO {

    @Insert
    suspend fun inserir(produto: Produto): Long

    @Query("SELECT * FROM produtos")
    suspend fun buscarProdutos(): List<Produto>

    @Query("SELECT * FROM produtos WHERE lojaId = :lojaId")
    suspend fun buscarPorLoja(lojaId: Int): List<Produto>

    @Update
    suspend fun atualizar(produto: Produto)

    @Delete
    suspend fun deletar(produto: Produto)

    // usado quando uma loja é removida
    @Query("DELETE FROM produtos WHERE lojaId = :lojaId")
    suspend fun deletarPorLoja(lojaId: Int)
}
