package persistence;

import com.google.gson.reflect.TypeToken;
import model.concretas.Administrador;
import java.lang.reflect.Type;

public class AdministradorRepository extends BaseListRepository<Administrador> {
    public AdministradorRepository(String path) {
        super(path, Administrador::getId);
    }

    @Override
    protected Type getListType() {
        return new TypeToken<java.util.List<Administrador>>(){}.getType();
    }
}