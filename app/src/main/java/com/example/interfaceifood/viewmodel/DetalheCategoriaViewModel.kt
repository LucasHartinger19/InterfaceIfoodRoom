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

data class DetalheCategoriaUiState(
    val categoria: Categoria? = null,
    val categorias: List<Categoria> = emptyList(),
    val lojas: List<Loja> = emptyList(),
    val carregando: Boolean = true,
    val nomeEditado: String = "",
    val descricaoEditada: String = ""
) {
    val lojasDaCategoria: List<Loja>
        get() = lojas.filter { it.categoriaId == categoria?.id }
}

class DetalheCategoriaViewModel(
    categoriaId: Int,
    private val categoriaRepository: CategoriaRepository,
    private val lojaRepository: LojaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalheCategoriaUiState())
    val uiState: StateFlow<DetalheCategoriaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoriaRepository.buscarPorId(categoriaId).collect { encontrada ->
                _uiState.update { estado ->
                    // O formulário só é preenchido na primeira leitura, para o
                    // Flow não apagar o que o usuário está digitando.
                    if (estado.carregando) {
                        estado.copy(
                            categoria = encontrada,
                            carregando = false,
                            nomeEditado = encontrada?.nome ?: "",
                            descricaoEditada = encontrada?.descricao ?: ""
                        )
                    } else {
                        estado.copy(categoria = encontrada)
                    }
                }
            }
        }
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

    fun alterarNome(valor: String) = _uiState.update { it.copy(nomeEditado = valor) }
    fun alterarDescricao(valor: String) = _uiState.update { it.copy(descricaoEditada = valor) }

    // Devolve false quando o nome está vazio, para a tela mostrar o aviso.
    fun salvarAlteracoes(): Boolean {
        val estado = _uiState.value
        val categoria = estado.categoria ?: return false
        if (estado.nomeEditado == "") return false

        viewModelScope.launch {
            categoriaRepository.atualizar(
                categoria.copy(nome = estado.nomeEditado, descricao = estado.descricaoEditada)
            )
        }
        return true
    }
}
