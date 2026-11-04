# Interface iFood — Trabalho 4 (Arquitetura MVVM)

App Android em **Kotlin + Jetpack Compose** inspirado no iFood. É a continuação do
Trabalho 3: as telas, os campos e as funcionalidades são os mesmos, mas o código foi
reorganizado em camadas com **MVVM** (Model — View — ViewModel), usando `Repository`,
`Flow`, `StateFlow`/`UiState` e `ViewModelFactory`.

**Integrantes:**
- Lucas Pereira Hartinger
- Luis Felipe de Paula Fogaça
- Felipe Paulista Silveira

A documentação do trabalho anterior continua em [docs/TRABALHO3.md](docs/TRABALHO3.md).

---

## Como rodar o projeto

1. Clone o repositório:
   ```
   git clone https://github.com/LucasHartinger19/InterfaceIfoodRoom.git
   ```
2. Abra a pasta no **Android Studio** e espere o **Gradle Sync** terminar.
3. Inicie um emulador (ou conecte um celular com depuração USB) com **Android 7.0 (API 24) ou superior**.
4. Clique em **Run ▶**.

**Versões usadas:** AGP 9.0.0 · Kotlin 2.2.21 · KSP 2.3.4 · Room 3.0.3 · Lifecycle 2.9.4 · compileSdk 37 · minSdk 24

---

## 1. Como estava o projeto no Trabalho 3 e o que mudou

No Trabalho 3 a persistência funcionava, mas quase tudo passava pelo objeto `DadosApp`,
com 314 linhas. Ele guardava as listas `mutableStateListOf`, chamava os DAOs, abria
`CoroutineScope`, escolhia `Dispatchers.IO` e ainda tinha as regras de negócio. As telas
acessavam esse objeto diretamente, e os campos de formulário ficavam em vários `remember`.

| Antes (Trabalho 3) | Agora (Trabalho 4) |
|---|---|
| Pastas `dados`, `model`, `telas`, `navegacao`, `componentes` | Pacotes `ui`, `data.local`, `data.repository`, `viewmodel` |
| `DadosApp` fazia tudo | `DadosApp` foi removido: cada responsabilidade foi para sua camada |
| Tela chamava `DadosApp`, que chamava o DAO | Tela → ViewModel → Repository → DAO |
| `suspend fun buscarLojas(): List<Loja>` | `fun buscarTodos(): Flow<List<Loja>>` |
| Depois de gravar, era preciso atualizar a lista à mão | O Room avisa o `Flow` e a tela atualiza sozinha |
| `LaunchedEffect { DadosApp.carregar() }` em cada lista | Removido: o ViewModel observa o `Flow` no `init` |
| Vários `remember { mutableStateOf(...) }` | Um `UiState` por tela |
| `CoroutineScope` e `Dispatchers.IO` manuais | `viewModelScope`, com as chamadas `suspend` do Room |

## 2. Como dividimos os pacotes

```
app/src/main/java/com/example/interfaceifood/
├── MainActivity.kt            → cria banco, Repositories e Factory
├── data/
│   ├── local/                 → Modelos (5 @Entity), 4 @Dao, AppDatabase, DadosIniciais
│   └── repository/            → Loja, Categoria, Produto, Pedido e DadosIniciaisRepository
├── viewmodel/                 → 7 ViewModels com seus UiState + AppViewModelFactory
└── ui/
    ├── telas/                 → uma tela por arquivo
    ├── componentes/           → CardLoja e funções de texto
    ├── navegacao/             → Rotas e AppNavigation
    └── theme/                 → cores do app
```

A regra que usamos para decidir o lugar de cada arquivo foi perguntar **o que ele faz**:

- **`ui`:** tudo que desenha ou navega. Inclui `navegacao` e `componentes`, porque são parte da interface.
- **`data.local`:** tudo que depende do Room. Os arquivos foram movidos sem mudar as tabelas, por isso o banco continua na versão 1.
- **`data.repository`:** a porta de entrada dos dados. Só essa camada fala com os DAOs.
- **`viewmodel`:** estado das telas e regras, como "a categoria com lojas não pode ser removida".

Os dados iniciais saíram do `DadosApp` e foram para `data/local/DadosIniciais.kt`.

## 3. Por que o Repository existe

Antes, se os dados viessem de outro lugar, teríamos que mexer no `DadosApp`, que também
tinha estado de tela e regras. Agora o `Repository` esconde de onde os dados vêm. Se um
dia as lojas vierem de uma API, só o `LojaRepository` muda: ViewModel e tela continuam
iguais.

Criamos um Repository por Entity principal, e cada um só repassa as chamadas para o DAO:

```kotlin
class LojaRepository(private val lojaDao: LojaDAO) {
    fun buscarTodos(): Flow<List<Loja>> = lojaDao.buscarTodos()
    suspend fun inserir(loja: Loja): Long = lojaDao.inserir(loja)
    suspend fun atualizar(loja: Loja) = lojaDao.atualizar(loja)
    suspend fun deletar(loja: Loja) = lojaDao.deletar(loja)
}
```

`ItemPedido` não tem Repository próprio porque só existe dentro de um pedido, então
fica no `PedidoRepository`. O `DadosIniciaisRepository` só popula o banco na primeira
execução.

## 4. O que tem dentro de cada UiState

Todo ViewModel segue o mesmo padrão: o `_uiState` é privado e só ele altera; a tela lê o
`uiState` público e chama os métodos do ViewModel.

