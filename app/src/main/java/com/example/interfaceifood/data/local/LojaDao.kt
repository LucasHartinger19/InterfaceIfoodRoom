package com.example.interfaceifood.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LojaDAO {

    @Insert
    suspend fun inserir(loja: Loja): Long

    @Query("SELECT * FROM lojas")
    fun buscarTodos(): Flow<List<Loja>>

    @Query("SELECT * FROM lojas WHERE id = :id")
    fun buscarPorId(id: Int): Flow<Loja?>

    @Update
    suspend fun atualizar(loja: Loja)

    @Delete
    suspend fun deletar(loja: Loja)
}