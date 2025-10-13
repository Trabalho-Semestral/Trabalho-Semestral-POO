package view;

import controller.SistemaController;
import model.abstractas.Equipamento;
import model.concretas.*;
import util.GeradorID;
import util.UITheme;
import util.Validador;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class RegistrarVendaView extends JPanel {

    private SistemaController controller;
    private Vendedor vendedorLogado;


    // --- COMPONENTES DO CLIENTE ---
    private JRadioButton rbClienteCorporativo, rbClienteBalcao;
    private CardLayout cardLayoutCliente;
    private JPanel painelCamposCliente;
    private JComboBox<Cliente> cmbCliente;
    private JTextArea txtAreaDetalhesCliente;
    private JTextField txtNomeClienteBalcao, txtBiClienteBalcao, txtTelefoneClienteBalcao;
    private JTable tabelaEquipamentosDisponiveis;
    private DefaultTableModel modeloTabelaEquipamentos;
    private JLabel lblFotoEquipamento;
    private JTextArea txtAreaDetalhesEquipamento;
    private JTextField txtQuantidade;
    private JButton btnAdicionarItem;
    private JTextField txtDesconto;
    private JTextField txtImposto;
    // --- COMPONENTES DA VENDA ATUAL (CARRINHO) ---
    private JTable tabelaItensVenda;
    private DefaultTableModel modeloTabelaItens;
    private JLabel lblTotalVenda;
    private boolean modoReserva;


    // --- BOTÕES DE AÇÃO ---
    private JButton btnRemoverItem, btnFinalizarVenda, btnLimparVenda, btnVoltar;

    // --- DADOS ---
    private List<ItemVenda> itensVenda = new ArrayList<>();
    private BigDecimal totalVenda = BigDecimal.ZERO;

    public RegistrarVendaView(SistemaController controller, Vendedor vendedorLogado) {
        this.controller = controller;
        this.vendedorLogado = vendedorLogado;
        this.modoReserva = false;
        initComponents();
        setupLayout();
        setupEvents();
        carregarDadosIniciais();
    }
    public static RegistrarVendaView criarParaReserva(SistemaController controller) {
        Object usuarioLogado = controller.getUsuarioLogado();
        if (!(usuarioLogado instanceof Vendedor)) {
            throw new IllegalStateException("Apenas vendedores podem criar reservas");
        }
        Vendedor vendedor = (Vendedor) usuarioLogado;
        RegistrarVendaView view = new RegistrarVendaView(controller, vendedor);
        view.modoReserva = true;
        view.btnFinalizarVenda.setText("Salvar Reserva");
        return view;
    }

    public static RegistrarVendaView criarParaVenda(SistemaController controller) {

        Object usuarioLogado = controller.getUsuarioLogado();
        if (!(usuarioLogado instanceof Vendedor)) {
            throw new IllegalStateException("Apenas vendedores podem registrar vendas");
        }

        Vendedor vendedor = (Vendedor) usuarioLogado;
        RegistrarVendaView view = new RegistrarVendaView(controller, vendedor);
        view.modoReserva = false;
        view.btnFinalizarVenda.setText("✅ Finalizar Venda");
        return view;
    }

    private void initComponents() {
        setBackground(UITheme.BACKGROUND_COLOR);

        // --- INICIALIZAÇÃO DOS COMPONENTES DO CLIENTE ---
        rbClienteCorporativo = new JRadioButton("Cliente Corporativo", true);
        rbClienteBalcao = new JRadioButton("Cliente de Balcão");
        UITheme.styleRadioButton(rbClienteCorporativo);
        UITheme.styleRadioButton(rbClienteBalcao);
        ButtonGroup bgTipoCliente = new ButtonGroup();
        bgTipoCliente.add(rbClienteCorporativo); bgTipoCliente.add(rbClienteBalcao);

        cardLayoutCliente = new CardLayout();
        painelCamposCliente = new JPanel(cardLayoutCliente);
        painelCamposCliente.setOpaque(false);

        // --- Card Cliente Corporativo ---
        JPanel painelCorp = new JPanel(new BorderLayout(5, 5));
        painelCorp.setOpaque(false);
        cmbCliente = new JComboBox<>();
        txtAreaDetalhesCliente = new JTextArea("Selecione um cliente para ver os detalhes.");
        txtAreaDetalhesCliente.setEditable(false);
        txtAreaDetalhesCliente.setFont(UITheme.FONT_BODY);
        txtAreaDetalhesCliente.setOpaque(false);
        txtAreaDetalhesCliente.setWrapStyleWord(true);
        txtAreaDetalhesCliente.setLineWrap(true);
        JScrollPane scrollDetalhesCliente = new JScrollPane(txtAreaDetalhesCliente);
        scrollDetalhesCliente.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        scrollDetalhesCliente.setOpaque(false);
        scrollDetalhesCliente.getViewport().setOpaque(false);
        painelCorp.add(cmbCliente, BorderLayout.NORTH);
        painelCorp.add(scrollDetalhesCliente, BorderLayout.CENTER);
        painelCamposCliente.add(painelCorp, "CORPORATIVO");

        // --- Card Cliente de Balcão ---
        JPanel painelBalcao = new JPanel(new GridLayout(0, 1, 5, 5));
        painelBalcao.setOpaque(false);
        txtNomeClienteBalcao = UITheme.createStyledTextField();
        txtBiClienteBalcao = UITheme.createStyledTextField();
        txtTelefoneClienteBalcao = UITheme.createStyledTextField();
        UITheme.setFieldAsInput(txtNomeClienteBalcao, "Nome do Cliente de Balcão");
        UITheme.setFieldAsInput(txtBiClienteBalcao, "Nº BI (Obrigatório)");
        UITheme.setFieldAsInput(txtTelefoneClienteBalcao, "Telefone (Obrigatório)");
        painelBalcao.add(txtNomeClienteBalcao);
        painelBalcao.add(txtBiClienteBalcao);
        painelBalcao.add(txtTelefoneClienteBalcao);
        painelCamposCliente.add(painelBalcao, "BALCAO");

        // --- INICIALIZAÇÃO DOS COMPONENTES DE PRODUTO ---
        modeloTabelaEquipamentos = new DefaultTableModel(new String[]{"Marca", "Tipo", "Preço", "Estoque"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaEquipamentosDisponiveis = new JTable(modeloTabelaEquipamentos);


        lblFotoEquipamento = new JLabel("Selecione um produto", SwingConstants.CENTER);
        lblFotoEquipamento.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));
        lblFotoEquipamento.setPreferredSize(new Dimension(150, 150));

        txtAreaDetalhesEquipamento = new JTextArea("Detalhes do produto...");
        txtAreaDetalhesEquipamento.setEditable(false);
        txtAreaDetalhesEquipamento.setFont(UITheme.FONT_BODY);
        txtAreaDetalhesEquipamento.setWrapStyleWord(true);
        txtAreaDetalhesEquipamento.setLineWrap(true);
        txtAreaDetalhesEquipamento.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        txtQuantidade = UITheme.createStyledTextField();
        txtQuantidade.setText("1");
        txtQuantidade.setPreferredSize(new Dimension(60, UITheme.INPUT_SIZE.height));
        btnAdicionarItem = UITheme.createPrimaryButton("➕ Adicionar");

        // --- INICIALIZAÇÃO DO CARRINHO E AÇÕES ---
        modeloTabelaItens = new DefaultTableModel(new String[]{"Qtd", "Produto", "Preço Unit.", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaItensVenda = new JTable(modeloTabelaItens);
        // UITheme.applyTableStyle(tabelaItensVenda);

        lblTotalVenda = UITheme.createTitleLabel("TOTAL: 0,00 MT");
        lblTotalVenda.setForeground(UITheme.SUCCESS_COLOR);
        txtDesconto = UITheme.createStyledTextField();
        txtDesconto.setPreferredSize(new Dimension(100, UITheme.INPUT_SIZE.height));
        UITheme.setFieldAsInput(txtDesconto, "Desconto (MT)");

        txtImposto = UITheme.createStyledTextField();
        txtImposto.setPreferredSize(new Dimension(100, UITheme.INPUT_SIZE.height));
        UITheme.setFieldAsInput(txtImposto, "Imposto (MT)");

        btnRemoverItem = UITheme.createDangerButton("Remover Item");
        btnFinalizarVenda = UITheme.createSuccessButton("✅ Finalizar Venda");
        btnFinalizarVenda.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnLimparVenda = UITheme.createSecondaryButton("Limpar Tudo");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnVoltar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
    }

    private void setupLayout() {

        setLayout(new BorderLayout());
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        JLabel lblTitulo = UITheme.createHeadingLabel("🛒 Registrar Nova Venda");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Sengoe UI Emoji", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar);
        topBar.add(voltarPanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setResizeWeight(0.6);
        mainSplit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainSplit.setLeftComponent(createLeftPanel());
        mainSplit.setRightComponent(createRightPanel());
        add(mainSplit, BorderLayout.CENTER);

        if (txtDesconto != null) {
            txtDesconto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { atualizarCarrinhoETotal(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { atualizarCarrinhoETotal(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { atualizarCarrinhoETotal(); }
            });
        }
        if (txtImposto != null) {
            txtImposto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { atualizarCarrinhoETotal(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { atualizarCarrinhoETotal(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { atualizarCarrinhoETotal(); }
            });
        }
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);

        JPanel painelCliente = UITheme.createCardPanel();
        painelCliente.setLayout(new BorderLayout(10, 10));
        painelCliente.setBorder(BorderFactory.createTitledBorder("1. Identificação do Cliente"));
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setOpaque(false);
        radioPanel.add(rbClienteCorporativo);
        radioPanel.add(rbClienteBalcao);
        painelCliente.add(radioPanel, BorderLayout.NORTH);
        painelCliente.add(painelCamposCliente, BorderLayout.CENTER);

        JPanel painelCarrinho = UITheme.createCardPanel();
        painelCarrinho.setLayout(new BorderLayout(10, 10));
        painelCarrinho.setBorder(BorderFactory.createTitledBorder("3. Itens da Venda (Carrinho)"));
        painelCarrinho.add(new JScrollPane(tabelaItensVenda), BorderLayout.CENTER);
        JPanel totaisPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        totaisPanel.setOpaque(false);
        totaisPanel.add(new JLabel("Desconto:"));
        totaisPanel.add(txtDesconto);
        totaisPanel.add(new JLabel("Imposto:"));
        totaisPanel.add(txtImposto);


        JPanel acoesCarrinhoPanel = new JPanel(new BorderLayout(10,10));
        acoesCarrinhoPanel.setOpaque(false);
        acoesCarrinhoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        acoesCarrinhoPanel.add(lblTotalVenda, BorderLayout.WEST);
        acoesCarrinhoPanel.add(totaisPanel, BorderLayout.CENTER);
        JPanel botoesCarrinho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        botoesCarrinho.setOpaque(false);
        botoesCarrinho.add(btnRemoverItem);
        botoesCarrinho.add(btnLimparVenda);
        botoesCarrinho.add(btnFinalizarVenda);
        acoesCarrinhoPanel.add(botoesCarrinho, BorderLayout.EAST);
        painelCarrinho.add(acoesCarrinhoPanel, BorderLayout.SOUTH);

        leftPanel.add(painelCliente, BorderLayout.NORTH);
        leftPanel.add(painelCarrinho, BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = UITheme.createCardPanel();
        rightPanel.setLayout(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("2. Catálogo de Produtos"));

        JPanel detalhesPanel = new JPanel(new BorderLayout(10, 10));
        detalhesPanel.setOpaque(false);
        detalhesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        detalhesPanel.add(lblFotoEquipamento, BorderLayout.WEST);
        detalhesPanel.add(new JScrollPane(txtAreaDetalhesEquipamento), BorderLayout.CENTER);

        JPanel acoesProdutoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        acoesProdutoPanel.setOpaque(false);
        acoesProdutoPanel.add(new JLabel("Qtd:"));
        acoesProdutoPanel.add(txtQuantidade);
        acoesProdutoPanel.add(btnAdicionarItem);
        detalhesPanel.add(acoesProdutoPanel, BorderLayout.SOUTH);

        rightPanel.add(new JScrollPane(tabelaEquipamentosDisponiveis), BorderLayout.CENTER);
        rightPanel.add(detalhesPanel, BorderLayout.SOUTH);
        return rightPanel;
    }

    private void setupEvents() {
        rbClienteCorporativo.addActionListener(e -> toggleClienteFields(false));
        rbClienteBalcao.addActionListener(e -> toggleClienteFields(true));
        cmbCliente.addActionListener(e -> exibirDetalhesCliente());
        tabelaEquipamentosDisponiveis.getSelectionModel().addListSelectionListener(this::exibirDetalhesEquipamento);
        btnAdicionarItem.addActionListener(e -> adicionarItemAoCarrinho());
        btnRemoverItem.addActionListener(e -> removerItemDoCarrinho());
        btnLimparVenda.addActionListener(e -> limparVenda());
        btnFinalizarVenda.addActionListener(e -> {
            if (modoReserva) {
                finalizarReserva();
            } else {
                finalizarVenda();
            }
        });
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());

    }
    private void carregarDadosIniciais() {
        cmbCliente.removeAllItems();
        controller.getClientes().forEach(cmbCliente::addItem);

        modeloTabelaEquipamentos.setRowCount(0);
        for(Equipamento eq : controller.getEquipamentos()) {
            if (eq != null && eq.getId() != null) {
                int disponivelReal = eq.getQuantidadeEstoque() - eq.getReservado();
                if (disponivelReal > 0) {
                    modeloTabelaEquipamentos.addRow(new Object[]{
                            eq.getMarca(),
                            eq instanceof Computador ? "Computador" : "Periférico",
                            String.format("%.2f MT", eq.getPreco()),
                            disponivelReal
                    });
                }
            }
        }

        toggleClienteFields(false);
    }
    private void toggleClienteFields(boolean isBalcao) {
        if (isBalcao) {
            cardLayoutCliente.show(painelCamposCliente, "BALCAO");
            limparCamposClienteBalcao();
        } else {
            cardLayoutCliente.show(painelCamposCliente, "CORPORATIVO");
            exibirDetalhesCliente();
        }
    }

    private void limparCamposClienteBalcao() {
        txtNomeClienteBalcao.setText("");
        txtBiClienteBalcao.setText("");
        txtTelefoneClienteBalcao.setText("");
    }

    private void exibirDetalhesCliente() {
        if(rbClienteCorporativo.isSelected()) {
            Cliente c = (Cliente) cmbCliente.getSelectedItem();
            if (c != null) {
                StringBuilder detalhes = new StringBuilder();
                detalhes.append("Nome:\t").append(c.getNome()).append("\n");
                detalhes.append("Nº BI/NIF:\t").append(c.getNrBI()).append("\n");
                detalhes.append("Telefone:\t").append(c.getTelefone()).append("\n");
                if (c.getEmail() != null && !c.getEmail().isEmpty()) {
                    detalhes.append("Email:\t").append(c.getEmail()).append("\n");
                }
                if (c.getEndereco() != null && !c.getEndereco().isEmpty()) {
                    detalhes.append("Endereço:\t").append(c.getEndereco());
                }
                txtAreaDetalhesCliente.setText(detalhes.toString());
            } else {
                txtAreaDetalhesCliente.setText("Nenhum cliente corporativo selecionado.");
            }
        }
    }

    private void exibirDetalhesEquipamento(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && tabelaEquipamentosDisponiveis.getSelectedRow() != -1) {
            int selectedRow = tabelaEquipamentosDisponiveis.convertRowIndexToModel(tabelaEquipamentosDisponiveis.getSelectedRow());
            Equipamento eq = controller.getEquipamentos().stream().filter(equip -> equip.getQuantidadeEstoque() > 0).toList().get(selectedRow);

            if (eq.getFotoPath() != null && new File(eq.getFotoPath()).exists()) {
                ImageIcon icon = new ImageIcon(eq.getFotoPath());
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                lblFotoEquipamento.setIcon(new ImageIcon(img));
                lblFotoEquipamento.setText("");
            } else {
                lblFotoEquipamento.setIcon(null);
                lblFotoEquipamento.setText("Sem Foto");
            }

            StringBuilder detalhes = new StringBuilder();
            detalhes.append("Marca: ").append(eq.getMarca()).append("\n");
            detalhes.append("Estado: ").append(eq.getEstado()).append("\n");
            detalhes.append("Preço: ").append(String.format("%.2f MT", eq.getPreco())).append("\n");
            if(eq instanceof Computador comp) {
                detalhes.append("Processador: ").append(comp.getProcessador()).append("\n");
                detalhes.append("RAM: ").append(comp.getMemoriaRAM()).append("\n");
                detalhes.append("Armazenamento: ").append(comp.getArmazenamento());
            } else if (eq instanceof Periferico per) {
                detalhes.append("Tipo: ").append(per.getTipo());
            }
            txtAreaDetalhesEquipamento.setText(detalhes.toString());
        }
    }
    private void adicionarItemAoCarrinho() {
        int selectedRow = tabelaEquipamentosDisponiveis.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto do catálogo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tabelaEquipamentosDisponiveis.convertRowIndexToModel(selectedRow);
        Equipamento eq = controller.getEquipamentos().stream().filter(equip -> equip.getQuantidadeEstoque() > 0).toList().get(modelRow);

        try {
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            if (quantidade <= 0) throw new NumberFormatException();

            if (quantidade > eq.getQuantidadeEstoque()) {
                JOptionPane.showMessageDialog(this, "Quantidade em estoque insuficiente.", "Estoque", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (ItemVenda item : itensVenda) {
                if (item.getEquipamento().getId().equals(eq.getId())) {
                    int novaQtd = item.getQuantidade() + quantidade;
                    if (novaQtd > eq.getQuantidadeEstoque()) {
                        JOptionPane.showMessageDialog(this, "Quantidade total excede o estoque.", "Estoque", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    item.setQuantidade(novaQtd);
                    atualizarCarrinhoETotal();
                    return;
                }
            }

            itensVenda.add(new ItemVenda(eq, quantidade));
            atualizarCarrinhoETotal();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser um número inteiro positivo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void removerItemDoCarrinho() {
        int selectedRow = tabelaItensVenda.getSelectedRow();
        if (selectedRow >= 0) {
            itensVenda.remove(selectedRow);
            atualizarCarrinhoETotal();
        }
    }

    private Cliente getClienteDaVenda() {
        if(rbClienteCorporativo.isSelected()) {
            Cliente c = (Cliente) cmbCliente.getSelectedItem();
            if (c == null) JOptionPane.showMessageDialog(this, "Selecione um cliente corporativo.", "Erro", JOptionPane.ERROR_MESSAGE);
            return c;
        } else {
            try {
                String nome = txtNomeClienteBalcao.getText().trim();
                String bi = txtBiClienteBalcao.getText().trim();
                String telefone = txtTelefoneClienteBalcao.getText().trim();

                if (!Validador.validarCampoObrigatorio(nome)) throw new IllegalArgumentException("O Nome do cliente é obrigatório.");
                if (!Validador.validarBI(bi)) throw new IllegalArgumentException("O Nº de BI do cliente é inválido.");
                if (!Validador.validarTelefone(telefone)) throw new IllegalArgumentException("O Telefone do cliente é inválido.");

                Cliente novoCliente = new Cliente(nome, bi, null, telefone, null, null);
                controller.adicionarCliente(novoCliente);
                return novoCliente;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Dados do Cliente Inválidos", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
    }

    private void finalizarVenda() {
        String tipoUsuario = controller.getTipoUsuarioLogado();
        if (!("Vendedor".equals(tipoUsuario) || "Gestor".equals(tipoUsuario) || "Administrador".equals(tipoUsuario))) {
            JOptionPane.showMessageDialog(this, "Apenas Vendedor, Gestor ou Administrador podem finalizar vendas.", "Permissão Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (itensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente cliente = getClienteDaVenda();
        if (cliente == null) return;

        for (ItemVenda item : itensVenda) {
            Equipamento eq = item.getEquipamento();
            int disponivelReal = eq.getQuantidadeEstoque() - eq.getReservado();

            System.out.println("Verificando: " + eq.getMarca() +
                    " - Estoque: " + eq.getQuantidadeEstoque() +
                    " - Reservado: " + eq.getReservado() +
                    " - Disponível: " + disponivelReal +
                    " - Necessário: " + item.getQuantidade());

            if (item.getQuantidade() > disponivelReal) {
                JOptionPane.showMessageDialog(this,
                        "Estoque insuficiente para " + eq.getMarca() +
                                "\nDisponível: " + disponivelReal +
                                "\nSolicitado: " + item.getQuantidade(),
                        "Estoque Insuficiente", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Venda venda = new Venda(new Date(), vendedorLogado, cliente, new ArrayList<>(), BigDecimal.ZERO);

        for (ItemVenda item : itensVenda) {
            boolean ok = venda.adicionarItem(item.getEquipamento(), item.getQuantidade());
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "Falha ao adicionar item: estoque insuficiente para " + item.getEquipamento().getMarca(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            if (txtDesconto != null && !txtDesconto.getText().isBlank()) {
                BigDecimal desc = new BigDecimal(txtDesconto.getText().trim().replace(',', '.'));
                venda.setDesconto(desc);
            }
            if (txtImposto != null && !txtImposto.getText().isBlank()) {
                BigDecimal imp = new BigDecimal(txtImposto.getText().trim().replace(',', '.'));
                venda.setImposto(imp);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Desconto/Imposto inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal totalFinal = venda.getTotalComDescontosImpostos();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Finalizar venda para " + cliente.getNome() + "?\n" +
                        "Total: " + String.format("%.2f MT", totalFinal) + "\n" +
                        "Itens: " + itensVenda.size(),
                "Confirmar Venda", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.registrarVenda(venda)) {
                JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparVenda();
                carregarDadosIniciais();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao registrar a venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparVenda() {
        itensVenda.clear();
        limparCamposClienteBalcao();
        txtAreaDetalhesCliente.setText("Selecione um cliente para ver os detalhes.");
        if (cmbCliente.getItemCount() > 0) cmbCliente.setSelectedIndex(0);
        atualizarCarrinhoETotal();
    }
    private void atualizarCarrinhoETotal() {
        modeloTabelaItens.setRowCount(0);
        totalVenda = BigDecimal.ZERO;

        for (ItemVenda item : itensVenda) {
            BigDecimal precoUnit = item.getPrecoUnitario();
            BigDecimal subtotal = item.getSubtotal();
            modeloTabelaItens.addRow(new Object[] {
                    item.getQuantidade(),
                    item.getEquipamento().getMarca(),
                    String.format("%.2f", precoUnit),
                    String.format("%.2f", subtotal)
            });
            totalVenda = totalVenda.add(subtotal);
        }

        BigDecimal totalFinal = aplicarDescontoEImpostoSeExistirem(totalVenda);
        lblTotalVenda.setText("TOTAL: " + String.format("%.2f MT", totalFinal));

        txtQuantidade.setText("1");
        tabelaEquipamentosDisponiveis.clearSelection();
        txtAreaDetalhesEquipamento.setText("Detalhes do produto...");
        lblFotoEquipamento.setIcon(null);
        lblFotoEquipamento.setText("Selecione um produto");
    }
    private BigDecimal aplicarDescontoEImpostoSeExistirem(BigDecimal base) {
        BigDecimal total = base != null ? base : BigDecimal.ZERO;
        try {
            if (txtDesconto != null && txtDesconto.getText() != null && !txtDesconto.getText().isBlank()) {
                BigDecimal desc = new BigDecimal(txtDesconto.getText().trim().replace(',', '.'));
                total = total.subtract(desc);
            }
            if (txtImposto != null && txtImposto.getText() != null && !txtImposto.getText().isBlank()) {
                BigDecimal imp = new BigDecimal(txtImposto.getText().trim().replace(',', '.'));
                total = total.add(imp);
            }
        } catch (NumberFormatException ex) {


        }
        return total.max(BigDecimal.ZERO);
    }

    private BigDecimal getTotalVenda() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itensVenda) {
            total = total.add(item.getSubtotal());
        }
        return aplicarDescontoEImpostoSeExistirem(total);
    }

    private void finalizarReserva() {

        if (itensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente cliente = getClienteDaVenda();
        if (cliente == null) {
            return;
        }

        try {
            // Criar objeto Reserva
            Reserva reserva = new Reserva();

            reserva.setCliente(cliente);
            reserva.setVendedor(vendedorLogado);
            reserva.setDataReserva(new Date());

            // Calcular data de expiração (7 dias)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 7);
            reserva.setExpiraEm(cal.getTime());

            // Converter itens
            List<ItemReserva> itensReserva = new ArrayList<>();

            for (ItemVenda itemVenda : itensVenda) {
                String equipamentoId = itemVenda.getEquipamento().getId();

                Optional<Equipamento> equipamentoOpt = controller.findEquipamentoById(equipamentoId);

                if (equipamentoOpt.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Equipamento não encontrado: " + equipamentoId,
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Equipamento equipamentoAtual = equipamentoOpt.get();

                ItemReserva itemReserva = new ItemReserva(equipamentoAtual, itemVenda.getQuantidade());
                itensReserva.add(itemReserva);
            }

            reserva.setItens(itensReserva);
            reserva.setStatus(Reserva.StatusReserva.ATIVA);

            BigDecimal totalFinal = getTotalVenda();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Salvar reserva para " + cliente.getNome() + "?\n" +
                            "Total: " + String.format("%.2f MT", totalFinal) +
                            "\nExpira em: " + new SimpleDateFormat("dd/MM/yyyy").format(reserva.getExpiraEm()) +
                            "\nItens: " + itensReserva.size(),
                    "Confirmar Reserva", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                boolean sucesso = controller.registrarReserva(reserva);

                if (sucesso) {
                    JOptionPane.showMessageDialog(this,
                            "Reserva registrada com sucesso!\nID: " + reserva.getIdReserva(),
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparVenda();
                    carregarDadosIniciais();
                    voltarParaGerirReservasEAtualizar();

                } else {
                    System.err.println("❌ Falha ao salvar reserva no controller");
                    JOptionPane.showMessageDialog(this,
                            "Falha ao registrar a reserva.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao registrar reserva: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void voltarParaGerirReservasEAtualizar() {

        try {
            CardLayoutManager clm = controller.getCardLayoutManager();

            GerirReservasView viewReservas = GerirReservasView.getInstance();

            if (viewReservas != null) {
                viewReservas.carregarReservas();
                clm.showPanel("GerirReservas");
            } else {
                GerirReservasView novaView = new GerirReservasView(controller);
                clm.addPanel(novaView, "GerirReservas");
                clm.showPanel("GerirReservas");
            }

        } catch (Exception e) {
            e.printStackTrace();
            voltarMenuPrincipal();
        }
    }
    public void carregarReserva(Reserva reserva) {
        if (reserva == null) return;

        limparVenda();

        if (reserva.getCliente() != null) {
            for (int i = 0; i < cmbCliente.getItemCount(); i++) {
                Cliente c = cmbCliente.getItemAt(i);
                if (c.getId().equals(reserva.getCliente().getId())) {
                    cmbCliente.setSelectedIndex(i);
                    rbClienteCorporativo.setSelected(true);
                    toggleClienteFields(false);
                    break;
                }
            }
        }
        if (reserva.getItens() != null) {
            for (var itemReserva : reserva.getItens()) {
                ItemVenda itemVenda = new ItemVenda(itemReserva.getEquipamento(), itemReserva.getQuantidade());
                itensVenda.add(itemVenda);
            }
            atualizarCarrinhoETotal();
        }
        if (!modoReserva) {
            btnFinalizarVenda.setText("✅ Vender");
        }
    }

    private void voltarMenuPrincipal() {
        String tipoUsuario = controller.getTipoUsuarioLogado();
        if (tipoUsuario == null) {
            controller.getCardLayoutManager().showPanel("Login");
            return;
        }

        switch (tipoUsuario) {
            case "Gestor":
                controller.getCardLayoutManager().showPanel("MenuGestor");
                break;
            case "Vendedor":
                controller.getCardLayoutManager().showPanel("MenuVendedor");
                break;
            case "Administrador":
            default:
                controller.getCardLayoutManager().showPanel("MenuAdministrador");
                break;
        }
    }
}
