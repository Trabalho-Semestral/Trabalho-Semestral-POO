package persistence;

import com.google.gson.reflect.TypeToken;
import model.abstractas.Equipamento;
import java.lang.reflect.Type;

public class EquipamentoRepository extends BaseListRepository<Equipamento> {
    public EquipamentoRepository(String path) { super(path, Equipamento::getId); }
    @Override protected Type getListType() { return new TypeToken<java.util.List<Equipamento>>(){}.getType(); }
}