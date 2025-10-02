package persistence;

import model.abstractas.Equipamento;
import java.util.Optional;

public interface EquipamentoRepository {
    Optional<Equipamento> buscarPorId(String id);
    void salvar(Equipamento equipamento);
}
