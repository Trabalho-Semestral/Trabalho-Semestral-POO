 package view;

import controller.SistemaController;
import model.abstractas.Equipamento;
import model.concretas.Computador;
import model.concretas.Periferico;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class GerirEquipamentosView extends JPanel {

    private SistemaController controller;

    private JTextField txtID;
    private JComboBox<String> cmbTipoEquipamento;
    private JTextField txtMarca;
    private JTextField txtPreco;
    private JTextField txtQuantidade;
    private JComboBox<Equipamento.EstadoEquipamento> cmbEstado;
    private JTextField txtFotoPath;

    private JTextField txtProcessador;
    private JTextField txtMemoriaRAM;
    private JTextField txtArmazenamento;
    private JTextField txtPlacaGrafica;

    private JTextField txtTipoPeriferico;

    private JPanel panelComputador;
    private JPanel panelPeriferico;
    private JPanel panelFoto;
    private JLabel lblFotoPreview;

    private JTable tabelaEquipamentos;
    private DefaultTableModel modeloTabela;

    private JButton btnCadastrar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnLimpar;
    private JButton btnVoltar;
    private JButton btnSelecionarFoto;

    public GerirEquipamentosView(SistemaController controller) {
        this.controller = controller;
        initComponents();
        setupLayout();
        setupEvents();
        carregarEquipamentos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_COLOR);

        txtID = UITheme.createStyledTextField();
        txtID.setEditable(false);
        txtID.setBackground(UITheme.SECONDARY_LIGHT);

        cmbTipoEquipamento = UITheme.createStyledComboBox(new String[]{"Computador", "Periférico"});
        txtMarca = UITheme.createStyledTextField();
        txtPreco = UITheme.createStyledTextField();
        txtQuantidade = UITheme.createStyledTextField();
        cmbEstado = UITheme.createStyledComboBox(Equipamento.EstadoEquipamento.values());
        txtFotoPath = UITheme.createStyledTextField();
        txtFotoPath.setEditable(false);

        txtProcessador = UITheme.createStyledTextField();
        txtMemoriaRAM = UITheme.createStyledTextField();
        txtArmazenamento = UITheme.createStyledTextField();
        txtPlacaGrafica = UITheme.createStyledTextField();

        txtTipoPeriferico = UITheme.createStyledTextField();

        btnCadastrar = UITheme.createPrimaryButton("➕ Cadastrar");
        btnEditar = UITheme.createPrimaryButton("✏️ Editar");
        btnRemover = UITheme.createDangerButton("🗑️ Remover");
        btnLimpar = UITheme.createSecondaryButton("🧹 Limpar");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnSelecionarFoto = UITheme.createSecondaryButton("📷 Selecionar Foto");

        String[] colunas = {"ID", "Tipo", "Marca", "Preço", "Quantidade", "Estado"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEquipamentos = new JTable(modeloTabela);
        tabelaEquipamentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaEquipamentos.setFont(UITheme.FONT_BODY);
        tabelaEquipamentos.getTableHeader().setFont(UITheme.FONT_SUBHEADING);
        tabelaEquipamentos.setRowHeight(30);
        tabelaEquipamentos.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        tabelaEquipamentos.setSelectionForeground(UITheme.TEXT_PRIMARY);

        lblFotoPreview = new JLabel("Nenhuma foto selecionada");
        lblFotoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblFotoPreview.setVerticalAlignment(SwingConstants.CENTER);
        lblFotoPreview.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 2));
        lblFotoPreview.setBackground(UITheme.CARD_BACKGROUND);
        lblFotoPreview.setOpaque(true);
        lblFotoPreview.setPreferredSize(new Dimension(200, 150));

        criarPaineisEspecificos();
    }

    private void criarPaineisEspecificos() {
        panelComputador = new JPanel(new GridBagLayout());
        panelComputador.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelComputador.add(UITheme.createBodyLabel("Processador:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelComputador.add(txtProcessador, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelComputador.add(UITheme.createBodyLabel("Memória RAM:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelComputador.add(txtMemoriaRAM, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelComputador.add(UITheme.createBodyLabel("Armazenamento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelComputador.add(txtArmazenamento, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panelComputador.add(UITheme.createBodyLabel("Placa Gráfica:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelComputador.add(txtPlacaGrafica, gbc);

        panelPeriferico = new JPanel(new GridBagLayout());
        panelPeriferico.setBackground(UITheme.CARD_BACKGROUND);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelPeriferico.add(UITheme.createBodyLabel("Tipo de Periférico:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelPeriferico.add(txtTipoPeriferico, gbc);
    }

    private void setupLayout() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        JLabel lblTitulo = UITheme.createHeadingLabel("💻 Gestão de Equipamentos");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        topPanel.add(lblTitulo, BorderLayout.CENTER);

        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        voltarPanel.add(btnVoltar);
        topPanel.add(voltarPanel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(600, 0));

        JPanel formPanel = criarPainelFormulario();
        leftPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = criarPainelBotoes();
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel rightPanel = criarPainelFoto();
        rightPanel.setPreferredSize(new Dimension(250, 0));

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        JPanel tablePanel = criarPainelTabela();
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblFormTitulo = UITheme.createSubtitleLabel("Dados do Equipamento");
        lblFormTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblFormTitulo, BorderLayout.NORTH);

        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(UITheme.CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formContent.add(UITheme.createBodyLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formContent.add(txtID, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formContent.add(UITheme.createBodyLabel("Tipo:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formContent.add(cmbTipoEquipamento, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formContent.add(UITheme.createBodyLabel("Marca:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formContent.add(txtMarca, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formContent.add(UITheme.createBodyLabel("Preço:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formContent.add(txtPreco, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formContent.add(UITheme.createBodyLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formContent.add(txtQuantidade, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formContent.add(UITheme.createBodyLabel("Estado:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formContent.add(cmbEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formContent.add(UITheme.createBodyLabel("Foto:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formContent.add(txtFotoPath, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        formContent.add(btnSelecionarFoto, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;

        JPanel especificacoesPanel = new JPanel(new CardLayout());
        especificacoesPanel.setBorder(BorderFactory.createTitledBorder("Especificações"));
        especificacoesPanel.add(panelComputador, "Computador");
        especificacoesPanel.add(panelPeriferico, "Periférico");
        formContent.add(especificacoesPanel, gbc);

        panel.add(formContent, BorderLayout.CENTER);
        JScrollPane scroll = new JScrollPane(formContent);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(500, 250));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelFoto() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = UITheme.createSubtitleLabel("Foto do Equipamento");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        panel.add(lblFotoPreview, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(UITheme.CARD_BACKGROUND);

        btnCadastrar.setPreferredSize(new Dimension(100, 28));
        btnEditar.setPreferredSize(new Dimension(100, 28));
        btnRemover.setPreferredSize(new Dimension(100, 28));
        btnLimpar.setPreferredSize(new Dimension(100, 28));

        panel.add(btnCadastrar);
        panel.add(btnEditar);
        panel.add(btnRemover);
        panel.add(btnLimpar);

        return panel;
    }


    private JPanel criarPainelTabela() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 300));

        JLabel lblTabelaTitulo = UITheme.createSubtitleLabel("Equipamentos Cadastrados");
        lblTabelaTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTabelaTitulo, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tabelaEquipamentos);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT, 1));
        scrollPane.getViewport().setBackground(UITheme.CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupEvents() {
        btnCadastrar.addActionListener(e -> cadastrarEquipamento());
        btnEditar.addActionListener(e -> editarEquipamento());
        btnRemover.addActionListener(e -> removerEquipamento());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
        btnSelecionarFoto.addActionListener(e -> selecionarFoto());

        cmbTipoEquipamento.addActionListener(e -> alterarTipoEquipamento());

        tabelaEquipamentos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarEquipamentoSelecionado();
            }
        });
    }

    private void selecionarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagens", "jpg", "jpeg", "png", "gif", "bmp"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtFotoPath.setText(selectedFile.getAbsolutePath());
            carregarImagemPreview(selectedFile.getAbsolutePath());
        }
    }

    private void carregarImagemPreview(String caminhoImagem) {
        try {
            ImageIcon icon = new ImageIcon(caminhoImagem);
            Image img = icon.getImage().getScaledInstance(180, 130, Image.SCALE_SMOOTH);
            lblFotoPreview.setIcon(new ImageIcon(img));
            lblFotoPreview.setText("");
        } catch (Exception e) {
            lblFotoPreview.setIcon(null);
            lblFotoPreview.setText("Erro ao carregar imagem");
        }
    }

    private void alterarTipoEquipamento() {
        String tipoSelecionado = (String) cmbTipoEquipamento.getSelectedItem();
        CardLayout cl = (CardLayout) panelComputador.getParent().getLayout();
        cl.show(panelComputador.getParent(), tipoSelecionado);
    }

    private void cadastrarEquipamento() {
        try {
            Equipamento equipamento = criarEquipamentoFromForm();
            if (equipamento != null && controller.adicionarEquipamento(equipamento)) {
                JOptionPane.showMessageDialog(this, "Equipamento cadastrado com sucesso!");
                limparFormulario();
                carregarEquipamentos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar equipamento. Verifique os dados.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void editarEquipamento() {
        int selectedRow = tabelaEquipamentos.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                Equipamento equipamentoAntigo = controller.getEquipamentos().get(selectedRow);
                Equipamento equipamentoNovo = criarEquipamentoFromForm();

                if (equipamentoNovo != null) {
                    equipamentoNovo.setId(equipamentoAntigo.getId());
                    if (controller.atualizarEquipamento(equipamentoAntigo, equipamentoNovo)) {
                        JOptionPane.showMessageDialog(this, "Equipamento atualizado com sucesso!");
                        limparFormulario();
                        carregarEquipamentos();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao atualizar equipamento.");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para editar.");
        }
    }

    private void removerEquipamento() {
        int selectedRow = tabelaEquipamentos.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente remover este equipamento?",
                    "Confirmar Remoção",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Equipamento equipamento = controller.getEquipamentos().get(selectedRow);
                    if (controller.removerEquipamento(equipamento)) {
                        JOptionPane.showMessageDialog(this, "Equipamento removido com sucesso!");
                        limparFormulario();
                        carregarEquipamentos();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro ao remover equipamento.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para remover.");
        }
    }

    private void limparFormulario() {
        txtID.setText("");
        txtMarca.setText("");
        txtPreco.setText("");
        txtQuantidade.setText("");
        txtFotoPath.setText("");
        txtProcessador.setText("");
        txtMemoriaRAM.setText("");
        txtArmazenamento.setText("");
        txtPlacaGrafica.setText("");
        txtTipoPeriferico.setText("");

        cmbTipoEquipamento.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);

        lblFotoPreview.setIcon(null);
        lblFotoPreview.setText("Nenhuma foto selecionada");

        tabelaEquipamentos.clearSelection();
    }

    private void voltarMenuPrincipal() {
        controller.getCardLayoutManager().showPanel("MenuAdministrador");
    }

    private void carregarEquipamentos() {
        modeloTabela.setRowCount(0);
        List<Equipamento> equipamentos = controller.getEquipamentos();

        for (Equipamento eq : equipamentos) {
            String tipo = eq instanceof Computador ? "Computador" : "Periférico";
            Object[] row = {
                    eq.getId(),
                    tipo,
                    eq.getMarca(),
                    String.format("%.2f MT", eq.getPreco()),
                    eq.getQuantidadeEstoque(),
                    eq.getEstado()
            };
            modeloTabela.addRow(row);
        }
    }

    private void carregarEquipamentoSelecionado() {
        int selectedRow = tabelaEquipamentos.getSelectedRow();
        if (selectedRow >= 0) {
            Equipamento equipamento = controller.getEquipamentos().get(selectedRow);

            txtID.setText(equipamento.getId());
            txtMarca.setText(equipamento.getMarca());
            txtPreco.setText(String.valueOf(equipamento.getPreco()));
            txtQuantidade.setText(String.valueOf(equipamento.getQuantidadeEstoque()));
            cmbEstado.setSelectedItem(equipamento.getEstado());

            if (equipamento instanceof Computador) {
                cmbTipoEquipamento.setSelectedItem("Computador");
                Computador comp = (Computador) equipamento;
                txtProcessador.setText(comp.getProcessador());
                txtMemoriaRAM.setText(comp.getMemoriaRAM());
                txtArmazenamento.setText(comp.getArmazenamento());
                txtPlacaGrafica.setText(comp.getPlacaGrafica());
            } else if (equipamento instanceof Periferico) {
                cmbTipoEquipamento.setSelectedItem("Periférico");
                Periferico per = (Periferico) equipamento;
                txtTipoPeriferico.setText(per.getTipo());
            }

            alterarTipoEquipamento();
        }
    }

    private Equipamento criarEquipamentoFromForm() {
        try {
            String marca = txtMarca.getText().trim();
            double preco = Double.parseDouble(txtPreco.getText().trim());
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            Equipamento.EstadoEquipamento estado = (Equipamento.EstadoEquipamento) cmbEstado.getSelectedItem();
            String tipoEquipamento = (String) cmbTipoEquipamento.getSelectedItem();

            if (marca.isEmpty()) {
                throw new IllegalArgumentException("Marca é obrigatória");
            }

            if ("Computador".equals(tipoEquipamento)) {
                String processador = txtProcessador.getText().trim();
                String memoriaRAM = txtMemoriaRAM.getText().trim();
                String armazenamento = txtArmazenamento.getText().trim();
                String placaGrafica = txtPlacaGrafica.getText().trim();

                if (processador.isEmpty() || memoriaRAM.isEmpty() || armazenamento.isEmpty() || placaGrafica.isEmpty()) {
                    throw new IllegalArgumentException("Todos os campos do computador são obrigatórios");
                }

                return new Computador(marca, preco, quantidade, estado, "", processador, memoriaRAM, armazenamento, placaGrafica);
            } else {
                String tipoPeriferico = txtTipoPeriferico.getText().trim();

                if (tipoPeriferico.isEmpty()) {
                    throw new IllegalArgumentException("Tipo de periférico é obrigatório");
                }

                return new Periferico(marca, preco, quantidade, estado, "", tipoPeriferico);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Preço e quantidade devem ser números válidos");
        }
    }
}

