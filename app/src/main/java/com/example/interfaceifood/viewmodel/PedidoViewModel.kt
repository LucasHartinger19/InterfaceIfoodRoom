package com.example.interfaceifood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interfaceifood.data.local.ETAPAS_PEDIDO
import com.example.interfaceifood.data.local.Loja
import com.example.interfaceifood.data.local.Pedido
import com.example.interfaceifood.data.local.STATUS_ANDAMENTO
import com.example.interfaceifood.data.local.STATUS_CANCELADO
import com.example.interfaceifood.data.local.STATUS_ENTREGUE
import com.example.interfaceifood.data.repository.LojaRepository
import com.example.interfaceifood.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class PedidoUiState(
    val pedido: Pedido? = null,
    val loja: Loja? = null,
    val etapas: List<String> = ETAPAS_PEDIDO,
    val carregando: Boolean = true
)

class PedidoViewModel(
    private val pedidoRepository: PedidoRepository,
    private val lojaRepository: LojaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PedidoUiState())
    val uiState: StateFlow<PedidoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // A lista vem do mais recente para o mais antigo. Se o último pedido
            // foi cancelado, a tela volta para "nenhum pedido".
            combine(
                pedidoRepository.buscarTodos(),
                lojaRepository.buscarTodos()
            ) { pedidos, lojas ->
                val ultimo = pedidos.firstOrNull()?.takeIf { it.status != STATUS_CANCELADO }
                PedidoUiState(
                    pedido = ultimo,
                    loja = lojas.find { it.id == ultimo?.lojaId },
                    carregando = false
                )
            }.collect { _uiState.value = it }
        }
    }

    // Devolve false quando o pedido já foi entregue.
    fun avancarEtapa(): Boolean {
        val pedido = _uiState.value.pedido ?: return false
        val ultimaEtapa = ETAPAS_PEDIDO.size - 1
        if (pedido.etapa >= ultimaEtapa) return false

        val novaEtapa = pedido.etapa + 1
        val status = if (novaEtapa == ultimaEtapa) STATUS_ENTREGUE else STATUS_ANDAMENTO
        viewModelScope.launch {
            pedidoRepository.atualizar(pedido.copy(etapa = novaEtapa, status = status))
        }
        return true
    }

    fun cancelarPedido() {
        val pedido = _uiState.value.pedido ?: return
        viewModelScope.launch {
            pedidoRepository.atualizar(pedido.copy(status = STATUS_CANCELADO))
        }
    }
}
