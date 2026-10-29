# Interface iFood — Trabalho 3 (Persistência de Dados com Room)

App Android em **Kotlin + Jetpack Compose** inspirado no iFood. É a continuação do
Trabalho 2 ([IfoodInterface](https://github.com/LucasHartinger19/IfoodInterface)):
o app continua com as mesmas telas, navegação e listas, mas agora **todos os dados
ficam salvos em um banco SQLite usando o Room**. Lojas, categorias e pedidos
sobrevivem ao fechar e abrir o app.

**Integrantes:**
- Lucas Pereira Hartinger
- Luis Felipe de Paula Fogaça
- Felipe Paulista Silveira

---

## Como rodar o projeto

1. Clone o repositório:
   ```
      git clone https://github.com/LucasHartinger19/InterfaceIfoodRoom.git
   ```
2. Abra a pasta no **Android Studio** e espere o **Gradle Sync** terminar.
3. Inicie um emulador (ou conecte um celular com depuração USB) com **Android 7.0 (API 24) ou superior**.
4. Clique em **Run ▶**.

> Se o app de uma versão anterior (Trabalho 2) já estiver instalado no emulador,
> desinstale antes de rodar. Assim o banco é criado do zero com os dados iniciais.

**Versões usadas:** AGP 9.0.0 · Kotlin 2.2.21 · KSP 2.3.4 · Room 3.0.3 · compileSdk 37 · minSdk 24



## 1. Como estava o projeto no Trabalho 2 e o que mudou

No Trabalho 2, todos os dados ficavam no objeto `DadosApp`, em listas
`mutableStateListOf` preenchidas direto no código:

```kotlin
val lojas = mutableStateListOf(
    Loja(1, "Mc Donalds", 2, 4.8, "20-30 min", 0.0),
    ...
)
```

A navegação e o CRUD funcionavam, mas tudo ficava só na memória. Quando a Activity
era destruída (app fechado), qualquer loja adicionada, removida ou editada se perdia.

**O que mudou no Trabalho 3:**

| Antes (Trabalho 2) | Agora (Trabalho 3) |
|---|---|
| Dados fixos dentro do `DadosApp` | Dados salvos no banco `ifood_database` (SQLite via Room) |
| IDs controlados à mão (`proximoIdLoja++`) | IDs gerados pelo banco (`autoGenerate = true`) |
| Adicionar/remover só mexia na lista | Cada ação chama o DAO (`inserir`, `atualizar`, `deletar`) |
| Telas de detalhe procuravam na lista | Telas de detalhe buscam no banco pelo id (`buscarPorId`) |
| Pedido existia só enquanto o app estava aberto | Pedido e histórico ficam salvos e são restaurados ao abrir o app |

As listas `mutableStateListOf` **continuam existindo**, mas agora servem só para a
tela se atualizar sozinha. A fonte da verdade é o banco: sempre que o app abre, elas
são preenchidas a partir dele.


## 2. Entity, DAO e Database criados

### Entities (`model/Modelos.kt`)

Aproveitamos as data classes `Loja` e `Categoria` do Trabalho 2 e criamos três novas
para o pedido. Todas usam `@Entity(tableName = "...")` e
`@PrimaryKey(autoGenerate = true) val id: Int = 0`.

| Entity | Tabela | Campos | Por que esses campos |
|---|---|---|---|
| `Categoria` | `categorias` | id, nome, descricao | Os mesmos do Trabalho 2: é o que aparece nos chips e no detalhe da categoria. |
| `Loja` | `lojas` | id, nome, categoriaId, avaliacao, tempoEntrega, taxaEntrega, favorita | Tudo o que a lista, a busca e os filtros usam. `categoriaId` liga a loja à categoria. `favorita` agora fica salva. |
| `Produto` | `produtos` | id, lojaId, nome, descricao, preco, disponivel | Cardápio de cada loja. `lojaId` indica de qual loja é o produto. |
| `Pedido` | `pedidos` | id, lojaId, dataHora, etapa, status, subtotal, taxaEntrega, total | Para o acompanhamento do pedido sobreviver ao fechar o app. `etapa` guarda o passo atual e `status` diz se está em andamento, entregue ou cancelado. |
| `ItemPedido` | `itens_pedido` | id, pedidoId, produtoId, nomeProduto, precoUnitario, quantidade | Itens de cada pedido. Guardamos nome e preço no momento da compra, para o pedido não mudar se o produto for editado depois. |

### DAOs (`dados/`)

| DAO | Métodos básicos | Consultas extras |
|---|---|---|
| `CategoriaDAO` | inserir, buscarCategorias, atualizar, deletar | `buscarPorId(id)` |
| `LojaDAO` | inserir, buscarLojas, atualizar, deletar | `buscarPorId(id)` |
| `ProdutoDAO` | inserir, buscarProdutos, atualizar, deletar | `buscarPorId(id)`, `buscarPorLoja(lojaId)`, `deletarPorLoja(lojaId)` |
| `PedidoDAO` | inserirPedido, buscarPedidos, atualizarPedido, deletarPedido | `buscarPedidoEmAndamento()`, `inserirItens`, `buscarItens(pedidoId)`, `deletarItens(pedidoId)` |

Todos os métodos são `suspend fun`.

### Database (`dados/AppDataBase.kt`)

A classe `AppDatabase` estende `RoomDatabase`, lista as 5 entidades em
`@Database(entities = [...], version = 1)` e expõe os 4 DAOs. Ela segue o padrão
**Singleton** visto em aula (`companion object` + `@Volatile` + `synchronized`), para
existir só uma conexão com o banco no app inteiro.

O banco é aberto uma vez na `MainActivity`, com `DadosApp.iniciar(applicationContext)`.
Na primeira execução, se a tabela de categorias estiver vazia, o app insere os dados
iniciais (5 categorias, 5 lojas e 10 produtos).

---

## 3. Como estruturamos as queries extras

- **`buscarPorId(id)`** em Loja, Categoria e Produto: é o que as telas de detalhe
  usam. Quando o usuário toca em uma loja, a rota `detalheLoja/{id}` passa o id, e a
  tela faz `SELECT * FROM lojas WHERE id = :id` em vez de procurar na lista em memória.
- **`buscarPorLoja(lojaId)`**: devolve só os produtos de uma loja (o cardápio).
- **`deletarPorLoja(lojaId)`**: quando uma loja é removida, os produtos dela também
  são apagados, para não sobrar produto sem loja no banco.
- **`buscarPedidoEmAndamento()`**: busca o último pedido com status
  "Em andamento". É o que permite reabrir o app e continuar acompanhando o pedido.
- **`buscarPedidos()`** com `ORDER BY dataHora DESC`: histórico do mais recente para
  o mais antigo.

---

## 4. Dificuldades na migração de `mutableStateListOf` para Room

**1. Chamadas ao banco não podem rodar na thread principal**
O Room não deixa acessar o banco na thread da interface. Por isso todas as chamadas
ao DAO rodam em `Dispatchers.IO`. Nas funções que retornam algo (como buscar por id),
usamos `withContext(Dispatchers.IO) { ... }`, que roda a consulta em segundo plano e
devolve o resultado para a tela. Nas que só gravam (atualizar, deletar), usamos
`escopo.launch(Dispatchers.IO) { ... }`.

**2. O id agora é gerado pelo banco**
No Trabalho 2 a gente controlava o próximo id à mão. Agora a loja nova é criada com
`id = 0`, o `dao.inserir()` devolve o id gerado e a lista da tela recebe a loja com
esse id: `lojas.add(loja.copy(id = novoId.toInt()))`. Sem isso, tocar na loja recém-criada
abria o detalhe errado.

**3. As telas de detalhe ficaram assíncronas**
Antes a loja era encontrada na hora. Agora a tela abre, dispara um `LaunchedEffect(id)`
que busca no banco, e só depois mostra os dados. Enquanto isso aparece "Carregando...",
e se não achar aparece "Loja não encontrada".

**4. Dados iniciais duplicando**
Inserir as lojas iniciais toda vez que o app abria duplicava os registros. A solução
foi só inserir quando a tabela de categorias estiver vazia (primeira execução).

**5. Bug do pedido entregue virando cancelado**
Ao fazer um pedido novo, o anterior era sempre marcado como cancelado, mesmo se já
tivesse sido entregue. Corrigimos para cancelar só se o status ainda for
"Em andamento".

---

## 5. Teste de persistência

1. Abrir a tela **Lojas** e adicionar uma loja nova.
2. Fechar o app completamente (tirar da lista de apps recentes).
3. Abrir o app de novo e ir em **Lojas**.
4. ✅ A loja continua lá.

O mesmo vale para remover uma loja, editar a nota, marcar como favorita e adicionar
ou editar categorias.


## Estrutura do código

```
app/src/main/java/com/example/interfaceifood/
├── MainActivity.kt            → abre o banco (DadosApp.iniciar) e chama AppNavigation()
├── model/Modelos.kt           → as 5 @Entity + funções de formatação (R$, taxa)
├── dados/
│   ├── AppDataBase.kt         → @Database com Singleton
│   ├── CategoriaDao.kt        → @Dao de categorias
│   ├── LojaDao.kt             → @Dao de lojas
│   ├── ProdutoDao.kt          → @Dao de produtos
│   ├── PedidoDao.kt           → @Dao de pedidos e itens
│   └── DadosApp.kt            → liga as telas ao banco (carregar, adicionar, remover, editar)
├── navegacao/
│   ├── Rotas.kt               → rotas do app
│   └── AppNavigation.kt       → Scaffold + NavHost + NavigationBar
├── componentes/Componentes.kt → CardLoja
├── telas/                     → uma tela por arquivo
└── ui/theme/Theme.kt          → cores do app (vermelho iFood)
```
