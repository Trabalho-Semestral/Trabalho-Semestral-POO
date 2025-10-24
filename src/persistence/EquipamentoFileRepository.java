package persistence;

import com.google.gson.reflect.TypeToken;
import model.abstractas.Equipamento;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class EquipamentoFileRepository extends BaseListRepository<Equipamento> implements EquipamentoRepository {
    public EquipamentoFileRepository(String path) { super(path, Equipamento::getId); }
    @Override protected Type getListType() { return new TypeToken<java.util.List<Equipamento>>(){}.getType(); }



    @Override
    public void salvar(Equipamento equipamento) {

    }
    @Override
    public Optional<Equipamento> buscarPorId(String id) {
        try {
            List<Equipamento> todosEquipamentos = findAll();
            return todosEquipamentos.stream()
                    .filter(e -> e.getId() != null && e.getId().equals(id))
                    .findFirst();
        } catch (Exception e) {
            System.err.println("Erro ao buscar equipamento por ID: " + id);
            e.printStackTrace();
            return Optional.empty();
        }
    }
}