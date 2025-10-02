package model.concretas;

import model.abstractas.Funcionario;

/**
 * Classe que representa um administrador no sistema.
 */
public class Administrador extends Funcionario {

    private String nivelAcesso;

    /**
     * Construtor padrão.
     */
    public Administrador() {
        super();
        setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        this.nivelAcesso = "TOTAL";
    }

    /**
     * Construtor com parâmetros.
     * @param nome Nome do administrador
     * @param nrBI Número do BI
     * @param nuit NUIT do administrador
     * @param telefone Telefone do administrador
     * @param salario Salário do administrador
     * @param senha Senha do administrador
     */
    public Administrador(String nome, String nrBI, String nuit, String telefone,
                         double salario, String senha) {
        super(nome, nrBI, nuit, telefone, TipoUsuario.ADMINISTRADOR, salario, senha);
        this.nivelAcesso = "TOTAL";
    }

    /**
     * Administrador tem acesso total ao sistema
     * @return true - pode gerir operações
     */
    public boolean podeGerirOperacoes() {
        return true;
    }

    /**
     * Administrador pode configurar o sistema
     * @return true - pode configurar sistema
     */
    public boolean podeConfigurarSistema() {
        return true;
    }

    /**
     * Administrador pode gerir outros usuários
     * @return true - pode gerir usuários
     */
    public boolean podeGerirUsuarios() {
        return true;
    }

    // Getters e Setters
    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", nrBI='" + nrBI + '\'' +
                ", nuit='" + nuit + '\'' +
                ", telefone='" + telefone + '\'' +
                ", salario=" + salario +
                ", tipoUsuario=" + tipoUsuario + // ✅ Incluir tipo de usuário
                ", nivelAcesso='" + nivelAcesso + '\'' + // ✅ Incluir nível de acesso
                ", dataContratacao=" + dataContratacao +
                '}';
    }
}