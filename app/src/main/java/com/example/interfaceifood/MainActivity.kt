package com.example.interfaceifood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.interfaceifood.dados.DadosApp
import com.example.interfaceifood.navegacao.AppNavigation
import com.example.interfaceifood.ui.theme.InterfaceIfoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DadosApp.iniciar(applicationContext)
        enableEdgeToEdge()
        setContent {
            InterfaceIfoodTheme {
                AppNavigation()
            }
        }
    }
}
