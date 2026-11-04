package com.example.interfaceifood.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDAO {

    @Insert
    suspend fun inserir(categoria: Categoria): Long

    @Query("SELECT * FROM categorias")
    fun buscarTodos(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    fun buscarPorId(id: Int): Flow<Categoria?>

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun contar(): Int

    @Update
    suspend fun atualizar(categoria: Categoria)

    @Delete
    suspend fun deletar(categoria: Categoria)
}