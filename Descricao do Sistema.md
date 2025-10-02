### Visão geral do sistema
O sistema “Gestão de Equipamentos” é uma aplicação desktop (Swing/Java) para controle de catálogo de equipamentos de informática (computadores e periféricos), cadastros de clientes e usuários (administrador, gestor e vendedor), registro de vendas com itens e quantidades, controle de estoque, e geração de relatórios (com gráficos e exportação em PDF e CSV). Não utiliza banco de dados; toda persistência é feita em arquivos na pasta `data` do projeto.

### Principais funcionalidades
- Catálogo de produtos (estoque):
  - Cadastro de `Equipamento` (classe abstrata), com subclasses `Computador` e `Periferico`.
  - Campos típicos: `id`, `marca`, `preco`, `quantidadeEstoque`, `estado`, `fotoPath`, e atributos específicos (ex.: processador, RAM, armazenamento, GPU, tipo de periférico).
  - Controle de estoque: baixa automática no fechamento de venda; regravação persistente em `data\equipamentos.json`.
- Cadastros de pessoas/usuários:
  - `Cliente`: `id`, `nome`, `nrBI`, `telefone`, `email`, `endereco`.
  - Usuários por papel: `Administrador`, `Gestor`, `Vendedor`. Senhas armazenadas com hash (ex.: `BCryptHasher`).
- Vendas:
  - Modelo com `Venda` e `ItemVenda` (quantidade por item, preço unitário “congelado” no momento da venda, cálculos com `BigDecimal`).
  - Desconto e imposto como valores absolutos na `Venda` (`BigDecimal`).
  - Validação de dados e cálculo de total líquido: `getTotalComDescontosImpostos()`.
  - Persistência de histórico de vendas em arquivo NDJSON `data\vendas.ndjson` (1 venda por linha) usando `VendaDTO` e `ItemVendaDTO`.
- Relatórios:
  - Filtros: todas as vendas, por período (datas), por vendedor, por cliente.
  - Estatísticas: total de vendas, faturamento total, ticket médio, melhor vendedor e melhor cliente.
  - Gráficos (JFreeChart): total por dia (barras), faturamento por vendedor (barras), top clientes (pizza), evolução de vendas por dia (linha).
  - Exportação: CSV (por período) e PDF (iText) com tí
  - tulo, período, estatísticas, tabela e imagens dos gráficos.
- Permissões e navegação por menu conforme papel (Administrador, Gestor, Vendedor).

### Arquitetura e camadas
- Camada de apresentação (Swing):
  - `RegistrarVendaView`: fluxo de venda com catálogo, seleção de quantidade, carrinho com `ItemVenda`, campos de desconto/imposto, validações, e fechamento chamando o controller.
  - `RelatoriosVendasView`: filtros, tabela, estatísticas, geração de gráficos (JFreeChart) e exportação para PDF/CSV.
  - Demais views de menu e cadastro (conforme seu projeto).
- Camada de controle:
  - `SistemaController`: ponto central entre as views e a persistência. Fornece listas para UI (`getEquipamentos()`, `getClientes()`, `getVendedores()`), autenticação/permits, registro de vendas (`registrarVenda`), e delega relatórios ao serviço.
- Serviços:
  - `RelatorioVendasService`: computa agregações a partir do NDJSON (`totalPorDia`, `itensMaisVendidos`, exportação CSV por período via repositório de vendas).
- Persistência (arquivos):
  - Repositório genérico de listas: `BaseListRepository<T>` com `findAll`, `findById`, `add`, `upsert`, `removeById`, `replaceAll`, cache em memória e índice por `id`. Escrita atômica (`writeAtomic`) para `.json`.
  - Repositórios de cadastros (JSON):
    - `EquipamentoRepository` → `data\equipamentos.json`
    - `ClienteRepository` → `data\clientes.json`
    - `VendedorRepository` → `data\vendedores.json`
    - `GestorRepository` → `data\gestores.json`
    - `AdministradorRepository` → `data\administradores.json`
  - Repositório de vendas (NDJSON): `VendaFileRepository` → `data\vendas.ndjson` (append-only, 1 linha por venda, escrita compacta sem pretty printing, leitura tolerante a BOM/linhas inválidas).
  - DTOs e mapeamento: `VendaDTO` e `ItemVendaDTO` (IDs de `vendedor`, `cliente`, `equipamento`, quantidade e preço unitário congelado). `VendaMapper` converte do domínio para DTO.
  - Serialização polimórfica de `Equipamento`: `RuntimeTypeAdapterFactory` local (`persistence.adapters.RuntimeTypeAdapterFactory`) registrado em `JsonUtil` permite gravar e ler `Computador`/`Periferico` mantendo o tipo pelo campo de discriminação (ex.: `type`).

