package persistence;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

// Exemplo de registro simples de usuário para persistência
public class UsuarioRepository extends BaseListRepository<UsuarioRepository.UsuarioRecord> {
    public static class UsuarioRecord {
        public String id;
        public String nome;
        public String login;
        public String senhaHash;
        public String tipo;
    }

    public UsuarioRepository(String path) {
        super(path, u -> u.id);
    }

    @Override protected Type getListType() {
        return new TypeToken<java.util.List<UsuarioRecord>>(){}.getType();
    }

    public Optional<UsuarioRecord> findByLogin(String login) {
        return cache.stream().filter(u -> u.login != null && u.login.equalsIgnoreCase(login)).findFirst();
    }
}