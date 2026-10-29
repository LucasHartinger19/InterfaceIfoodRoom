package com.example.interfaceifood.dados

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.interfaceifood.model.Categoria

@Dao
interface CategoriaDAO {

    @Insert
    suspend fun inserir(categoria: Categoria): Long

    @Query("SELECT * FROM categorias")
    suspend fun buscarCategorias(): List<Categoria>

    @Update
    suspend fun atualizar(categoria: Categoria)

    @Delete
    suspend fun deletar(categoria: Categoria)
}