```kotlin
private val _uiState = MutableStateFlow(LojasUiState())
val uiState: StateFlow<LojasUiState> = _uiState.asStateFlow()
```

| UiState | Campos | Por que esses campos |
|---|---|---|
| `InicioUiState` | categorias, lojas | É só o que a tela inicial lista. |
| `BuscaUiState` | textoBusca, filtroSelecionado, filtros, lojas, categorias | O texto e o filtro definem o resultado, calculado em `resultados`. |
| `LojasUiState` | lojas, categorias, nome, avaliacao, tempo, taxa, categoriaId | A lista e todos os campos do formulário de nova loja. |
| `CategoriasUiState` | categorias, lojas, nome, descricao | As lojas servem para contar quantas existem em cada categoria. |
| `DetalheLojaUiState` | loja, categorias, carregando, novaNota, valorPedido | `carregando` separa "Carregando..." de "Loja não encontrada". `total` é calculado. |
| `DetalheCategoriaUiState` | categoria, categorias, lojas, carregando, nomeEditado, descricaoEditada | Dados da categoria, formulário de edição e lojas dela. |
| `PedidoUiState` | pedido, loja, etapas, carregando | O pedido mostra etapa e número; a loja mostra nome e previsão. |

Criamos um ViewModel por tela porque cada tela tem um estado diferente. A
`AppViewModelFactory` é uma só e foi reaproveitada, já que todos usam os mesmos
Repositories. O método `comId(id)` passa o id da rota para as telas de detalhe:

```kotlin
viewModel: DetalheLojaViewModel = viewModel(factory = factory.comId(id))
```

## 5. Alguma funcionalidade quebrou?

Encontramos os problemas repassando cada funcionalidade do Trabalho 3, tela por tela.

**1. O formulário de editar categoria podia perder o texto digitado**
Como o `buscarPorId` agora é um `Flow`, cada mudança no banco manda a categoria de novo.
Se isso sobrescrevesse `nomeEditado` e `descricaoEditada`, o texto seria perdido. Agora o
formulário só é preenchido na primeira leitura, enquanto `carregando` é `true`.

**2. Os dados iniciais podiam voltar sem querer**
Colocar a verificação de banco vazio no `InicioViewModel` não servia: ele é recriado a
cada volta para a tela Início. Quem apagasse todas as categorias veria os dados iniciais
voltarem com o app aberto. A chamada foi para a `MainActivity`, que roda ao abrir o app.

**3. O pedido entregue sumia da tela de acompanhamento**
Observar só pedidos "Em andamento" fazia o pedido desaparecer na última etapa, e o aviso
"O pedido já foi entregue!" nunca aparecia. Agora o `PedidoViewModel` usa o pedido mais
recente, e a tela só fica vazia se ele tiver sido cancelado.

**4. Os nomes de categoria dentro do `CardLoja`**
O `CardLoja` buscava o nome no `DadosApp`. Sem esse objeto, ele passou a receber
`nomeCategoria` como parâmetro, e cada tela informa o nome a partir do seu `UiState`.

### Diferenças de comportamento

As telas e funcionalidades são as mesmas. Duas diferenças internas ficaram:

- **Pedido entregue:** continua aparecendo depois de fechar e abrir o app. No Trabalho 3,
  a tela voltava para "nenhum pedido" nesse caso.
- **Erro de banco:** no Trabalho 3, o erro ia só para o log. Agora os blocos do
  `viewModelScope` não têm `try/catch`, para os métodos ficarem mais simples.

Também removemos do `DadosApp` o que nenhuma tela usava, como `historicoPedidos` e `criarItem`.

## 6. Dificuldades do trio

No começo tivemos dificuldade para entender por que separar tanto o código, já que o
app funcionava. Colocar uma classe entre o ViewModel e o DAO só para repassar chamadas
parecia trabalho repetido. Também nos confundimos com as duas variáveis do mesmo estado
(`_uiState` e `uiState`), e tentamos alterar o estado direto pela tela.

O que resolveu foi fazer primeiro só o caminho de **Lojas**, do DAO até a tela, e seguir
uma ação do começo ao fim:

1. O usuário toca em "Adicionar loja" e a tela chama `viewModel.adicionarLoja()`.
2. O ViewModel chama `lojaRepository.inserir(...)`.
3. O Repository chama o DAO, e o Room grava.
4. O `Flow` avisa o ViewModel, que atualiza o `_uiState`.
5. A tela recebe o novo `uiState` e redesenha.

Com esse fluxo, ficou claro que cada camada tem uma função só, e que o `_uiState` privado
garante que o estado só mude pelo ViewModel. As outras telas seguiram o mesmo modelo.

## 7. Prints e vídeos

Arquivos na pasta [docs/prints](docs/prints):

| Arquivo | O que mostra |
|---|---|
| `01-pacotes.png` | Pacotes `ui`, `data`, `data.repository` e `viewmodel` no Android Studio |
| `02-adicionar-loja.mp4` | Adicionar uma loja e ver a lista atualizar sozinha |
| `03-editar-remover.mp4` | Editar nota, marcar favorita, editar categoria e remover loja |
| `04-persistencia.mp4` | Fechar o app, abrir de novo e ver os dados salvos |
| `05-pedido.mp4` | Fazer pedido, atualizar status, entregar e cancelar |
