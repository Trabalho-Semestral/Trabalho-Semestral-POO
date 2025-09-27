package persistence;

import com.google.gson.reflect.TypeToken;
import model.concretas.Gestor;
import java.lang.reflect.Type;

public class GestorRepository extends BaseListRepository<Gestor> {
    public GestorRepository(String path) {
        super(path, Gestor::getId);
    }

    @Override
    protected Type getListType() {
        return new TypeToken<java.util.List<Gestor>>(){}.getType();
    }
}