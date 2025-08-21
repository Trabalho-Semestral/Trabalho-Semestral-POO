package view;

import controller.SistemaController;

import model.abstractas.Equipamento;
import model.concretas.Cliente;
import model.concretas.Venda;
import model.concretas.Vendedor;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tela para registrar vendas com layout padronizado.
 */
public class RegistrarVendaView extends JPanel {
    
    private SistemaController controller;
    private Vendedor vendedorLogado;
    
    // Componentes da interface
    private JComboBox<Cliente> cmbCliente;
    private JComboBox<Equipamento> cmbEquipamento;
    private JTextField txtQuantidade;
    private JButton btnAdicionarItem;
    private JButton btnRemoverItem;
    private JButton btnFinalizarVenda;
    private JButton btnLimparVenda;
    private JButton btnVoltar;
    private JLabel lblTotalVenda;
    private JLabel lblInfoCliente;
    private JLabel lblInfoEquipamento;
    
    // Tabela de itens da venda
    private JTable tabelaItensVenda;
    private DefaultTableModel modeloTabelaItens;
    
    // Dados da venda
    private List<ItemVenda> itensVenda;
    private double totalVenda;
    
    // Classe interna para representar um item da venda
    private static class ItemVenda {
        public Equipamento equipamento;
        public int quantidade;
        public double subtotal;
        
        public ItemVenda(Equipamento equipamento, int quantidade) {
            this.equipamento = equipamento;
            this.quantidade = quantidade;
            this.subtotal = equipamento.getPreco() * quantidade;
        }
    }
    
    public RegistrarVendaView(SistemaController controller, Vendedor vendedorLogado) {
        this.controller = controller;
        this.vendedorLogado = vendedorLogado;
        this.itensVenda = new ArrayList<>();
        this.totalVenda = 0.0;
        initComponents();
        setupLayout();
        setupEvents();
        carregarDadosIniciais();
    }
    
