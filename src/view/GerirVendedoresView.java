package view;

import controller.SistemaController;
import model.concretas.Vendedor;
import util.UITheme;
import util.Validador;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public class GerirVendedoresView extends JPanel {

    private static final String FOTOS_VENDEDORES_PATH = "resources/fotos/vendedores/";

    private SistemaController controller;

    // Campos do formulário
    private JTextField txtNome;
    private JTextField txtNrBI;
    private JTextField txtNuit;
    private JTextField txtTelefone;
    private JTextField txtSalario;
    private JPasswordField txtSenha;
    private JTextField txtPesquisar;

    // Componentes da foto
    private JLabel lblFoto;
    private String caminhoFotoAtual;

    // Tabela
    private JTable tabelaVendedores;
    private DefaultTableModel modeloTabela;
    private TableRowSorter<DefaultTableModel> sorter;

    // Botões
    private JButton btnAdicionarFoto;
    private JButton btnRemoverFoto;
    private JButton btnCadastrar;
    private JButton btnEditar;
    private JButton btnRemover;
    private JButton btnVoltar;
    private JButton btnLimpar;

    public GerirVendedoresView(SistemaController controller) {
        this.controller = controller;
        new File(FOTOS_VENDEDORES_PATH).mkdirs();
        try {
            initComponents();
            setupLayout();
            setupEvents();
            carregarVendedores();
            installAltForVoltar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao inicializar a tela: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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
        txtPesquisar = new JTextField();

        styleTextField(txtNome, "Nome");
        styleTextField(txtNrBI, "Nº BI");
        styleTextField(txtNuit, "NUIT");
        styleTextField(txtTelefone, "Telefone");
        styleTextField(txtSalario, "Salário");
        styleTextField(txtSenha, "Senha");
        styleTextField(txtPesquisar, "🔍 Pesquisar");

        // Ajustar altura dos campos
        Dimension dimCampo = new Dimension(200, 45);
        txtNome.setPreferredSize(dimCampo);
        txtNrBI.setPreferredSize(dimCampo);
        txtNuit.setPreferredSize(dimCampo);
        txtTelefone.setPreferredSize(dimCampo);
        txtSalario.setPreferredSize(dimCampo);
        txtSenha.setPreferredSize(dimCampo);
        txtPesquisar.setPreferredSize(new Dimension(180, 35));

        // Aplicar efeito hover aos campos
        JTextField[] campos = { txtNome, txtNrBI, txtNuit, txtTelefone, txtSalario, txtPesquisar };
        for (JTextField tf : campos) {
            adicionarEfeitoHover(tf);
        }
        adicionarEfeitoHover(txtSenha);

        // Componentes da foto
        lblFoto = new JLabel("Sem Foto", SwingConstants.CENTER);
        lblFoto.setFont(UITheme.FONT_SUBHEADING);
        lblFoto.setForeground(UITheme.TEXT_SECONDARY);
        lblFoto.setOpaque(true);
        lblFoto.setBackground(UITheme.CARD_BACKGROUND);
        lblFoto.setBorder(BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT));
        lblFoto.setPreferredSize(new Dimension(160, 158));

        // Botões de Ação
        btnAdicionarFoto = UITheme.createPrimaryButton("📸 Adicionar");
        btnRemoverFoto = UITheme.createSecondaryButton("🗑 Remover");
        btnCadastrar = UITheme.createSuccessButton("➕ Cadastrar");
        btnEditar = UITheme.createPrimaryButton("✏ Editar");
        btnRemover = UITheme.createDangerButton("🗑 Remover");
        btnVoltar = UITheme.createSecondaryButton("⬅ Voltar");
        btnLimpar = UITheme.createSecondaryButton("🧹 Limpar");

        // Estilo reduzido dos botões
        JButton[] botoes = { btnAdicionarFoto, btnRemoverFoto, btnCadastrar, btnEditar, btnRemover, btnVoltar, btnLimpar };
        for (JButton btn : botoes) {
            btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
            btn.setPreferredSize(new Dimension(120, 35));
        }

        // Tabela
        String[] colunas = {"ID", "Nome", "BI", "NUIT", "Telefone", "Salário"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaVendedores = new JTable(modeloTabela);

        tabelaVendedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendedores.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaVendedores.setRowHeight(30);
        sorter = new TableRowSorter<>(modeloTabela);
        tabelaVendedores.setRowSorter(sorter);

        // Configurar header da tabela
        JTableHeader header =  tabelaVendedores.getTableHeader();
        header.setBackground(Color.WHITE); // fundo branco (ou troca, se quiser)
        header.setForeground(UITheme.TEXT_SECONDARY); // mesma cor dos títulos dos campos
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16)); // mesma fonte e tamanho
        header.setOpaque(true);
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
        if (component instanceof JTextField) {
            ((JTextField) component).setCaretColor(UITheme.PRIMARY_COLOR);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // --- Top bar ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TOPBAR_BACKGROUND);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY_COLOR));
        topBar.setPreferredSize(new Dimension(0, UITheme.TOPBAR_HEIGHT));
        JLabel lblTitulo = UITheme.createHeadingLabel("👥 GESTÃO DE VENDEDORES");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(lblTitulo, BorderLayout.CENTER);
        JPanel voltarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voltarPanel.setOpaque(false);
        voltarPanel.add(btnVoltar);
        topBar.add(voltarPanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // --- PAINEL PRINCIPAL ---
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UITheme.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // --- PAINEL SUPERIOR (foto, formulário, botões) ---
        JPanel topContentPanel = new JPanel(new BorderLayout(15, 15));
        topContentPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // Painel da Foto (com botões)
        JPanel fotoPainelWrapper = new JPanel(new BorderLayout(10, 10));
        fotoPainelWrapper.setBackground(UITheme.BACKGROUND_COLOR);
        lblFoto.setPreferredSize(new Dimension(150, 150));
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
        acoesPanel.add(btnLimpar);

        topContentPanel.add(acoesPanel, BorderLayout.EAST);

        // --- PAINEL INFERIOR (tabela e pesquisa) ---
        JPanel tabelaPanel = new JPanel(new BorderLayout(10, 10));
        tabelaPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.SECONDARY_LIGHT),
                "👥 Vendedores Cadastrados",
                0, 0,
                new Font("Segoe UI Emoji", Font.BOLD, 14),
                UITheme.TEXT_SECONDARY
        ));
        tabelaPanel.setBackground(UITheme.CARD_BACKGROUND);

        // Painel de pesquisa
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(txtPesquisar);
        tabelaPanel.add(searchPanel, BorderLayout.NORTH);

        // Tabela
        JScrollPane scroll = new JScrollPane(tabelaVendedores);
        tabelaPanel.add(scroll, BorderLayout.CENTER);

        mainPanel.add(topContentPanel, BorderLayout.NORTH);
        mainPanel.add(tabelaPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // --- RODAPÉ ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UITheme.TOPBAR_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UITheme.PRIMARY_COLOR));
        bottomPanel.setPreferredSize(new Dimension(0, 40));

        JLabel lblCopyright = new JLabel("© 2025 Sistema de Venda de Equipamentos Informáticos");
        lblCopyright.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        lblCopyright.setForeground(UITheme.TEXT_WHITE);
        bottomPanel.add(lblCopyright);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        btnAdicionarFoto.addActionListener(e -> adicionarFoto());
        btnRemoverFoto.addActionListener(e -> removerFoto());
        btnCadastrar.addActionListener(e -> cadastrarVendedor());
        btnEditar.addActionListener(e -> editarVendedor());
        btnRemover.addActionListener(e -> removerVendedor());
        btnVoltar.addActionListener(e -> voltarMenuPrincipal());
        btnLimpar.addActionListener(e -> limparCampos());
        tabelaVendedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarVendedorSelecionado();
        });
        txtPesquisar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarVendedores();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarVendedores();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarVendedores();
            }
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
                File destino = new File(FOTOS_VENDEDORES_PATH + novoNome);

                Files.copy(arquivoSelecionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                this.caminhoFotoAtual = destino.getPath();
                exibirImagem(this.caminhoFotoAtual);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar a foto: " + e.getMessage(), "Erro de Arquivo", JOptionPane.ERROR_MESSAGE);
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
            Image img = icon.getImage();
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
                vendedor.setFotoPath(this.caminhoFotoAtual);
                if (controller.adicionarVendedor(vendedor)) {
                    JOptionPane.showMessageDialog(this, "Vendedor cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCampos();
                    carregarVendedores();
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível cadastrar o vendedor.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
            vendedorNovo.setFotoPath(this.caminhoFotoAtual);

            if (vendedorNovo.getSenha() == null || vendedorNovo.getSenha().isEmpty()) {
                vendedorNovo.setSenha(vendedorAntigo.getSenha());
            } else {
                vendedorNovo.setSenha(util.BCryptHasher.hashPassword(vendedorNovo.getSenha()));
            }

            if (controller.atualizarVendedor(vendedorAntigo, vendedorNovo)) {
                JOptionPane.showMessageDialog(this, "Vendedor atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
                carregarVendedores();
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
                    if (v.getFotoPath() != null) {
                        try {
                            Files.deleteIfExists(Paths.get(v.getFotoPath()));
                        } catch (IOException e) {
                            /* Ignorar falha na deleção */
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Vendedor removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCampos();
                    carregarVendedores();
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

            if (!Validador.validarCampoObrigatorio (nome)) throw new IllegalArgumentException("O campo 'Nome' é obrigatório.");
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
        try {
            modeloTabela.setRowCount(0);
            List<Vendedor> vendedores = controller.getVendedores();
            if (vendedores != null) {
                for (Vendedor v : vendedores) {
                    modeloTabela.addRow(new Object[]{
                            v.getId(), v.getNome(), v.getNrBI(), v.getNuit(),
                            v.getTelefone(), String.format("%.2f MT", v.getSalario())
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendedores: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
                this.caminhoFotoAtual = v.getFotoPath();
                exibirImagem(this.caminhoFotoAtual);
            }
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtNrBI.setText("");
        txtNuit.setText("");
        txtTelefone.setText("");
        txtSalario.setText("");
        txtSenha.setText("");
        txtPesquisar.setText("");
        styleTextField(txtSenha, "Senha");
        tabelaVendedores.clearSelection();
        removerFoto();
        sorter.setRowFilter(null);
    }

    private void voltarMenuPrincipal() {
        try {
            String tipoUsuario = controller.getTipoUsuarioLogado();
            if (tipoUsuario == null) {
                System.err.println("Tipo de usuário é nulo. Redirecionando para Login.");
                controller.getCardLayoutManager().showPanel("Login");
                return;
            }

            String painel = switch (tipoUsuario) {
                case "Gestor" -> "MenuGestor";
                case "Vendedor" -> "MenuVendedor";
                case "Administrador" -> "MenuAdministrador";
                default -> {
                    System.err.println("Tipo de usuário desconhecido: " + tipoUsuario);
                    yield "Login";
                }
            };

            if (controller.getCardLayoutManager() == null) {
                System.err.println("CardLayoutManager é nulo.");
                JOptionPane.showMessageDialog(this, "Erro interno: Gerenciador de layout não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            controller.getCardLayoutManager().showPanel(painel);
        } catch (Exception ex) {
            System.err.println("Erro ao voltar para o menu principal: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao voltar para o menu principal: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            controller.getCardLayoutManager().showPanel("Login");
        }
    }

    private void filtrarVendedores() {
        String texto = txtPesquisar.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0, 1));
        }
    }

    private void adicionarEfeitoHover(JTextField campo) {
        final Border bordaOriginal = campo.getBorder();
        Border bordaHover = new CompoundBorder(
                new javax.swing.border.LineBorder(new Color(16, 234, 208), 3, true),
                new EmptyBorder(3, 6, 3, 6)
        );

        campo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                campo.setBorder(bordaHover);
                campo.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!campo.hasFocus()) {
                    campo.setBorder(bordaOriginal);
                }
            }
        });

        campo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(bordaHover);
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(bordaOriginal);
            }
        });
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