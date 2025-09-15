package model.concretas;

/**
 * Enum que define os tipos de usuário do sistema.
 */
public enum TipoUsuario {
    ADMINISTRADOR("Administrador"),
    GESTOR("Gestor"),
    VENDEDOR("Vendedor"),
    CLIENTE("Cliente");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}