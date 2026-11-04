package com.example.interfaceifood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interfaceifood.data.local.Categoria
import com.example.interfaceifood.data.local.Loja
import com.example.interfaceifood.data.repository.CategoriaRepository
import com.example.interfaceifood.data.repository.LojaRepository
import com.example.interfaceifood.data.repository.ProdutoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LojasUiState(
    val lojas: List<Loja> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val nome: String = "",
    val avaliacao: String = "",
    val tempo: String = "",
    val taxa: String = "",
    val categoriaId: Int = 1
)

class LojasViewModel(
    private val lojaRepository: LojaRepository,
    private val categoriaRepository: CategoriaRepository,
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LojasUiState())
    val uiState: StateFlow<LojasUiState> = _uiState.asStateFlow()

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

    fun alterarNome(valor: String) = _uiState.update { it.copy(nome = valor) }
    fun alterarAvaliacao(valor: String) = _uiState.update { it.copy(avaliacao = valor) }
    fun alterarTempo(valor: String) = _uiState.update { it.copy(tempo = valor) }
    fun alterarTaxa(valor: String) = _uiState.update { it.copy(taxa = valor) }
    fun selecionarCategoria(id: Int) = _uiState.update { it.copy(categoriaId = id) }

    // Devolve false quando o nome está vazio, para a tela mostrar o aviso.
    fun adicionarLoja(): Boolean {
        val estado = _uiState.value
        if (estado.nome == "") return false

        val novaLoja = Loja(
            0, // o Room gera o id (autoGenerate)
            estado.nome,
            estado.categoriaId,
            estado.avaliacao.toDoubleOrNull() ?: 0.0,
            estado.tempo,
            estado.taxa.toDoubleOrNull() ?: 0.0
        )
        viewModelScope.launch { lojaRepository.inserir(novaLoja) }

        _uiState.update { it.copy(nome = "", avaliacao = "", tempo = "", taxa = "") }
        return true
    }

    fun alterarFavorita(loja: Loja, favorita: Boolean) {
        viewModelScope.launch { lojaRepository.atualizar(loja.copy(favorita = favorita)) }
    }

    // Os produtos saem junto, para não sobrar produto sem loja no banco.
    fun removerLoja(loja: Loja) {
        viewModelScope.launch {
            produtoRepository.deletarPorLoja(loja.id)
            lojaRepository.deletar(loja)
        }
    }
}
