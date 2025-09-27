package persistence;

import com.google.gson.reflect.TypeToken;
import model.concretas.Vendedor;
import java.lang.reflect.Type;

public class VendedorRepository extends BaseListRepository<Vendedor> {
    public VendedorRepository(String path) { super(path, Vendedor::getId); }
    @Override protected Type getListType() { return new TypeToken<java.util.List<Vendedor>>(){}.getType(); }
}