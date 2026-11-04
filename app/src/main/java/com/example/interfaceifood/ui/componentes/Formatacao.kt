package com.example.interfaceifood.ui.componentes

import com.example.interfaceifood.data.local.Categoria

fun textoTaxa(taxa: Double): String {
    if (taxa == 0.0) {
        return "Entrega Grátis"
    }
    return "Entrega R$ " + taxa
}

fun nomeCategoria(categorias: List<Categoria>, id: Int): String {
    return categorias.find { it.id == id }?.nome ?: "Sem categoria"
}
