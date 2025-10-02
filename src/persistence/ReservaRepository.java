package persistence;

import model.concretas.Reserva;
import java.util.List;
import java.util.Optional;
public interface ReservaRepository {
    void salvar(Reserva reserva);
    void atualizar(Reserva reserva);
    Optional<Reserva> buscarPorId(String idReserva);
    List<Reserva> listarAtivas();
    List<Reserva> buscarPorNomeCliente(String nomeCliente);
    List<Reserva> buscarPorNomeVendedor(String nomeVendedor);
    List<Reserva> listarTodas();

}
