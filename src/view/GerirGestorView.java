package view;

import controller.SistemaController;
import model.concretas.Gestor;
import util.BCryptHasher;
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

public class GerirGestorView extends JPanel {

    // --- CONSTANTE PARA O DIRETÓRIO DE FOTOS ---
    private static final String FOTOS_GESTORES_PATH = "resources/fotos/gestores/";

    private SistemaController controller;

    // Campos do formulário
    private JTextField txtNome;
    private JTextField txtNrBI;
    private JTextField txtNuit;
    private JTextField txtTelefone;
    private JTextField txtSalario;
    private JPasswordField txtSenha;

    // Componentes da foto
    private JLabel lblFoto;
    private String caminhoFotoAtual;

    // Tabela
    private JTable tabelaGestores;
    private DefaultTableModel modeloTabela;

    // Botões
    private JButton btnAdicionarFoto;
    private JButton btnRemoverFoto;
    private JButton btnCadastrar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnVoltar;

    public GerirGestorView(SistemaController controller) {
        this.controller = controller;
        new File(FOTOS_GESTORES_PATH).mkdirs(); // Garante que o diretório de fotos exista
        initComponents();
        setupLayout();
        setupEvents();
        carregarGestores();
    }

    private void initComponents() {
        setBackground(UITheme.BACKGROUND_COLOR);

        // Campos de texto
        txtNome = new JTextField();
        txtNrBI = new JTextField();
        txtNuit = new JTextField();
        txtTelefone = new JTextField();
        txtSalario = new JTextField();
        txtSenha = new JPasswordField();

        styleTextField(txtNome, "Nome Completo");
        styleTextField(txtNrBI, "Nº do BI");
        styleTextField(txtNuit, "NUIT");
        styleTextField(txtTelefone, "Telefone");
        styleTextField(txtSalario, "Salário");
        styleTextField(txtSenha, "Senha");

        // Componentes da foto
        lblFoto = new JLabel("Sem Foto", SwingConstants.CENTER);
        lblFoto.setFont(UITheme.FONT_SUBHEADING);
        lblFoto.setForeground(UITheme.TEXT_SECONDARY);
        lblFoto.setOpaque(true);
        lblFoto.setBackground(UITheme.CARD_BACKGROUND);
        lblFoto.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));

        // Botões de Ação
        btnAdicionarFoto = UITheme.createPrimaryButton("Adicionar Foto");
        btnRemoverFoto = UITheme.createSecondaryButton("Remover Foto");
        btnCadastrar = UITheme.createSuccessButton("Cadastrar");
        btnEditar = UITheme.createPrimaryButton("Editar");
        btnRemover = UITheme.createDangerButton("Remover");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnVoltar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));

        // Tabela
        String[] colunas = {"ID", "Nome", "Nº BI", "NUIT", "Telefone", "Salário"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaGestores = new JTable(modeloTabela);
        UITheme.applyTableStyle(tabelaGestores);
        tabelaGestores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        // --- Top bar ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topPanel.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));

        JLabel lblTitulo = UITheme.createHeadingLabel("👑 Gestão de Gestores");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        topPanel.add(lblTitulo, BorderLayout.CENTER);

        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        voltarPanel.add(btnVoltar);
        topPanel.add(voltarPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- PAINEL PRINCIPAL ---
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- PAINEL SUPERIOR (foto, formulário, botões) ---
        JPanel topContentPanel = new JPanel(new BorderLayout(15, 15));
        topContentPanel.setBackground(UITheme.BACKGROUND_COLOR);

        JPanel fotoPainelWrapper = new JPanel(new BorderLayout(10, 10));
        fotoPainelWrapper.setBackground(UITheme.BACKGROUND_COLOR);
        lblFoto.setPreferredSize(new Dimension(250, 250));
        fotoPainelWrapper.add(lblFoto, BorderLayout.CENTER);

        JPanel fotoBotoesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        fotoBotoesPanel.setBackground(UITheme.BACKGROUND_COLOR);
        fotoBotoesPanel.add(btnAdicionarFoto);
        fotoBotoesPanel.add(btnRemoverFoto);
        fotoPainelWrapper.add(fotoBotoesPanel, BorderLayout.SOUTH);
        topContentPanel.add(fotoPainelWrapper, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(UITheme.CARD_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.add(txtNome);
        formPanel.add(txtNrBI);
        formPanel.add(txtNuit);
        formPanel.add(txtTelefone);
        formPanel.add(txtSalario);
        formPanel.add(txtSenha);
        topContentPanel.add(formPanel, BorderLayout.CENTER);

        JPanel acoesPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        acoesPanel.setBackground(UITheme.BACKGROUND_COLOR);
        acoesPanel.add(btnCadastrar);
        acoesPanel.add(btnEditar);
        acoesPanel.add(btnRemover);
        topContentPanel.add(acoesPanel, BorderLayout.EAST);

        // --- PAINEL INFERIOR (tabela) ---
        JPanel tabelaPanel = new JPanel(new BorderLayout());
        tabelaPanel.setBorder(BorderFactory.createTitledBorder("Gestores Cadastrados"));
        tabelaPanel.setBackground(UITheme.CARD_BACKGROUND);
        JScrollPane scroll = new JScrollPane(tabelaGestores);
        tabelaPanel.add(scroll, BorderLayout.CENTER);

        mainPanel.add(topContentPanel, BorderLayout.NORTH);
        mainPanel.add(tabelaPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEvents() {
        btnAdicionarFoto.addActionListener(e -> adicionarFoto());
        btnRemoverFoto.addActionListener(e -> removerFoto());
        btnCadastrar.addActionListener(e -> cadastrarGestor());
        btnEditar.addActionListener(e -> editarGestor());
        btnRemover.addActionListener(e -> removerGestor());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
        tabelaGestores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarGestorSelecionado();
        });
    }

    private void adicionarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione uma foto");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagens (JPG, PNG, GIF)", "jpg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivoSelecionado = fileChooser.getSelectedFile();
            try {
                String extensao = arquivoSelecionado.getName().substring(arquivoSelecionado.getName().lastIndexOf("."));
                String novoNome = UUID.randomUUID().toString() + extensao;
                File destino = new File(FOTOS_GESTORES_PATH + novoNome);
                Files.copy(arquivoSelecionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                this.caminhoFotoAtual = destino.getPath();
                exibirImagem(this.caminhoFotoAtual);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar a foto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

    private void cadastrarGestor() {
        try {
            Gestor gestor = criarGestorFromForm(false);
            if (gestor != null) {
                gestor.setFotoPath(this.caminhoFotoAtual);
                if (controller.adicionarGestor(gestor)) {
                    JOptionPane.showMessageDialog(this, "Gestor cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    carregarGestores();
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível cadastrar o gestor.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarGestor() {
        int row = tabelaGestores.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um gestor para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String gestorId = (String) modeloTabela.getValueAt(row, 0);
        Gestor gestorAntigo = controller.getGestores().stream().filter(g -> g.getId().equals(gestorId)).findFirst().orElse(null);

        Gestor gestorNovo = criarGestorFromForm(true);
        if (gestorAntigo != null && gestorNovo != null) {
            gestorNovo.setId(gestorAntigo.getId());
            gestorNovo.setFotoPath(this.caminhoFotoAtual);

            if (gestorNovo.getSenha().isEmpty()) {
                gestorNovo.setSenha(gestorAntigo.getSenha());
            } else {
                gestorNovo.setSenha(BCryptHasher.hashPassword(gestorNovo.getSenha()));
            }

            if (controller.atualizarGestor(gestorAntigo, gestorNovo)) {
                JOptionPane.showMessageDialog(this, "Gestor atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarGestores();
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível atualizar o gestor.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removerGestor() {
        int row = tabelaGestores.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um gestor para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String gestorId = (String) modeloTabela.getValueAt(row, 0);
        Gestor gestor = controller.getGestores().stream().filter(g -> g.getId().equals(gestorId)).findFirst().orElse(null);

        if (gestor != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente remover '" + gestor.getNome() + "'?", "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.removerGestor(gestor)) {
                    if (gestor.getFotoPath() != null) {
                        try { Files.deleteIfExists(Paths.get(gestor.getFotoPath())); } catch (IOException e) { /* Ignorar */ }
                    }
                    JOptionPane.showMessageDialog(this, "Gestor removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarGestores();
                    limparFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível remover o gestor.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private Gestor criarGestorFromForm(boolean isEdit) {
        try {
            String nome = txtNome.getText().trim();
            String nrBI = txtNrBI.getText().trim();
            String nuit = txtNuit.getText().trim();
            String telefone = txtTelefone.getText().trim();
            double salario = Double.parseDouble(txtSalario.getText().trim());
            String senha = new String(txtSenha.getPassword());

            if (!Validador.validarCampoObrigatorio(nome)) throw new IllegalArgumentException("'Nome' é obrigatório.");
            if (!Validador.validarBI(nrBI)) throw new IllegalArgumentException("'Nr. BI' inválido.");
            if (!Validador.validarTelefone(telefone)) throw new IllegalArgumentException("'Telefone' inválido.");
            if (!Validador.validarValorPositivo(salario)) throw new IllegalArgumentException("'Salário' deve ser positivo.");
            if (!isEdit && !Validador.validarCampoObrigatorio(senha)) throw new IllegalArgumentException("'Senha' é obrigatória.");

            return new Gestor(nome, nrBI, nuit, telefone, salario, senha);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "'Salário' deve ser um valor numérico.", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void carregarGestores() {
        modeloTabela.setRowCount(0);
        List<Gestor> gestores = controller.getGestores();
        for (Gestor gestor : gestores) {
            modeloTabela.addRow(new Object[]{
                    gestor.getId(), gestor.getNome(), gestor.getNrBI(),
                    gestor.getNuit(), gestor.getTelefone(), String.format("%.2f MT", gestor.getSalario())
            });
        }
    }

    private void carregarGestorSelecionado() {
        int row = tabelaGestores.getSelectedRow();
        if (row >= 0) {
            String gestorId = (String) modeloTabela.getValueAt(row, 0);
            Gestor g = controller.getGestores().stream().filter(gst -> gst.getId().equals(gestorId)).findFirst().orElse(null);
            if (g != null) {
                txtNome.setText(g.getNome());
                txtNrBI.setText(g.getNrBI());
                txtNuit.setText(g.getNuit());
                txtTelefone.setText(g.getTelefone());
                txtSalario.setText(String.valueOf(g.getSalario()));
                txtSenha.setText("");
                styleTextField(txtSenha, "Nova Senha (opcional)");
                this.caminhoFotoAtual = g.getFotoPath();
                exibirImagem(this.caminhoFotoAtual);
            }
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtNrBI.setText("");
        txtNuit.setText("");
        txtTelefone.setText("");
        txtSalario.setText("");
        txtSenha.setText("");
        styleTextField(txtSenha, "Senha");
        tabelaGestores.clearSelection();
        removerFoto();
    }

    private void voltarMenuPrincipal() {
        controller.getCardLayoutManager().showPanel("MenuAdministrador");
    }
}
