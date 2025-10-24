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
    public Optional<Equipamento> buscarPorId(String id) {
        try {
            List<Equipamento> todosEquipamentos = findAll();
            return todosEquipamentos.stream()
                    .filter(e -> e.getId() != null && e.getId().equals(id))
                    .findFirst();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    @Override
    public void salvar(Equipamento equipamento) {
        try {
            // Buscar todos os equipamentos
            List<Equipamento> equipamentos = findAll();

            // Encontrar e atualizar o equipamento
            boolean encontrado = false;
            for (int i = 0; i < equipamentos.size(); i++) {
                if (equipamentos.get(i).getId().equals(equipamento.getId())) {
                    equipamentos.set(i, equipamento);
                    encontrado = true;
                    break;
                }
            }

            // Se não encontrou, adicionar novo
            if (!encontrado) {
                equipamentos.add(equipamento);
            }

            // Salvar a lista atualizada
            replaceAll(equipamentos);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar equipamento: " + e.getMessage(), e);
        }
    }

}