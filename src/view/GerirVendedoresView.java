package view;

import controller.SistemaController;
import model.concretas.Vendedor;
import util.Validador;
import util.UITheme;

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

public class GerirVendedoresView extends JPanel {

    // --- CONSTANTE PARA O DIRETÓRIO DE FOTOS ---
    private static final String FOTOS_VENDEDORES_PATH = "resources/fotos/vendedores/";

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
    private JTable tabelaVendedores;
    private DefaultTableModel modeloTabela;

    // Botões
    private JButton btnAdicionarFoto;
    private JButton btnRemoverFoto;
    private JButton btnCadastrar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnVoltar;

    public GerirVendedoresView(SistemaController controller) {
        this.controller = controller;
        // Garante que o diretório de fotos exista
        new File(FOTOS_VENDEDORES_PATH).mkdirs();
        initComponents();
        setupLayout();
        setupEvents();
        carregarVendedores();
    }

    private void initComponents() {
        setBackground(UITheme.BACKGROUND_COLOR);

        // Campos de texto com bordas de título
        txtNome = new JTextField();
        txtNrBI = new JTextField();
        txtNuit = new JTextField();
        txtTelefone = new JTextField();
        txtSalario = new JTextField();
        txtSenha = new JPasswordField();

        styleTextField(txtNome, "Nome");
        styleTextField(txtNrBI, "Nr. BI");
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

        // Botões de Ação – sempre visíveis e ativos
        btnAdicionarFoto = UITheme.createPrimaryButton("Adicionar Foto");

        btnRemoverFoto = UITheme.createPrimaryButton("Remover Foto");
        btnRemoverFoto.setEnabled(true);
        btnRemoverFoto.setVisible(true);
// Botões
        btnAdicionarFoto = UITheme.createPrimaryButton("Adicionar Foto");
        btnRemoverFoto = UITheme.createSecondaryButton("Remover Foto");
        btnCadastrar = UITheme.createSuccessButton("Cadastrar");
        btnEditar = UITheme.createPrimaryButton("Editar");
        btnRemover = UITheme.createDangerButton("Remover");
        btnVoltar = UITheme.createSecondaryButton("⬅️ Voltar");
        btnVoltar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        // Tabela
        String[] colunas = {"ID", "Nome", "BI", "NUIT", "Telefone", "Salário"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaVendedores = new JTable(modeloTabela);
        tabelaVendedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        JLabel lblTitulo = UITheme.createHeadingLabel("👥 Gestão de Vendedores");
        lblTitulo.setForeground(UITheme.TEXT_WHITE);
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
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

        // Painel da Foto (com botões)
        JPanel fotoPainelWrapper = new JPanel(new BorderLayout(10,10));
        fotoPainelWrapper.setBackground(UITheme.BACKGROUND_COLOR);
        lblFoto.setPreferredSize(new Dimension(250, 250));
        fotoPainelWrapper.add(lblFoto, BorderLayout.CENTER);

        JPanel fotoBotoesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        fotoBotoesPanel.setBackground(UITheme.BACKGROUND_COLOR);
        fotoBotoesPanel.add(btnAdicionarFoto);
        fotoBotoesPanel.add(btnRemoverFoto);
        fotoPainelWrapper.add(fotoBotoesPanel, BorderLayout.SOUTH);

        topContentPanel.add(fotoPainelWrapper, BorderLayout.WEST);

        // Formulário ao centro
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

        // Botões de Ação Principais à direita
        JPanel acoesPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        acoesPanel.setBackground(UITheme.BACKGROUND_COLOR);
        acoesPanel.add(btnCadastrar);
        acoesPanel.add(btnEditar);
        acoesPanel.add(btnRemover);

        topContentPanel.add(acoesPanel, BorderLayout.EAST);

        // --- PAINEL INFERIOR (tabela) ---
        JPanel tabelaPanel = new JPanel(new BorderLayout());
        tabelaPanel.setBorder(BorderFactory.createTitledBorder("Vendedores Cadastrados"));
        tabelaPanel.setBackground(UITheme.CARD_BACKGROUND);
        JScrollPane scroll = new JScrollPane(tabelaVendedores);
        tabelaPanel.add(scroll, BorderLayout.CENTER);

        mainPanel.add(topContentPanel, BorderLayout.NORTH);
        mainPanel.add(tabelaPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }


    private void setupEvents() {
        btnAdicionarFoto.addActionListener(e -> adicionarFoto());
        btnRemoverFoto.addActionListener(e -> removerFoto());
        btnCadastrar.addActionListener(e -> cadastrarVendedor());
        btnEditar.addActionListener(e -> editarVendedor());
        btnRemover.addActionListener(e -> removerVendedor());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());

        tabelaVendedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarVendedorSelecionado();
        });
    }

    private void adicionarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione uma foto");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagens (JPG, PNG, GIF)", "jpg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivoSelecionado = fileChooser.getSelectedFile();
            try {
                // Cria um nome de arquivo único para evitar conflitos
                String extensao = arquivoSelecionado.getName().substring(arquivoSelecionado.getName().lastIndexOf("."));
                String novoNome = UUID.randomUUID().toString() + extensao;
                File destino = new File(FOTOS_VENDEDORES_PATH + novoNome);

                // Copia o arquivo para o diretório de fotos do projeto
                Files.copy(arquivoSelecionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Armazena o caminho relativo e exibe a imagem
                this.caminhoFotoAtual = destino.getPath();
                exibirImagem(this.caminhoFotoAtual);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar a foto: " + e.getMessage(), "Erro de Arquivo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removerFoto() {
        this.caminhoFotoAtual = null;
        exibirImagem(null); // Limpa a imagem exibida
    }

    private void exibirImagem(String caminho) {
        if (caminho != null && !caminho.isEmpty() && new File(caminho).exists()) {
            ImageIcon icon = new ImageIcon(caminho);
            Image img = icon.getImage();
            // Redimensiona a imagem para caber no JLabel, mantendo a proporção
            Image newImg = img.getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH);
            lblFoto.setIcon(new ImageIcon(newImg));
            lblFoto.setText("");
        } else {
            lblFoto.setIcon(null);
            lblFoto.setText("Sem Foto");
        }
    }

    private void cadastrarVendedor() {
        try {
            Vendedor vendedor = criarVendedorFromForm(false);
            if (vendedor != null) {
                vendedor.setFotoPath(this.caminhoFotoAtual); // Associa a foto ao vendedor
                if (controller.adicionarVendedor(vendedor)) {
                    JOptionPane.showMessageDialog(this, "Vendedor cadastrado com sucesso!");
                    limparFormulario();
                    carregarVendedores();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarVendedor() {
        int row = tabelaVendedores.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um vendedor na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vendedorId = (String) modeloTabela.getValueAt(row, 0);
        Vendedor vendedorAntigo = controller.getVendedores().stream()
                .filter(v -> v.getId().equals(vendedorId)).findFirst().orElse(null);

        Vendedor vendedorNovo = criarVendedorFromForm(true);
        if (vendedorAntigo != null && vendedorNovo != null) {
            vendedorNovo.setId(vendedorAntigo.getId());
            vendedorNovo.setFotoPath(this.caminhoFotoAtual); // Atualiza o caminho da foto

            if (vendedorNovo.getSenha() == null || vendedorNovo.getSenha().isEmpty()) {
                vendedorNovo.setSenha(vendedorAntigo.getSenha());
            } else {
                vendedorNovo.setSenha(util.BCryptHasher.hashPassword(vendedorNovo.getSenha()));
            }

            if (controller.atualizarVendedor(vendedorAntigo, vendedorNovo)) {
                JOptionPane.showMessageDialog(this, "Vendedor atualizado com sucesso!");
                carregarVendedores();
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível atualizar o vendedor.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removerVendedor() {
        int row = tabelaVendedores.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um vendedor na tabela para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vendedorId = (String) modeloTabela.getValueAt(row, 0);
        Vendedor v = controller.getVendedores().stream()
                .filter(vend -> vend.getId().equals(vendedorId)).findFirst().orElse(null);

        if (v != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente remover o vendedor '" + v.getNome() + "'?", "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.removerVendedor(v)) {
                    // Opcional: remover o arquivo da foto
                    if(v.getFotoPath() != null) {
                        try { Files.deleteIfExists(Paths.get(v.getFotoPath())); } catch (IOException e) { /* Ignorar falha na deleção */ }
                    }
                    JOptionPane.showMessageDialog(this, "Vendedor removido com sucesso!");
                    carregarVendedores();
                    limparFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível remover o vendedor.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private Vendedor criarVendedorFromForm(boolean isEdit) {
        try {
            String nome = txtNome.getText().trim();
            String nrBI = txtNrBI.getText().trim();
            String nuit = txtNuit.getText().trim();
            String telefone = txtTelefone.getText().trim();
            double salario = Double.parseDouble(txtSalario.getText().trim());
            String senha = new String(txtSenha.getPassword());

            if (!Validador.validarCampoObrigatorio(nome)) throw new IllegalArgumentException("O campo 'Nome' é obrigatório.");
            if (!Validador.validarBI(nrBI)) throw new IllegalArgumentException("O formato do 'Nr. BI' é inválido.");
            if (!Validador.validarTelefone(telefone)) throw new IllegalArgumentException("O formato do 'Telefone' é inválido.");
            if (!Validador.validarValorPositivo(salario)) throw new IllegalArgumentException("O 'Salário' deve ser um valor positivo.");
            if (!isEdit && !Validador.validarCampoObrigatorio(senha)) throw new IllegalArgumentException("A 'Senha' é obrigatória para novos cadastros.");

            return new Vendedor(nome, nrBI, nuit, telefone, salario, senha);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro: O campo 'Salário' deve ser um valor numérico.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro de Validação: " + e.getMessage(), "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void carregarVendedores() {
        modeloTabela.setRowCount(0);
        List<Vendedor> vendedores = controller.getVendedores();
        for (Vendedor v : vendedores) {
            modeloTabela.addRow(new Object[]{
                    v.getId(), v.getNome(), v.getNrBI(), v.getNuit(),
                    v.getTelefone(), String.format("%.2f MT", v.getSalario())
            });
        }
    }

    private void carregarVendedorSelecionado() {
        int row = tabelaVendedores.getSelectedRow();
        if (row >= 0) {
            String vendedorId = (String) modeloTabela.getValueAt(row, 0);
            Vendedor v = controller.getVendedores().stream()
                    .filter(vend -> vend.getId().equals(vendedorId)).findFirst().orElse(null);

            if (v != null) {
                txtNome.setText(v.getNome());
                txtNrBI.setText(v.getNrBI());
                txtNuit.setText(v.getNuit());
                txtTelefone.setText(v.getTelefone());
                txtSalario.setText(String.valueOf(v.getSalario()));
                txtSenha.setText("");
                styleTextField(txtSenha, "Nova Senha (opcional)");

                // Carrega a foto
                this.caminhoFotoAtual = v.getFotoPath();
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
        tabelaVendedores.clearSelection();

        // Limpa a foto
        removerFoto();
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