### Modelo de domínio (resumo)
- `Equipamento` (abstrata): `id`, `marca`, `preco`, `quantidadeEstoque`, `estado`, `fotoPath`, …
- `Computador` extends `Equipamento`: `processador`, `memoriaRAM`, `armazenamento`, `gpu`.
- `Periferico` extends `Equipamento`: `tipo`.
- `Cliente`: `id`, `nome`, `nrBI`, `telefone`, `email`, `endereco`.
- `Vendedor`, `Gestor`, `Administrador`: `id`, `nome`, documentos/contatos, `senha` (hash), e enum/indicador de tipo para autorização.
- `ItemVenda`: `equipamento`, `quantidade`, `precoUnitario` (`BigDecimal`), `getSubtotal()`.
- `Venda`: `idVenda`, `data`, `vendedor`, `cliente`, `List<ItemVenda>`, `valorTotal` (`BigDecimal`), `desconto`, `imposto`, `getTotalComDescontosImpostos()`, `validarDados()`.

### Fluxos principais
- Registro de venda (UI):
  1) Selecionar cliente (corporativo ou balcão com validação e cadastro rápido).
  2) Selecionar equipamento no catálogo, informar quantidade → adiciona ao carrinho (`ItemVenda`), recalcula totais em `BigDecimal`.
  3) Opcional: informar `desconto`/`imposto` (valores absolutos).
  4) Finalizar: controller chama `registrarVenda(venda)`; `Venda.adicionarItem` reduz estoque; `VendaFileRepository.salvar` escreve a venda (NDJSON) e `EquipamentoRepository.upsert` persiste o novo estoque.
  5) UI limpa carrinho e recarrega catálogo (estoque atualizado).
- Relatórios:
  1) Carrega vendas de `vendas.ndjson` via `controller.getVendasDTO()`.
  2) Aplica filtro conforme tipo (“Todas”, “Por Período”, “Por Vendedor”, “Por Cliente”).
  3) Atualiza tabela e estatísticas (total, faturamento, ticket médio, melhor vendedor/cliente).
  4) Gera gráficos (JFreeChart) em grade 2x2 com painéis compactos.
  5) Exporta PDF (iText) contendo: título/período, estatísticas, tabela e gráficos como imagens; exporta CSV por período via controller/serviço.
- Autenticação e autorização:
  - Login por `id` e `senha` (hash) nos repositórios de `Administrador`, `Gestor`, `Vendedor`.
  - Menus e permissões nas views conforme o papel (ex.: vendedor não acessa relatórios administrativos).

