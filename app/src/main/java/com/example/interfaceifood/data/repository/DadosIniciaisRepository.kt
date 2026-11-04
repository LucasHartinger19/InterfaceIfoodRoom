package com.example.interfaceifood.data.repository

import com.example.interfaceifood.data.local.AppDatabase
import com.example.interfaceifood.data.local.DadosIniciais

class DadosIniciaisRepository(private val db: AppDatabase) {

    // Só insere na primeira execução, quando ainda não existe nenhuma categoria.
    suspend fun popularSeVazio() {
        if (db.categoriaDAO().contar() > 0) return

        DadosIniciais.categorias.forEach { db.categoriaDAO().inserir(it) }
        DadosIniciais.lojas.forEach { db.lojaDAO().inserir(it) }
        DadosIniciais.produtos.forEach { db.produtoDAO().inserir(it) }
    }
}
