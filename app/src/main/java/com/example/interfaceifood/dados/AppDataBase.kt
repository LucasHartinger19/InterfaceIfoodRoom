package com.example.interfaceifood.dados

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.interfaceifood.model.Categoria
import com.example.interfaceifood.model.ItemPedido
import com.example.interfaceifood.model.Loja
import com.example.interfaceifood.model.Pedido
import com.example.interfaceifood.model.Produto

@Database(
    entities = [
        Categoria::class,
        Loja::class,
        Produto::class,
        Pedido::class,
        ItemPedido::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDAO(): CategoriaDAO
    abstract fun lojaDAO(): LojaDAO
    abstract fun produtoDAO(): ProdutoDAO
    abstract fun pedidoDAO(): PedidoDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ifood_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
