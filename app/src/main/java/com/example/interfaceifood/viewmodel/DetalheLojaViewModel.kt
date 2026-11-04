package com.example.interfaceifood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interfaceifood.data.local.Categoria
import com.example.interfaceifood.data.local.Loja
import com.example.interfaceifood.data.local.Pedido
import com.example.interfaceifood.data.local.STATUS_CANCELADO
import com.example.interfaceifood.data.repository.CategoriaRepository
import com.example.interfaceifood.data.repository.LojaRepository
import com.example.interfaceifood.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalheLojaUiState(
    val loja: Loja? = null,
    val categorias: List<Categoria> = emptyList(),
    val carregando: Boolean = true,
    val novaNota: String = "",
    val valorPedido: String = ""
) {
    val subtotal: Double get() = valorPedido.toDoubleOrNull() ?: 0.0
    val total: Double get() = subtotal + (loja?.taxaEntrega ?: 0.0)
}

class DetalheLojaViewModel(
    lojaId: Int,
    private val lojaRepository: LojaRepository,
    private val categoriaRepository: CategoriaRepository,
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalheLojaUiState())
    val uiState: StateFlow<DetalheLojaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            lojaRepository.buscarPorId(lojaId).collect { encontrada ->
                _uiState.update { it.copy(loja = encontrada, carregando = false) }
            }
        }
        viewModelScope.launch {
            categoriaRepository.buscarTodos().collect { lista ->
                _uiState.update { it.copy(categorias = lista) }
            }
        }
    }

    fun alterarNovaNota(valor: String) = _uiState.update { it.copy(novaNota = valor) }
    fun alterarValorPedido(valor: String) = _uiState.update { it.copy(valorPedido = valor) }

    fun alterarFavorita(favorita: Boolean) {
        val loja = _uiState.value.loja ?: return
        viewModelScope.launch { lojaRepository.atualizar(loja.copy(favorita = favorita)) }
    }

    // Devolve false quando a nota não é um número de 0 a 5.
    fun salvarNota(): Boolean {
        val loja = _uiState.value.loja ?: return false
        val nota = _uiState.value.novaNota.replace(",", ".").toDoubleOrNull()
        if (nota == null || nota < 0 || nota > 5) return false

        viewModelScope.launch { lojaRepository.atualizar(loja.copy(avaliacao = nota)) }
        _uiState.update { it.copy(novaNota = "") }
        return true
    }

    // Só existe um pedido em andamento por vez: o anterior é cancelado.
    fun fazerPedido() {
        val estado = _uiState.value
        val loja = estado.loja ?: return

        viewModelScope.launch {
            val anterior = pedidoRepository.buscarPedidoEmAndamento()
            if (anterior != null) {
                pedidoRepository.atualizar(anterior.copy(status = STATUS_CANCELADO))
            }
            pedidoRepository.inserir(
                Pedido(
                    lojaId = loja.id,
                    subtotal = estado.subtotal,
                    taxaEntrega = loja.taxaEntrega,
                    total = estado.total
                )
            )
        }
    }
}
