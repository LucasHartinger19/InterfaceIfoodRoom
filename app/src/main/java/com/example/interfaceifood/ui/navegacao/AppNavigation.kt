package com.example.interfaceifood.ui.navegacao

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.interfaceifood.ui.telas.TelaAcompanharPedido
import com.example.interfaceifood.ui.telas.TelaBusca
import com.example.interfaceifood.ui.telas.TelaDetalheCategoria
import com.example.interfaceifood.ui.telas.TelaDetalheLoja
import com.example.interfaceifood.ui.telas.TelaInicial
import com.example.interfaceifood.ui.telas.TelaListaCategorias
import com.example.interfaceifood.ui.telas.TelaListaLojas
import com.example.interfaceifood.ui.theme.VermelhoIfood
import com.example.interfaceifood.viewmodel.AppViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(factory: AppViewModelFactory) {
    val navController = rememberNavController()


    val telaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = telaAtual?.destination?.route

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text(tituloDaTela(rotaAtual)) },
                navigationIcon = {
                    if (rotaAtual != Rotas.INICIO) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VermelhoIfood,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },


        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = rotaAtual == Rotas.INICIO,
                    onClick = { navController.navigate(Rotas.INICIO) },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Início") },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = rotaAtual == Rotas.BUSCA,
                    onClick = { navController.navigate(Rotas.BUSCA) },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Busca") },
                    label = { Text("Busca") }
                )
                NavigationBarItem(
                    selected = rotaAtual == Rotas.LOJAS,
                    onClick = { navController.navigate(Rotas.LOJAS) },
                    icon = { Icon(Icons.Filled.Place, contentDescription = "Lojas") },
                    label = { Text("Lojas") }
                )
                NavigationBarItem(
                    selected = rotaAtual == Rotas.CATEGORIAS,
                    onClick = { navController.navigate(Rotas.CATEGORIAS) },
                    icon = { Icon(Icons.Filled.Menu, contentDescription = "Categorias") },
                    label = { Text("Categorias") }
                )
                NavigationBarItem(
                    selected = rotaAtual == Rotas.PEDIDO,
                    onClick = { navController.navigate(Rotas.PEDIDO) },
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Pedido") },
                    label = { Text("Pedido") }
                )
            }
        }
    ) { innerPadding ->


        NavHost(
            navController = navController,
            startDestination = Rotas.INICIO,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.INICIO) { TelaInicial(navController, factory) }
            composable(Rotas.BUSCA) { TelaBusca(navController, factory) }
            composable(Rotas.LOJAS) { TelaListaLojas(navController, factory) }
            composable(Rotas.CATEGORIAS) { TelaListaCategorias(navController, factory) }
            composable(Rotas.PEDIDO) { TelaAcompanharPedido(navController, factory) }


            composable(Rotas.DETALHE_LOJA) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: "0"
                TelaDetalheLoja(navController, factory, id.toInt())
            }
            composable(Rotas.DETALHE_CATEGORIA) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: "0"
                TelaDetalheCategoria(navController, factory, id.toInt())
            }
        }
    }
}

fun tituloDaTela(rota: String?): String {
    if (rota == Rotas.BUSCA) return "Buscar"
    if (rota == Rotas.LOJAS) return "Lojas"
    if (rota == Rotas.CATEGORIAS) return "Categorias"
    if (rota == Rotas.PEDIDO) return "Acompanhar Pedido"
    if (rota == Rotas.DETALHE_LOJA) return "Detalhe da Loja"
    if (rota == Rotas.DETALHE_CATEGORIA) return "Detalhe da Categoria"
    return "iFood"
}