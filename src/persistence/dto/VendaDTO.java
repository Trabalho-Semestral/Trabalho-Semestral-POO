package persistence.dto;

import java.math.BigDecimal;
import java.util.List;

public class VendaDTO {
    public String idVenda;
    public long dataMillis;
    public String vendedorId;
    public String clienteId;
    public List<ItemVendaDTO> itens;
    public BigDecimal desconto;
    public BigDecimal imposto;
    public BigDecimal total;
}