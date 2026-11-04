package com.example.interfaceifood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.interfaceifood.data.repository.CategoriaRepository
import com.example.interfaceifood.data.repository.LojaRepository
import com.example.interfaceifood.data.repository.PedidoRepository
import com.example.interfaceifood.data.repository.ProdutoRepository

// Uma só Factory reaproveitada: todos os ViewModels usam os mesmos Repositories.
// idDetalhe é o id da rota, usado pelas duas telas de detalhe.
class AppViewModelFactory(
    private val lojaRepository: LojaRepository,
    private val categoriaRepository: CategoriaRepository,
    private val produtoRepository: ProdutoRepository,
    private val pedidoRepository: PedidoRepository,
    private val idDetalhe: Int = 0
) : ViewModelProvider.Factory {

    fun comId(id: Int) = AppViewModelFactory(
        lojaRepository, categoriaRepository, produtoRepository, pedidoRepository, id
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = when (modelClass) {
            InicioViewModel::class.java -> InicioViewModel(lojaRepository, categoriaRepository)
            BuscaViewModel::class.java -> BuscaViewModel(lojaRepository, categoriaRepository)
            LojasViewModel::class.java ->
                LojasViewModel(lojaRepository, categoriaRepository, produtoRepository)
            CategoriasViewModel::class.java -> CategoriasViewModel(categoriaRepository, lojaRepository)
            DetalheLojaViewModel::class.java ->
                DetalheLojaViewModel(idDetalhe, lojaRepository, categoriaRepository, pedidoRepository)
            DetalheCategoriaViewModel::class.java ->
                DetalheCategoriaViewModel(idDetalhe, categoriaRepository, lojaRepository)
            PedidoViewModel::class.java -> PedidoViewModel(pedidoRepository, lojaRepository)
            else -> throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
        }
        return viewModel as T
    }
}
