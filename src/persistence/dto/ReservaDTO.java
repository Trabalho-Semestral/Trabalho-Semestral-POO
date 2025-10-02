package persistence.dto;

import java.util.List;

public class ReservaDTO {
    public String idReserva;
    public String clienteId;
    public String clienteNome;
    public String vendedorId;
    public String vendedorNome;
    public String status; // ATIVA, CANCELADA, EXPIRADA, CONVERTIDA
    public long dataMillis;
    public long expiraEmMillis;
    public List<ItemReservaDTO> itens;

    public static class ItemReservaDTO {
        public String equipamentoId;
        public String equipamentoMarca;
        public int quantidade;
    }
}
