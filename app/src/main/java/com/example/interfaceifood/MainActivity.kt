package com.example.interfaceifood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.interfaceifood.data.local.AppDatabase
import com.example.interfaceifood.data.repository.CategoriaRepository
import com.example.interfaceifood.data.repository.DadosIniciaisRepository
import com.example.interfaceifood.data.repository.LojaRepository
import com.example.interfaceifood.data.repository.PedidoRepository
import com.example.interfaceifood.data.repository.ProdutoRepository
import com.example.interfaceifood.ui.navegacao.AppNavigation
import com.example.interfaceifood.ui.theme.InterfaceIfoodTheme
import com.example.interfaceifood.viewmodel.AppViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        val factory = AppViewModelFactory(
            LojaRepository(db.lojaDAO()),
            CategoriaRepository(db.categoriaDAO()),
            ProdutoRepository(db.produtoDAO()),
            PedidoRepository(db.pedidoDAO())
        )

        lifecycleScope.launch { DadosIniciaisRepository(db).popularSeVazio() }

        enableEdgeToEdge()
        setContent {
            InterfaceIfoodTheme {
                AppNavigation(factory)
            }
        }
    }
}