    /**
     * Inicializa os componentes da interface.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);
        
        // ComboBoxes com tema personalizado
        cmbCliente = UITheme.createStyledComboBox(new Cliente[0]);
        cmbEquipamento = UITheme.createStyledComboBox(new Equipamento[0]);
        
        // Campo de quantidade
        txtQuantidade = UITheme.createStyledTextField();
        txtQuantidade.setText("1");
        
        // Botões com tema personalizado
        btnAdicionarItem = UITheme.createPrimaryButton("➕ Adicionar Item");
        btnRemoverItem = UITheme.createDangerButton("➖ Remover Item");
        btnFinalizarVenda = UITheme.createPrimaryButton("✅ Finalizar Venda");
        btnLimparVenda = UITheme.createSecondaryButton("🗑️ Limpar Venda");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        
        // Labels informativos
        lblTotalVenda = UITheme.createTitleLabel("Total da Venda: 0,00 MT");
        lblTotalVenda.setForeground(UITheme.PRIMARY_COLOR);
        
        lblInfoCliente = UITheme.createBodyLabel("Selecione um cliente");
        lblInfoCliente.setForeground(UITheme.TEXT_SECONDARY);
        
        lblInfoEquipamento = UITheme.createBodyLabel("Selecione um equipamento");
        lblInfoEquipamento.setForeground(UITheme.TEXT_SECONDARY);
        
        // Tabela de itens
        String[] colunasItens = {"ID", "Marca", "Tipo", "Preço Unit.", "Qtd", "Subtotal"};
        modeloTabelaItens = new DefaultTableModel(colunasItens, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaItensVenda = new JTable(modeloTabelaItens);
        tabelaItensVenda.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaItensVenda.setFont(UITheme.FONT_BODY);
        tabelaItensVenda.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaItensVenda.setRowHeight(30);
        tabelaItensVenda.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        tabelaItensVenda.setSelectionForeground(UITheme.TEXT_PRIMARY);
    }
    
    /**
     * Configura o layout da interface seguindo o padrão de relatórios.
     */
    private void setupLayout() {
        // Painel superior com título
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        
        JLabel lblTitulo = UITheme.createHeadingLabel("🛒 Registrar Nova Venda");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        topPanel.add(lblTitulo, BorderLayout.CENTER);
        
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        voltarPanel.add(btnVoltar);
        topPanel.add(voltarPanel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Painel principal com layout padronizado
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        
        // Painel de formulário (superior)
        JPanel formPanel = criarPainelFormulario();
        mainPanel.add(formPanel, BorderLayout.NORTH);
        
        // Painel de botões de ação (meio)
        JPanel buttonPanel = criarPainelBotoes();
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Painel de tabela (inferior)
        JPanel tablePanel = criarPainelTabela();
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Cria o painel do formulário seguindo o padrão.
     */
    private JPanel criarPainelFormulario() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título do formulário
        JLabel lblFormTitulo = UITheme.createSubtitleLabel("Dados da Venda");
        lblFormTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblFormTitulo, BorderLayout.NORTH);
        
        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Cliente
        gbc.gridx = 0; gbc.gridy = 0;
        formContent.add(UITheme.createBodyLabel("Cliente:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formContent.add(cmbCliente, gbc);
        
        // Equipamento
        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formContent.add(UITheme.createBodyLabel("Equipamento:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formContent.add(cmbEquipamento, gbc);
        
        // Quantidade
        gbc.gridx = 4; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formContent.add(UITheme.createBodyLabel("Quantidade:"), gbc);
        gbc.gridx = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        formContent.add(txtQuantidade, gbc);
        
        // Informações adicionais
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formContent.add(lblInfoCliente, gbc);
        
        gbc.gridx = 3; gbc.gridy = 1; gbc.gridwidth = 3;
        formContent.add(lblInfoEquipamento, gbc);
        
        panel.add(formContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Cria o painel de botões de ação seguindo o padrão.
     */
    private JPanel criarPainelBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBackground(UITheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        panel.add(btnAdicionarItem);
        panel.add(btnRemoverItem);
        panel.add(btnLimparVenda);
        panel.add(btnFinalizarVenda);
        
        return panel;
    }
    
    /**
     * Cria o painel da tabela seguindo o padrão.
     */
    private JPanel criarPainelTabela() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 400));
        
        // Título da tabela
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.CARD_BACKGROUND);
        
        JLabel lblTabelaTitulo = UITheme.createSubtitleLabel("Itens da Venda");
        headerPanel.add(lblTabelaTitulo, BorderLayout.WEST);
        
        lblTotalVenda.setForeground(UITheme.PRIMARY_COLOR);
        headerPanel.add(lblTotalVenda, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(tabelaItensVenda);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Configura os eventos da interface.
     */
    private void setupEvents() {
        // ComboBox cliente
        cmbCliente.addActionListener(e -> atualizarInfoCliente());
        
        // ComboBox equipamento
        cmbEquipamento.addActionListener(e -> atualizarInfoEquipamento());
        
        // Botão adicionar item
        btnAdicionarItem.addActionListener(e -> adicionarItem());
        
        // Botão remover item
        btnRemoverItem.addActionListener(e -> removerItem());
        
        // Botão finalizar venda
        btnFinalizarVenda.addActionListener(e -> finalizarVenda());
        
        // Botão limpar venda
        btnLimparVenda.addActionListener(e -> limparVenda());
        
        // Botão voltar
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
        
        // Enter no campo quantidade adiciona item
        txtQuantidade.addActionListener(e -> adicionarItem());
    }
    
    /**
     * Carrega os dados iniciais (clientes e equipamentos).
     */
    private void carregarDadosIniciais() {
        // Carregar clientes
        List<Cliente> clientes = controller.getClientes();
        cmbCliente.removeAllItems();
        for (Cliente cliente : clientes) {
            cmbCliente.addItem(cliente);
        }
        
        // Carregar equipamentos
        List<Equipamento> equipamentos = controller.getEquipamentos();
        cmbEquipamento.removeAllItems();
        for (Equipamento equipamento : equipamentos) {
            cmbEquipamento.addItem(equipamento);
        }
        
        atualizarInfoCliente();
        atualizarInfoEquipamento();
    }
    
    /**
     * Atualiza as informações do cliente selecionado.
     */
    private void atualizarInfoCliente() {
        Cliente clienteSelecionado = (Cliente) cmbCliente.getSelectedItem();
        if (clienteSelecionado != null) {
            lblInfoCliente.setText("📞 " + clienteSelecionado.getTelefone() + " | 📧 " + 
                                 (clienteSelecionado.getEmail() != null ? clienteSelecionado.getEmail() : "N/A"));
        } else {
            lblInfoCliente.setText("Selecione um cliente");
        }
    }
    
    /**
     * Atualiza as informações do equipamento selecionado.
     */
    private void atualizarInfoEquipamento() {
        Equipamento equipamentoSelecionado = (Equipamento) cmbEquipamento.getSelectedItem();
        if (equipamentoSelecionado != null) {
            lblInfoEquipamento.setText("💰 " + String.format("%.2f MT", equipamentoSelecionado.getPreco()) + 
                                     " | 📦 " + equipamentoSelecionado.getEstado());
        } else {
            lblInfoEquipamento.setText("Selecione um equipamento");
        }
    }
    
    /**
     * Adiciona um item à venda.
     */
    private void adicionarItem() {
        try {
            Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
            Equipamento equipamento = (Equipamento) cmbEquipamento.getSelectedItem();
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            
            // Validações
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (equipamento == null) {
                JOptionPane.showMessageDialog(this, "Selecione um equipamento.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verificar se o item já existe na venda
            for (ItemVenda item : itensVenda) {
                if (item.equipamento.getId().equals(equipamento.getId())) {
                    item.quantidade += quantidade;
                    item.subtotal = item.equipamento.getPreco() * item.quantidade;
                    atualizarTabelaItens();
                    calcularTotal();
                    txtQuantidade.setText("1");
                    return;
                }
            }
            
            // Adicionar novo item
            ItemVenda novoItem = new ItemVenda(equipamento, quantidade);
            itensVenda.add(novoItem);
            
            atualizarTabelaItens();
            calcularTotal();
            txtQuantidade.setText("1");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Remove o item selecionado da venda.
     */
    private void removerItem() {
        int selectedRow = tabelaItensVenda.getSelectedRow();
        if (selectedRow >= 0) {
            itensVenda.remove(selectedRow);
            atualizarTabelaItens();
            calcularTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Finaliza a venda.
     */
    private void finalizarVenda() {
        if (itensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um item à venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirmar venda
        int confirm = JOptionPane.showConfirmDialog(this,
            "Finalizar venda no valor de " + String.format("%.2f MT", totalVenda) + "?",
            "Confirmar Venda",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Criar venda
                List<Equipamento> equipamentos = new ArrayList<>();
                for (ItemVenda item : itensVenda) {
                    for (int i = 0; i < item.quantidade; i++) {
                        equipamentos.add(item.equipamento);
                    }
                }
                
                Venda venda = new Venda(new Date(), vendedorLogado, cliente, equipamentos, totalVenda);
                
                if (controller.registrarVenda(venda)) {
                    JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparVenda();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao registrar venda.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao finalizar venda: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Limpa todos os itens da venda.
     */
    private void limparVenda() {
        itensVenda.clear();
        atualizarTabelaItens();
        calcularTotal();
        txtQuantidade.setText("1");
    }
    
    /**
     * Volta ao menu principal.
     */
    private void voltarMenuPrincipal() {
        if (!itensVenda.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Há itens na venda atual. Deseja realmente sair?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        controller.getCardLayoutManager().showPanel("MenuAdministrador");
    }
    
    /**
     * Atualiza a tabela de itens da venda.
     */
    private void atualizarTabelaItens() {
        modeloTabelaItens.setRowCount(0);
        
        for (ItemVenda item : itensVenda) {
            Object[] row = {
                item.equipamento.getId(),
                item.equipamento.getMarca(),
                item.equipamento.getClass().getSimpleName(),
                String.format("%.2f MT", item.equipamento.getPreco()),
                item.quantidade,
                String.format("%.2f MT", item.subtotal)
            };
            modeloTabelaItens.addRow(row);
        }
    }
    
    /**
     * Calcula o total da venda.
     */
    private void calcularTotal() {
        totalVenda = 0.0;
        for (ItemVenda item : itensVenda) {
            totalVenda += item.subtotal;
        }
        lblTotalVenda.setText("Total da Venda: " + String.format("%.2f MT", totalVenda));
    }
}

