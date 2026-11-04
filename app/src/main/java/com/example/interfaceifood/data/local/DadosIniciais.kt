package com.example.interfaceifood.data.local

object DadosIniciais {

    val categorias = listOf(
        Categoria(1, "Pizza", "Pizzas tradicionais e especiais"),
        Categoria(2, "Lanches", "Hambúrgueres e sanduíches"),
        Categoria(3, "Japonesa", "Sushi, temaki e yakisoba"),
        Categoria(4, "Brasileira", "Pratos feitos e comida caseira"),
        Categoria(5, "Doces", "Bolos, açaí e sobremesas")
    )

    val lojas = listOf(
        Loja(1, "Mc Donalds", 2, 4.8, "20-30 min", 0.0),
        Loja(2, "Yaki i'n house", 3, 4.9, "35-45 min", 4.99),
        Loja(3, "Dalle Pizza", 1, 4.7, "30-40 min", 2.50),
        Loja(4, "Maias Pizzaria", 1, 4.9, "30-40 min", 5.0),
        Loja(5, "Burguer King", 2, 4.8, "15-25 min", 0.0)
    )

    val produtos = listOf(
        Produto(lojaId = 1, nome = "Big Mac", descricao = "Dois hambúrgueres, alface, queijo e molho especial", preco = 29.90),
        Produto(lojaId = 1, nome = "McFritas Média", descricao = "Batata frita crocante", preco = 12.90),
        Produto(lojaId = 2, nome = "Combo 20 peças", descricao = "Sushi e sashimi variados", preco = 69.90),
        Produto(lojaId = 2, nome = "Temaki Salmão", descricao = "Salmão, arroz e cebolinha", preco = 27.00),
        Produto(lojaId = 3, nome = "Pizza Calabresa G", descricao = "Calabresa, cebola e azeitona", preco = 54.90),
        Produto(lojaId = 3, nome = "Pizza Margherita G", descricao = "Muçarela, tomate e manjericão", preco = 52.90),
        Produto(lojaId = 4, nome = "Pizza Quatro Queijos G", descricao = "Muçarela, provolone, parmesão e catupiry", preco = 59.90),
        Produto(lojaId = 4, nome = "Refrigerante 2L", descricao = "Coca-Cola ou Guaraná", preco = 14.00),
        Produto(lojaId = 5, nome = "Whopper", descricao = "Hambúrguer grelhado no fogo", preco = 31.90),
        Produto(lojaId = 5, nome = "Onion Rings", descricao = "Anéis de cebola empanados", preco = 13.90)
    )
}
