
package view;

import controller.SistemaController;
import model.abstractas.Equipamento;
import model.concretas.*;
import util.GeradorID;
import util.UITheme;
import util.Validador;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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

    // --- COMPONENTES DE PRODUTOS ---
    private JTextField txtBuscarProdutos;
    private JTable tabelaEquipamentosDisponiveis;
    private DefaultTableModel modeloTabelaEquipamentos;
    private TableRowSorter<DefaultTableModel> sorterEquipamentos;
    private JLabel lblFotoEquipamento;
    private JTextArea txtAreaDetalhesEquipamento;
    private JTextField txtQuantidade;
    private JButton btnAdicionarItem;

    // --- COMPONENTES DO CARRINHO ---
    private JTable tabelaItensVenda;
    private DefaultTableModel modeloTabelaItens;
    private TableRowSorter<DefaultTableModel> sorterItensVenda;
    private JLabel lblTotalVenda;
    private BigDecimal totalFinal = BigDecimal.ZERO;
    private boolean modoReserva;

    // --- BOTÕES DE AÇÃO ---
    private JButton btnRemoverItem, btnFinalizarVenda, btnLimparVenda, btnVoltar;

    // --- DADOS ---
    private List<ItemVenda> itensVenda = new ArrayList<>();
    private BigDecimal totalVenda = BigDecimal.ZERO;

    // --- COMPONENTES PARA PAGAMENTO ---
    private JRadioButton rbCash, rbCartao;
    private JTextField txtTotalPago;
    private JLabel lblTroco;
    private JLabel lblTrocoValue;

    public RegistrarVendaView(SistemaController controller, Vendedor vendedorLogado) {
        this.controller = controller;
        this.vendedorLogado = vendedorLogado;
        this.modoReserva = false;
        initComponents();
        setupLayout();
        installAltForVoltar();
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
        bgTipoCliente.add(rbClienteCorporativo);
        bgTipoCliente.add(rbClienteBalcao);

        cardLayoutCliente = new CardLayout();
        painelCamposCliente = new JPanel(cardLayoutCliente);
        painelCamposCliente.setOpaque(false);

        // --- Card Cliente Corporativo ---
        JPanel painelCorp = new JPanel(new BorderLayout(5, 5));
        painelCorp.setOpaque(false);
        cmbCliente = new JComboBox<>();
        // Custom renderer for JComboBox to show only Nome (ID)
        cmbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente cliente) {
                    setText(cliente.getNome() + " (" + cliente.getId() + ")");
                }
                return this;
            }
        });
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
        JPanel painelBalcao = new JPanel(new GridLayout(0, 2, 5, 5));
        painelBalcao.setOpaque(false);
        txtNomeClienteBalcao = UITheme.createStyledTextField();
        txtBiClienteBalcao = UITheme.createStyledTextField();
        txtTelefoneClienteBalcao = UITheme.createStyledTextField();
        UITheme.setFieldAsInput(txtNomeClienteBalcao, "Nome");
        UITheme.setFieldAsInput(txtBiClienteBalcao, "Nº BI (Obrigatório)");
        UITheme.setFieldAsInput(txtTelefoneClienteBalcao, "Telefone (Obrigatório)");
        painelBalcao.add(UITheme.createFormLabel("Nome:"));
        painelBalcao.add(txtNomeClienteBalcao);
        painelBalcao.add(UITheme.createFormLabel("Nº BI:"));
        painelBalcao.add(txtBiClienteBalcao);
        painelBalcao.add(UITheme.createFormLabel("Telefone:"));
        painelBalcao.add(txtTelefoneClienteBalcao);
        painelCamposCliente.add(painelBalcao, "BALCAO");

        // --- INICIALIZAÇÃO DOS COMPONENTES DE PRODUTO ---
        txtBuscarProdutos = UITheme.createStyledTextField();
        txtBuscarProdutos.setToolTipText("Buscar por marca ou tipo");
        modeloTabelaEquipamentos = new DefaultTableModel(new String[]{"Marca", "Tipo", "Preço", "Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabelaEquipamentosDisponiveis = new JTable(modeloTabelaEquipamentos);
        tabelaEquipamentosDisponiveis.setRowHeight(35);
        tabelaEquipamentosDisponiveis.setShowGrid(false);
        tabelaEquipamentosDisponiveis.setIntercellSpacing(new Dimension(0, 1));
        tabelaEquipamentosDisponiveis.setBackground(Color.WHITE);
        // Set column header color to dark gray
        tabelaEquipamentosDisponiveis.getTableHeader().setForeground(Color.DARK_GRAY);
        sorterEquipamentos = new TableRowSorter<>(modeloTabelaEquipamentos);
        tabelaEquipamentosDisponiveis.setRowSorter(sorterEquipamentos);

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
        btnAdicionarItem = UITheme.createSuccessButton("➕ Adicionar");
        btnAdicionarItem.setPreferredSize(new Dimension(180, 45));
        btnAdicionarItem.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        btnAdicionarItem.setToolTipText("Adicionar item ao carrinho");

        // --- INICIALIZAÇÃO DO CARRINHO E AÇÕES ---
        modeloTabelaItens = new DefaultTableModel(new String[]{"Qtd", "Produto", "Preço Unit.", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        tabelaItensVenda = new JTable(modeloTabelaItens) {
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 0) {
                    try {
                        int novaQtd = Integer.parseInt(aValue.toString());
                        if (novaQtd > 0) {
                            ItemVenda item = itensVenda.get(row);
                            int disponivel = item.getEquipamento().getQuantidadeEstoque() - item.getEquipamento().getReservado();
                            if (novaQtd <= disponivel) {
                                item.setQuantidade(novaQtd);
                                atualizarCarrinhoETotal();
                            } else {
                                JOptionPane.showMessageDialog(this, "Quantidade excede o estoque disponível.", "Erro", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        tabelaItensVenda.setRowHeight(35);
        tabelaItensVenda.setShowGrid(false);
        tabelaItensVenda.setIntercellSpacing(new Dimension(0, 1));
        tabelaItensVenda.setBackground(Color.WHITE);

        tabelaItensVenda.getTableHeader().setForeground(Color.DARK_GRAY);
        sorterItensVenda = new TableRowSorter<>(modeloTabelaItens);
        tabelaItensVenda.setRowSorter(sorterItensVenda);

        lblTotalVenda = UITheme.createTitleLabel("TOTAL: 0,00 MT");
        lblTotalVenda.setForeground(UITheme.SUCCESS_COLOR);

        // --- NOVOS COMPONENTES PARA PAGAMENTO ---
        rbCash = new JRadioButton("Cash", true);
        rbCartao = new JRadioButton("Cartão");
        UITheme.styleRadioButton(rbCash);
        UITheme.styleRadioButton(rbCartao);
        ButtonGroup bgPagamento = new ButtonGroup();
        bgPagamento.add(rbCash);
        bgPagamento.add(rbCartao);

        txtTotalPago = UITheme.createStyledTextField();
        txtTotalPago.setPreferredSize(new Dimension(120, UITheme.INPUT_SIZE.height));
        UITheme.setFieldAsInput(txtTotalPago, "Valor Pago");

        lblTroco = UITheme.createTitleLabel("Troco:");
        lblTroco.setForeground(UITheme.SUCCESS_COLOR);
        lblTrocoValue = UITheme.createTitleLabel("0,00 MT");
        lblTrocoValue.setForeground(UITheme.SUCCESS_COLOR);

        btnRemoverItem = UITheme.createDangerButton("🗑️ Remover");
        btnRemoverItem.setPreferredSize(new Dimension(180, 45));
        btnRemoverItem.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        btnRemoverItem.setToolTipText("Remover item selecionado");

        btnFinalizarVenda = UITheme.createSuccessButton("✅ Finalizar");
        btnFinalizarVenda.setPreferredSize(new Dimension(180, 45));
        btnFinalizarVenda.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        btnFinalizarVenda.setToolTipText("Finalizar venda ou reserva");

        btnLimparVenda = UITheme.createSecondaryButton("🧹 Limpar");
        btnLimparVenda.setPreferredSize(new Dimension(180, 45));
        btnLimparVenda.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        btnLimparVenda.setToolTipText("Limpar carrinho");

        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnVoltar.setPreferredSize(new Dimension(180, 45));
        btnVoltar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        btnVoltar.setToolTipText("Voltar ao menu");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel topBar = UITheme.createTopbar("REGISTRAR VENDA", btnVoltar);
        add(topBar, BorderLayout.NORTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setResizeWeight(0.5);
        mainSplit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainSplit.setLeftComponent(createLeftPanel());
        mainSplit.setRightComponent(createRightPanel());
        add(mainSplit, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);

        JPanel painelCliente = UITheme.createCardPanel();
        painelCliente.setLayout(new BorderLayout(10, 10));

        painelCliente.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                "🧑‍💼 1. Cliente",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI Emoji", Font.BOLD, 12),
                Color.DARK_GRAY));
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setOpaque(false);
        radioPanel.add(rbClienteCorporativo);
        radioPanel.add(rbClienteBalcao);
        painelCliente.add(radioPanel, BorderLayout.NORTH);
        painelCliente.add(painelCamposCliente, BorderLayout.CENTER);

        JPanel painelCarrinho = UITheme.createCardPanel();
        painelCarrinho.setLayout(new BorderLayout(10, 10));
        // Set titled border with emoji and dark gray color
        painelCarrinho.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                "🛒 3. Carrinho",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI Emoji", Font.BOLD, 12),
                Color.DARK_GRAY));
        JScrollPane scrollPaneCarrinho = new JScrollPane(tabelaItensVenda);
        scrollPaneCarrinho.setPreferredSize(new Dimension(0, 250));
        painelCarrinho.add(scrollPaneCarrinho, BorderLayout.CENTER);

        JPanel pagamentoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pagamentoPanel.setOpaque(false);
        pagamentoPanel.setBorder(BorderFactory.createTitledBorder("💳 Pagamento"));
        pagamentoPanel.add(UITheme.createFormLabel("Método:"));
        pagamentoPanel.add(rbCash);
        pagamentoPanel.add(rbCartao);
        pagamentoPanel.add(UITheme.createFormLabel("Valor Pago:"));
        pagamentoPanel.add(txtTotalPago);
        pagamentoPanel.add(lblTroco);
        pagamentoPanel.add(lblTrocoValue);

        JPanel acoesCarrinhoPanel = new JPanel(new BorderLayout(10, 10));
        acoesCarrinhoPanel.setOpaque(false);
        acoesCarrinhoPanel.add(lblTotalVenda, BorderLayout.NORTH);
        acoesCarrinhoPanel.add(pagamentoPanel, BorderLayout.CENTER);

        JPanel botoesCarrinho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        botoesCarrinho.setOpaque(false);
        botoesCarrinho.add(btnRemoverItem);
        botoesCarrinho.add(btnLimparVenda);
        botoesCarrinho.add(btnFinalizarVenda);
        acoesCarrinhoPanel.add(botoesCarrinho, BorderLayout.SOUTH);

        painelCarrinho.add(acoesCarrinhoPanel, BorderLayout.SOUTH);

        leftPanel.add(painelCliente, BorderLayout.NORTH);
        leftPanel.add(painelCarrinho, BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = UITheme.createCardPanel();
        rightPanel.setLayout(new BorderLayout(10, 10));
        // Set titled border with emoji and dark gray color
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                "📦 2. Produtos",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI Emoji", Font.BOLD, 12),
                Color.DARK_GRAY));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(UITheme.createFormLabel("Buscar:"));
        searchPanel.add(txtBuscarProdutos);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel detalhesPanel = new JPanel(new BorderLayout(10, 10));
        detalhesPanel.setOpaque(false);
        detalhesPanel.add(lblFotoEquipamento, BorderLayout.WEST);
        detalhesPanel.add(new JScrollPane(txtAreaDetalhesEquipamento), BorderLayout.CENTER);

        JPanel acoesProdutoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        acoesProdutoPanel.setOpaque(false);
        acoesProdutoPanel.add(UITheme.createFormLabel("Qtd:"));
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

        // Busca de produtos
        txtBuscarProdutos.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarTabelaEquipamentos(); }
            public void removeUpdate(DocumentEvent e) { filtrarTabelaEquipamentos(); }
            public void changedUpdate(DocumentEvent e) { filtrarTabelaEquipamentos(); }
        });

        // Eventos para pagamento
        rbCash.addActionListener(e -> togglePagamentoFields(true));
        rbCartao.addActionListener(e -> togglePagamentoFields(false));
        txtTotalPago.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizarTroco(); }
            public void removeUpdate(DocumentEvent e) { atualizarTroco(); }
            public void changedUpdate(DocumentEvent e) { atualizarTroco(); }
        });
    }

    private void filtrarTabelaEquipamentos() {
        String texto = txtBuscarProdutos.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            sorterEquipamentos.setRowFilter(null);
        } else {
            sorterEquipamentos.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void togglePagamentoFields(boolean isCash) {
        txtTotalPago.setEnabled(isCash);
        lblTrocoValue.setVisible(isCash);
        if (!isCash) {
            txtTotalPago.setText(totalVenda.toPlainString());
            lblTrocoValue.setText("0,00 MT");
        }
        atualizarTroco();
    }

    private void atualizarTroco() {
        try {
            BigDecimal pago = new BigDecimal(txtTotalPago.getText().trim().replace(',', '.'));
            BigDecimal trocoCalc = pago.subtract(totalFinal).max(BigDecimal.ZERO);
            lblTrocoValue.setText(String.format("%.2f MT", trocoCalc));
        } catch (NumberFormatException e) {
            lblTrocoValue.setText("0,00 MT");
        }
    }

    private void carregarDadosIniciais() {
        cmbCliente.removeAllItems();
        controller.getClientes().forEach(cmbCliente::addItem);

        modeloTabelaEquipamentos.setRowCount(0);
        for (Equipamento eq : controller.getEquipamentos()) {
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
        if (rbClienteCorporativo.isSelected()) {
            Cliente c = (Cliente) cmbCliente.getSelectedItem();
            if (c != null) {
                StringBuilder detalhes = new StringBuilder();
                detalhes.append("Nome:\t").append(c.getNome()).append("\n");
                detalhes.append("Nº BI:\t").append(c.getNrBI()).append("\n");
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
            if (eq instanceof Computador comp) {
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

            int disponivelReal = eq.getQuantidadeEstoque() - eq.getReservado();
            if (quantidade > disponivelReal) {
                JOptionPane.showMessageDialog(this, "Quantidade em estoque insuficiente.", "Estoque", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (ItemVenda item : itensVenda) {
                if (item.getEquipamento().getId().equals(eq.getId())) {
                    int novaQtd = item.getQuantidade() + quantidade;
                    if (novaQtd > disponivelReal) {
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
        if (rbClienteCorporativo.isSelected()) {
            Cliente c = (Cliente) cmbCliente.getSelectedItem();
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente corporativo.", "Erro", JOptionPane.ERROR_MESSAGE);
                return null;
            }
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

        String metodo = rbCash.isSelected() ? "CASH" : "CARTAO";
        BigDecimal pago;
        try {
            pago = new BigDecimal(txtTotalPago.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor pago inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (rbCash.isSelected() && pago.compareTo(venda.getTotalComDescontosImpostos()) < 0) {
            JOptionPane.showMessageDialog(this, "Valor pago insuficiente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        venda.setMetodoPagamento(metodo);
        venda.setTotalPago(pago);

        BigDecimal totalFinal = venda.getTotalComDescontosImpostos();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Finalizar venda para " + cliente.getNome() + "?\n" +
                        "Total: " + String.format("%.2f MT", totalFinal) + "\n" +
                        (itensVenda.size() > 5 ? "Desconto (5%): " + String.format("%.2f MT", venda.getDesconto()) + "\n" : "") +
                        "Imposto (3%): " + String.format("%.2f MT", venda.getImposto()) + "\n" +
                        "Método de Pagamento: " + metodo + "\n" +
                        "Valor Pago: " + String.format("%.2f MT", pago) + "\n" +
                        (rbCash.isSelected() ? "Troco: " + String.format("%.2f MT", venda.getTroco()) + "\n" : "") +
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
        totalVenda = BigDecimal.ZERO;
        totalFinal = BigDecimal.ZERO; // Adiciona reinicialização
        lblTotalVenda.setText("TOTAL: 0,00 MT");
        txtTotalPago.setText("");
        lblTrocoValue.setText("0,00 MT");
        rbCash.setSelected(true);
        togglePagamentoFields(true);
        modeloTabelaItens.setRowCount(0);
        txtQuantidade.setText("1");
        tabelaEquipamentosDisponiveis.clearSelection();
        txtAreaDetalhesEquipamento.setText("Detalhes do produto...");
        lblFotoEquipamento.setIcon(null);
        lblFotoEquipamento.setText("Selecione um produto");
        txtBuscarProdutos.setText("");
    }
    private void atualizarCarrinhoETotal() {
        modeloTabelaItens.setRowCount(0);
        totalVenda = BigDecimal.ZERO;

        for (ItemVenda item : itensVenda) {
            BigDecimal precoUnit = item.getPrecoUnitario();
            BigDecimal subtotal = item.getSubtotal();
            modeloTabelaItens.addRow(new Object[]{
                    item.getQuantidade(),
                    item.getEquipamento().getMarca(),
                    String.format("%.2f", precoUnit),
                    String.format("%.2f", subtotal)
            });
            totalVenda = totalVenda.add(subtotal);
        }

        Venda vendaTemp = new Venda();
        vendaTemp.setItens(new ArrayList<>(itensVenda));
        totalFinal = vendaTemp.getTotalComDescontosImpostos();
        lblTotalVenda.setText("TOTAL: " + String.format("%.2f MT", totalFinal));

        txtQuantidade.setText("1");
        tabelaEquipamentosDisponiveis.clearSelection();
        txtAreaDetalhesEquipamento.setText("Detalhes do produto...");
        lblFotoEquipamento.setIcon(null);
        lblFotoEquipamento.setText("Selecione um produto");
        atualizarTroco();
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
            Reserva reserva = new Reserva();
            reserva.setCliente(cliente);
            reserva.setVendedor(vendedorLogado);
            reserva.setDataReserva(new Date());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 7);
            reserva.setExpiraEm(cal.getTime());

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
            BigDecimal taxa = totalFinal.multiply(BigDecimal.valueOf(0.3));
            reserva.setTaxaPaga(taxa);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Salvar reserva para " + cliente.getNome() + "?\n" +
                            "Total: " + String.format("%.2f MT", totalFinal) + "\n" +
                            "Taxa Obrigatória (30%): " + String.format("%.2f MT", taxa) + "\n" +
                            "Expira em: " + new SimpleDateFormat("dd/MM/yyyy").format(reserva.getExpiraEm()) +
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

    private BigDecimal getTotalVenda() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itensVenda) {
            total = total.add(item.getSubtotal());
        }
        Venda vendaTemp = new Venda();
        vendaTemp.setItens(new ArrayList<>(itensVenda));
        return vendaTemp.getTotalComDescontosImpostos();
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

    private void installAltForVoltar() {
        JComponent root = getRootPane();
        if (root == null) {
            root = this;
        }

        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        KeyStroke altPress = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, false);
        KeyStroke altRelease = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true);

        im.put(altPress, "altPressed_voltar");
        im.put(altRelease, "altReleased_voltar");

        am.put("altPressed_voltar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnVoltar != null) {
                    ButtonModel m = btnVoltar.getModel();
                    m.setArmed(true);
                    m.setPressed(true);
                    btnVoltar.requestFocusInWindow();
                }
            }
        });

        am.put("altReleased_voltar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnVoltar != null) {
                    btnVoltar.doClick();
                    ButtonModel m = btnVoltar.getModel();
                    m.setPressed(false);
                    m.setArmed(false);
                }
            }
        });
    }
}