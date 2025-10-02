package view;

import controller.SistemaController;
import model.abstractas.Equipamento;
import model.concretas.Computador;
import model.concretas.Periferico;
import util.UITheme;
import util.Validador;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public class GerirEquipamentosView extends JPanel {

    private static final String FOTOS_EQUIPAMENTOS_PATH = "resources/fotos/equipamentos/";

    private SistemaController controller;

    // Campos do formulário
    private JComboBox<String> cmbTipoEquipamento;
    private JTextField txtMarca;
    private JTextField txtPreco;
    private JTextField txtQuantidade;
    private JComboBox<Equipamento.EstadoEquipamento> cmbEstado;

    // Campos específicos
    private JTextField txtProcessador;
    private JTextField txtMemoriaRAM;
    private JTextField txtArmazenamento;
    private JTextField txtPlacaGrafica;
    private JTextField txtTipoPeriferico;
    private JPanel especificacoesPanel; // Painel com CardLayout

    // Componentes da foto
    private JLabel lblFoto;
    private String caminhoFotoAtual;

    // Tabela
    private JTable tabelaEquipamentos;
    private DefaultTableModel modeloTabela;

    // Botões
    private JButton btnAdicionarFoto;
    private JButton btnRemoverFoto;
    private JButton btnCadastrar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnVoltar;

    public GerirEquipamentosView(SistemaController controller) {
        this.controller = controller;
        new File(FOTOS_EQUIPAMENTOS_PATH).mkdirs(); // Cria o diretório de fotos se não existir
        initComponents();
        setupLayout();
        setupEvents();
        carregarEquipamentos();
    }

    private void initComponents() {
        setBackground(UITheme.BACKGROUND_COLOR);

        // Campos do formulário
        cmbTipoEquipamento = new JComboBox<>(new String[]{"Computador", "Periférico"});
        txtMarca = new JTextField();
        txtPreco = new JTextField();
        txtQuantidade = new JTextField();
        cmbEstado = new JComboBox<>(Equipamento.EstadoEquipamento.values());

        // Estilo dos campos comuns
        styleTextField(txtMarca, "Marca");
        styleTextField(txtPreco, "Preço (MT)");
        styleTextField(txtQuantidade, "Quantidade");

        // Campos específicos de Computador
        txtProcessador = new JTextField();
        txtMemoriaRAM = new JTextField();
        txtArmazenamento = new JTextField();
        txtPlacaGrafica = new JTextField();
        styleTextField(txtProcessador, "Processador");
        styleTextField(txtMemoriaRAM, "Memória RAM");
        styleTextField(txtArmazenamento, "Armazenamento");
        styleTextField(txtPlacaGrafica, "Placa Gráfica");

        // Campos específicos de Periférico
        txtTipoPeriferico = new JTextField();
        styleTextField(txtTipoPeriferico, "Tipo de Periférico");

        // Foto
        lblFoto = new JLabel("Sem Foto", SwingConstants.CENTER);
        lblFoto.setOpaque(true);
        lblFoto.setBackground(UITheme.CARD_BACKGROUND);
        lblFoto.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));
        lblFoto.setFont(UITheme.FONT_SUBHEADING);


        // Botões
        btnAdicionarFoto = UITheme.createPrimaryButton("Adicionar Foto");
        btnRemoverFoto = UITheme.createSecondaryButton("Remover Foto");  // antes era secondary
        btnCadastrar = UITheme.createSuccessButton("Cadastrar");        // antes era success
        btnEditar = UITheme.createPrimaryButton("Editar");
        btnRemover = UITheme.createDangerButton("Remover");            // antes era danger
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
        btnVoltar.setFont(emojiFont);


        // Tabela
        String[] colunas = {"ID", "Tipo", "Marca", "Preço", "Qtd.", "Estado"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaEquipamentos = new JTable(modeloTabela);
       // UITheme.applyTableStyle(tabelaEquipamentos);
    }

    private void styleTextField(JComponent component, String title) {
        component.setFont(UITheme.FONT_BODY);
        Border lineBorder = BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(lineBorder, title,
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        UITheme.FONT_SUBHEADING, UITheme.TEXT_SECONDARY),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        component.setBackground(UITheme.CARD_BACKGROUND);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // --- Top Bar ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        JLabel lblTitulo = UITheme.createHeadingLabel("💻 Gestão de Equipamentos");
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblTitulo.setForeground(Color.WHITE);
        topBar.add(lblTitulo, BorderLayout.CENTER);
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar);
        topBar.add(voltarPanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // --- Painel Principal ---
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // --- Painel Superior (Foto, Formulário, Ações) ---
        JPanel topContentPanel = new JPanel(new BorderLayout(15, 15));
        topContentPanel.setOpaque(false);

        // Painel da Foto (Esquerda)
        JPanel fotoPanel = new JPanel(new BorderLayout(10, 10));
        fotoPanel.setOpaque(false);
        lblFoto.setPreferredSize(new Dimension(250, 250));
        fotoPanel.add(lblFoto, BorderLayout.CENTER);
        JPanel fotoBotoesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        fotoBotoesPanel.setOpaque(false);
        fotoBotoesPanel.add(btnAdicionarFoto);
        fotoBotoesPanel.add(btnRemoverFoto);
        fotoPanel.add(fotoBotoesPanel, BorderLayout.SOUTH);
        topContentPanel.add(fotoPanel, BorderLayout.WEST);

        // Painel do Formulário (Centro)
        JPanel formWrapper = new JPanel(new BorderLayout(10, 10));
        formWrapper.setBackground(UITheme.CARD_BACKGROUND);
        formWrapper.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));

        // Formulário de campos comuns
        JPanel commonFieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        commonFieldsPanel.setOpaque(false);
        commonFieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        commonFieldsPanel.add(cmbTipoEquipamento);
        commonFieldsPanel.add(cmbEstado);
        commonFieldsPanel.add(txtMarca);
        commonFieldsPanel.add(txtPreco);
        commonFieldsPanel.add(txtQuantidade);
        formWrapper.add(commonFieldsPanel, BorderLayout.NORTH);

        // Formulário de campos específicos (com CardLayout)
        JPanel panelComputador = new JPanel(new GridLayout(0, 2, 10, 10));
        panelComputador.setOpaque(false);
        panelComputador.add(txtProcessador);
        panelComputador.add(txtMemoriaRAM);
        panelComputador.add(txtArmazenamento);
        panelComputador.add(txtPlacaGrafica);

        JPanel panelPeriferico = new JPanel(new GridLayout(0, 1, 10, 10));
        panelPeriferico.setOpaque(false);
        panelPeriferico.add(txtTipoPeriferico);

        especificacoesPanel = new JPanel(new CardLayout(10, 10));
        especificacoesPanel.setOpaque(false);
        especificacoesPanel.setBorder(BorderFactory.createTitledBorder("Especificações"));
        especificacoesPanel.add(panelComputador, "Computador");
        especificacoesPanel.add(panelPeriferico, "Periférico");
        formWrapper.add(especificacoesPanel, BorderLayout.CENTER);

        topContentPanel.add(formWrapper, BorderLayout.CENTER);

        // Painel de Ações (Direita)
        JPanel acoesPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        acoesPanel.setOpaque(false);
        acoesPanel.add(btnCadastrar);
        acoesPanel.add(btnEditar);
        acoesPanel.add(btnRemover);
        topContentPanel.add(acoesPanel, BorderLayout.EAST);

        // --- Painel Inferior (Tabela) ---
        JPanel tabelaPanel = new JPanel(new BorderLayout());
        tabelaPanel.setBorder(BorderFactory.createTitledBorder("Equipamentos Cadastrados"));
        tabelaPanel.setBackground(UITheme.CARD_BACKGROUND);
        tabelaPanel.add(new JScrollPane(tabelaEquipamentos), BorderLayout.CENTER);

        mainPanel.add(topContentPanel, BorderLayout.NORTH);
        mainPanel.add(tabelaPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEvents() {
        btnAdicionarFoto.addActionListener(e -> adicionarFoto());
        btnRemoverFoto.addActionListener(e -> removerFoto());
        btnCadastrar.addActionListener(e -> cadastrarEquipamento());
        btnEditar.addActionListener(e -> editarEquipamento());
        btnRemover.addActionListener(e -> removerEquipamento());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());

        cmbTipoEquipamento.addActionListener(e -> {
            CardLayout cl = (CardLayout) especificacoesPanel.getLayout();
            cl.show(especificacoesPanel, (String) cmbTipoEquipamento.getSelectedItem());
        });

        tabelaEquipamentos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarEquipamentoSelecionado();
        });
    }

    private void adicionarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione uma foto para o equipamento");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagens (JPG, PNG)", "jpg", "png"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File arquivo = fileChooser.getSelectedFile();
                String extensao = arquivo.getName().substring(arquivo.getName().lastIndexOf("."));
                String novoNome = UUID.randomUUID().toString() + extensao;
                File destino = new File(FOTOS_EQUIPAMENTOS_PATH + novoNome);

                Files.copy(arquivo.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                this.caminhoFotoAtual = destino.getPath();
                exibirImagem(this.caminhoFotoAtual);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar a foto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removerFoto() {
        this.caminhoFotoAtual = null;
        exibirImagem(null);
    }

    private void exibirImagem(String caminho) {
        if (caminho != null && !caminho.isEmpty() && new File(caminho).exists()) {
            ImageIcon icon = new ImageIcon(caminho);
            Image img = icon.getImage().getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH);
            lblFoto.setIcon(new ImageIcon(img));
            lblFoto.setText("");
        } else {
            lblFoto.setIcon(null);
            lblFoto.setText("Sem Foto");
        }
    }

    private void cadastrarEquipamento() {
        try {
            Equipamento equipamento = criarEquipamentoFromForm();
            if (equipamento != null) {
                equipamento.setFotoPath(this.caminhoFotoAtual);
                if (controller.adicionarEquipamento(equipamento)) {
                    JOptionPane.showMessageDialog(this, "Equipamento cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    carregarEquipamentos();
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao cadastrar o equipamento.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro de validação: " + ex.getMessage(), "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editarEquipamento() {
        int row = tabelaEquipamentos.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloTabela.getValueAt(row, 0);
        Equipamento equipamentoAntigo = controller.getEquipamentos().stream().filter(eq -> eq.getId().equals(id)).findFirst().orElse(null);

        try {
            Equipamento equipamentoNovo = criarEquipamentoFromForm();
            if (equipamentoAntigo != null && equipamentoNovo != null) {
                equipamentoNovo.setId(equipamentoAntigo.getId());
                equipamentoNovo.setFotoPath(this.caminhoFotoAtual);

                if (controller.atualizarEquipamento(equipamentoAntigo, equipamentoNovo)) {
                    JOptionPane.showMessageDialog(this, "Equipamento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    carregarEquipamentos();
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao atualizar o equipamento.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro de validação: " + ex.getMessage(), "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removerEquipamento() {
        int row = tabelaEquipamentos.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloTabela.getValueAt(row, 0);
        Equipamento equipamento = controller.getEquipamentos().stream().filter(eq -> eq.getId().equals(id)).findFirst().orElse(null);

        if (equipamento != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja remover o equipamento '" + equipamento.getMarca() + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.removerEquipamento(equipamento)) {
                    // Tenta apagar a foto associada
                    if (equipamento.getFotoPath() != null) {
                        try { Files.deleteIfExists(Paths.get(equipamento.getFotoPath())); } catch (IOException ex) { /* Ignora falha */ }
                    }
                    JOptionPane.showMessageDialog(this, "Equipamento removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    carregarEquipamentos();
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao remover o equipamento.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private Equipamento criarEquipamentoFromForm() throws IllegalArgumentException {
        try {
            String marca = txtMarca.getText().trim();
            double preco = Double.parseDouble(txtPreco.getText().trim());
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            Equipamento.EstadoEquipamento estado = (Equipamento.EstadoEquipamento) cmbEstado.getSelectedItem();
            String tipoEquipamento = (String) cmbTipoEquipamento.getSelectedItem();

            if (!Validador.validarCampoObrigatorio(marca)) throw new IllegalArgumentException("Marca é obrigatória.");
            if (!Validador.validarValorPositivo(preco)) throw new IllegalArgumentException("Preço deve ser positivo.");
            if (quantidade < 0) throw new IllegalArgumentException("Quantidade não pode ser negativa.");

            if ("Computador".equals(tipoEquipamento)) {
                return new Computador(marca, preco, quantidade, estado, null,
                        txtProcessador.getText().trim(), txtMemoriaRAM.getText().trim(),
                        txtArmazenamento.getText().trim(), txtPlacaGrafica.getText().trim());
            } else {
                return new Periferico(marca, preco, quantidade, estado, null,
                        txtTipoPeriferico.getText().trim());
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Preço e Quantidade devem ser números válidos.");
        }
    }

    private void carregarEquipamentos() {
        modeloTabela.setRowCount(0);
        for (Equipamento eq : controller.getEquipamentos()) {
            modeloTabela.addRow(new Object[]{
                    eq.getId(),
                    eq instanceof Computador ? "Computador" : "Periférico",
                    eq.getMarca(),
                    String.format("%.2f MT", eq.getPreco()),
                    eq.getQuantidadeEstoque(),
                    eq.getEstado()
            });
        }
    }

    private void carregarEquipamentoSelecionado() {
        int row = tabelaEquipamentos.getSelectedRow();
        if (row < 0) return;

        String id = (String) modeloTabela.getValueAt(row, 0);
        Equipamento eq = controller.getEquipamentos().stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);

        if (eq != null) {
            txtMarca.setText(eq.getMarca());
            txtPreco.setText(String.valueOf(eq.getPreco()));
            txtQuantidade.setText(String.valueOf(eq.getQuantidadeEstoque()));
            cmbEstado.setSelectedItem(eq.getEstado());
            this.caminhoFotoAtual = eq.getFotoPath();
            exibirImagem(this.caminhoFotoAtual);

            if (eq instanceof Computador comp) {
                cmbTipoEquipamento.setSelectedItem("Computador");
                txtProcessador.setText(comp.getProcessador());
                txtMemoriaRAM.setText(comp.getMemoriaRAM());
                txtArmazenamento.setText(comp.getArmazenamento());
                txtPlacaGrafica.setText(comp.getPlacaGrafica());
            } else if (eq instanceof Periferico per) {
                cmbTipoEquipamento.setSelectedItem("Periférico");
                txtTipoPeriferico.setText(per.getTipo());
            }
        }
    }

    private void limparFormulario() {
        txtMarca.setText("");
        txtPreco.setText("");
        txtQuantidade.setText("");
        txtProcessador.setText("");
        txtMemoriaRAM.setText("");
        txtArmazenamento.setText("");
        txtPlacaGrafica.setText("");
        txtTipoPeriferico.setText("");
        cmbTipoEquipamento.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        tabelaEquipamentos.clearSelection();
        removerFoto();
    }

    private void voltarMenuPrincipal() {
        String tipoUsuario = controller.getTipoUsuarioLogado();
        String painel = switch (tipoUsuario) {
            case "Gestor" -> "MenuGestor";
            case "Vendedor" -> "MenuVendedor";
            case "Administrador" -> "MenuAdministrador";
            default -> "Login";
        };
        controller.getCardLayoutManager().showPanel(painel);
    }
}
