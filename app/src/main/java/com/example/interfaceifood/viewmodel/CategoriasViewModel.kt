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

data class CategoriasUiState(
    val categorias: List<Categoria> = emptyList(),
    val lojas: List<Loja> = emptyList(),
    val nome: String = "",
    val descricao: String = ""
) {
    fun quantidadeLojas(categoriaId: Int): Int = lojas.count { it.categoriaId == categoriaId }
}

class CategoriasViewModel(
    private val categoriaRepository: CategoriaRepository,
    private val lojaRepository: LojaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriasUiState())
    val uiState: StateFlow<CategoriasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoriaRepository.buscarTodos().collect { lista ->
                _uiState.update { it.copy(categorias = lista) }
            }
        }
        viewModelScope.launch {
            lojaRepository.buscarTodos().collect { lista ->
                _uiState.update { it.copy(lojas = lista) }
            }
        }
    }

    fun alterarNome(valor: String) = _uiState.update { it.copy(nome = valor) }
    fun alterarDescricao(valor: String) = _uiState.update { it.copy(descricao = valor) }

    // Devolve false quando o nome está vazio, para a tela mostrar o aviso.
    fun adicionarCategoria(): Boolean {
        val estado = _uiState.value
        if (estado.nome == "") return false

        viewModelScope.launch {
            categoriaRepository.inserir(Categoria(0, estado.nome, estado.descricao))
        }
        _uiState.update { it.copy(nome = "", descricao = "") }
        return true
    }

    // Devolve false quando a categoria ainda tem lojas e não pode ser removida.
    fun removerCategoria(categoria: Categoria): Boolean {
        if (_uiState.value.quantidadeLojas(categoria.id) > 0) return false

        viewModelScope.launch { categoriaRepository.deletar(categoria) }
        return true
    }
}
