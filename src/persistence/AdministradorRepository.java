package persistence;

import com.google.gson.reflect.TypeToken;
import model.concretas.Administrador;
import java.lang.reflect.Type;
import java.util.Optional;

public class AdministradorRepository extends BaseListRepository<Administrador> {
    public AdministradorRepository(String path) {
        super(path, Administrador::getId);
    }

    @Override
    public void removeById(String id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo.");
        }
        if ("ADMIN".equals(id)) {
            System.err.println("Tentativa de remoção do administrador supremo bloqueada: ID=" + id);
            throw new IllegalStateException("Administrador supremo não pode ser removido.");
        }
        super.removeById(id);
    }

    @Override
    public void atualizar(Administrador admin) throws Exception {
        if (admin == null || admin.getId() == null) {
            throw new IllegalArgumentException("Administrador ou ID não pode ser nulo.");
        }
        if ("ADMIN".equals(admin.getId())) {
            throw new IllegalStateException("Dados do Administrador supremo não podem ser alterados.");
        }
        if (!findById(admin.getId()).isPresent()) {
            throw new IllegalStateException("Administrador com ID " + admin.getId() + " não encontrado.");
        }
        super.atualizar(admin);
    }

    @Override
    protected Type getListType() {
        return new TypeToken<java.util.List<Administrador>>(){}.getType();
    }
}