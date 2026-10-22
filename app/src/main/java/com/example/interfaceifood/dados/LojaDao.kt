package com.example.interfaceifood.dados

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.interfaceifood.model.Loja

@Dao
interface LojaDAO {

    @Insert
    suspend fun inserir(loja: Loja): Long

    @Query("SELECT * FROM lojas")
    suspend fun buscarLojas(): List<Loja>

    @Update
    suspend fun atualizar(loja: Loja)

    @Delete
    suspend fun deletar(loja: Loja)
}
