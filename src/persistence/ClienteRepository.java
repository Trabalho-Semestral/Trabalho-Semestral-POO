package persistence;

import com.google.gson.reflect.TypeToken;
import model.concretas.Cliente;
import java.lang.reflect.Type;

public class ClienteRepository extends BaseListRepository<Cliente> {
    public ClienteRepository(String path) { super(path, Cliente::getId); }
    @Override protected Type getListType() { return new TypeToken<java.util.List<Cliente>>(){}.getType(); }
}