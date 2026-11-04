package com.example.interfaceifood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interfaceifood.data.local.Categoria
import com.example.interfaceifood.data.local.Loja
import com.example.interfaceifood.data.repository.CategoriaRepository
import com.example.interfaceifood.data.repository.LojaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val FILTRO_AVALIACAO = "Melhor avaliação"
const val FILTRO_GRATIS = "Entrega grátis"
const val FILTRO_FAVORITAS = "Favoritas"

data class BuscaUiState(
    val textoBusca: String = "",
    val filtroSelecionado: String = "",
    val filtros: List<String> = listOf(FILTRO_AVALIACAO, FILTRO_GRATIS, FILTRO_FAVORITAS),
    val lojas: List<Loja> = emptyList(),
    val categorias: List<Categoria> = emptyList()
) {
    val resultados: List<Loja>
        get() {
            val encontradas = lojas.filter { it.nome.contains(textoBusca, ignoreCase = true) }
            return when (filtroSelecionado) {
                FILTRO_AVALIACAO -> encontradas.sortedByDescending { it.avaliacao }
                FILTRO_GRATIS -> encontradas.filter { it.taxaEntrega == 0.0 }
                FILTRO_FAVORITAS -> encontradas.filter { it.favorita }
                else -> encontradas
            }
        }
}

class BuscaViewModel(
    private val lojaRepository: LojaRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuscaUiState())
    val uiState: StateFlow<BuscaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            lojaRepository.buscarTodos().collect { lista ->
                _uiState.update { it.copy(lojas = lista) }
            }
        }
        viewModelScope.launch {
            categoriaRepository.buscarTodos().collect { lista ->
                _uiState.update { it.copy(categorias = lista) }
            }
        }
    }

    fun alterarTextoBusca(texto: String) {
        _uiState.update { it.copy(textoBusca = texto) }
    }

    // Tocar no filtro já selecionado desliga o filtro.
    fun selecionarFiltro(filtro: String) {
        _uiState.update {
            it.copy(filtroSelecionado = if (it.filtroSelecionado == filtro) "" else filtro)
        }
    }
}