### Persistência em arquivos (detalhes)
- Pasta de dados: `data\` (criada no primeiro uso).
- Cadastros em `.json` (listas) com pretty printing e escrita atômica para evitar corrupção.
- Vendas em `.ndjson` (um objeto JSON por linha), sem pretty printing para manter cada venda em linha única (compatível com leitura streaming).
- Leitura resiliente do NDJSON: ignora linhas vazias, BOM, e quaisquer linhas fora do padrão `{ ... }` completo.
- Exportações CSV são salvas em `data\exports\`.

### Relatórios e gráficos
- Tabela: `ID`, `Data`, `Vendedor`, `Cliente`, `Qtd Itens`, `Total`.
- Estatísticas calculadas sobre o conjunto filtrado:
  - `totalVendas` = número de vendas listadas
  - `faturamentoTotal` = soma de `dto.total` (`BigDecimal`)
  - `ticketMedio` = `faturamentoTotal / totalVendas` (2 casas, HALF_UP)
  - `melhorVendedor`/`melhorCliente` = maior faturamento agregado por nome
- Gráficos (JFreeChart):
  - Barras: total por dia; faturamento por vendedor.
  - Pizza: top clientes por faturamento (Top N).
  - Linha: evolução da quantidade de vendas por dia.
- Exportação PDF (iText): documento A4 paisagem com título, período, estatísticas (tabela curta), tabela de vendas e imagens dos 4 gráficos.

### Validações e usabilidade
- `RegistrarVendaView` usa validações de campos (ex.: `Validador.validarBI`, `validarTelefone`) para clientes de balcão.
- Quantidade do item respeita o estoque disponível; soma incremental no carrinho não pode ultrapassar o estoque.
- Totais monetários sempre com `BigDecimal` para evitar erros de ponto flutuante.
- Desconto/imposto: valores absolutos; total líquido não fica negativo (`max(BigDecimal.ZERO)`).
- Mensagens amigáveis e atualização de rótulos de total em tempo real.

### Segurança
- Senhas guardadas com hash (ex.: `BCryptHasher`).
- Checagem de permissões tanto na UI (habilitar/desabilitar menus) quanto no controller (defesa em profundidade).
- Escrita sincronizada do NDJSON para evitar concorrência entre gravações.

### Desempenho e escalabilidade
- Cadastros (JSON) carregados em memória com índice por `id` para buscas rápidas.
- Vendas em NDJSON permitem apêndice O(1) por venda e leitura streaming para relatórios.
- Para volumes maiores, os relatórios agregam em memória após filtro (escala bem para milhares de vendas). Caso atinja ordens de grandeza maiores, pode-se evoluir para indexação por data ou sumarização incremental.

### Empacotamento e dependências
- Projeto sem Maven/Gradle: dependências adicionadas via `.jar` no classpath.
  - `gson-2.11.0.jar` (serialização JSON)
  - `jfreechart-1.5.x.jar` e `jcommon-1.0.24.jar` (gráficos)
  - `itextpdf-5.5.13.3.jar` (PDF)
  - Outras bibliotecas de UI (ex.: FlatLaf demo) e utilitários conforme necessário
- Build pelo IntelliJ; pode gerar “fat jar” (artifact) contendo dependências.

### Estrutura de pastas (essencial)
- `src\view\` → telas Swing (ex.: `RegistrarVendaView`, `RelatoriosVendasView`, menus)
- `src\controller\` → `SistemaController`
- `src\model\abstractas` e `src\model\concretas` → domínio
- `src\persistence\` → repositórios, `JsonUtil`, `VendaFileRepository`
- `src\persistence\dto` → `VendaDTO`, `ItemVendaDTO`
- `src\persistence\adapters` → `RuntimeTypeAdapterFactory`
- `src\service\` → `RelatorioVendasService`
- `data\` → arquivos `.json` e `.ndjson`; `data\exports\` para CSV; (opcional) `data\logs\` para auditoria

### Requisitos não funcionais atendidos
- Confiabilidade: escrita atômica em `.json`; leitura resiliente do NDJSON.
- Integridade: baixa de estoque na regra de negócio (`Venda.adicionarItem`), persistência imediata no sucesso da venda.
- Precisão monetária: `BigDecimal` em totais e subtotais.
- Usabilidade: feedback visual, gráficos, exportações e filtros claros.
- Portabilidade: Java puro (Swing), arquivos no sistema de arquivos local.

### Testes e validação (checklist)
- Cadastro: criar/editar/remover clientes, usuários e equipamentos → verificar persistência em `*.json` e recarga após reinício.
- Vendas: registrar vendas com múltiplos itens e descontos → verificar NDJSON e ajuste de estoque.
- Relatórios: aplicar filtros por período, vendedor e cliente; confirmar estatísticas, gráficos e exportação PDF/CSV.
- Permissões: vendedor sem acesso a relatórios administrativos; gestor/administrador com acesso completo.

### Roadmap (evoluções futuras sugeridas)
- Autenticação por `login` em repositório unificado de usuários (`UsuarioRepository`) com `findByLogin` e auditoria.
- Estorno/cancelamento de vendas como “eventos” no NDJSON (linhas adicionais marcando reversões).
- Snapshot de nomes (cliente/vendedor) no `VendaDTO` para relatórios imutáveis mesmo após renomear cadastros.
- Internacionalização (formatação moeda/data por locale) e temas (LAF).
- Importação/exportação de estoque e cadastros (CSV/Excel) com validações.

### Conclusão
O sistema integra um fluxo completo de vendas e gestão de estoque com persistência em arquivos, mantendo precisão monetária, relatórios ricos (com gráficos e PDF), e uma arquitetura modular (views Swing, controller central, serviços de relatório e repositórios resilientes). Essa base é sólida para operação local e pode evoluir gradualmente para necessidades de maior escala ou integração externa sem reescrever a lógica principal.