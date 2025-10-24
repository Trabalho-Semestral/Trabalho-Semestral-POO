package persistence;

import model.concretas.ItemVenda;
import model.concretas.Venda;
import persistence.dto.ItemVendaDTO;
import persistence.dto.VendaDTO;
import persistence.mapper.VendaMapper;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VendaFileRepository {
    private final Path pastaDados;
    private final Path arquivoNdjson;

    public VendaFileRepository(String pasta) {
        this.pastaDados = Paths.get(pasta);
        this.arquivoNdjson = pastaDados.resolve("vendas.ndjson");
    }

    public void init() throws IOException {
        Files.createDirectories(pastaDados);
        JsonUtil.ensureFile(arquivoNdjson);
    }

    public synchronized void salvar(Venda venda) throws IOException {
        VendaDTO dto = VendaMapper.toDTO(venda);
        com.google.gson.Gson gsonCompact = new com.google.gson.GsonBuilder().serializeNulls().create();
        String linha = gsonCompact.toJson(dto) + System.lineSeparator();
        Files.writeString(arquivoNdjson, linha, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }
    public java.util.List<persistence.dto.VendaDTO> listarTodas() throws java.io.IOException {
        java.util.List<persistence.dto.VendaDTO> out = new java.util.ArrayList<>();
        try (var br = java.nio.file.Files.newBufferedReader(arquivoNdjson, java.nio.charset.StandardCharsets.UTF_8)) {
            String ln; boolean first = true;
            while ((ln = br.readLine()) != null) {
                if (ln.isBlank()) continue;
                String trimmed = ln.trim();
                if (first && trimmed.length() > 0 && trimmed.charAt(0) == '\uFEFF') {
                    trimmed = trimmed.substring(1);
                }
                first = false;
                if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) continue;
                try {
                    var dto = JsonUtil.GSON.fromJson(trimmed, persistence.dto.VendaDTO.class);
                    if (dto != null) out.add(dto);
                } catch (Exception ignored) {
                }
            }
        }
        return out;
    }

    public java.util.List<persistence.dto.VendaDTO> listarPorPeriodo(java.util.Date inicio, java.util.Date fim) throws java.io.IOException {
        long a = inicio.getTime(), b = fim.getTime();
        java.util.List<persistence.dto.VendaDTO> out = new java.util.ArrayList<>();
        try (var br = java.nio.file.Files.newBufferedReader(arquivoNdjson, java.nio.charset.StandardCharsets.UTF_8)) {
            String ln; boolean first = true;
            while ((ln = br.readLine()) != null) {
                if (ln.isBlank()) continue;
                String trimmed = ln.trim();
                if (first && trimmed.length() > 0 && trimmed.charAt(0) == '\uFEFF') {
                    trimmed = trimmed.substring(1);
                }
                first = false;
                if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) continue;
                try {
                    var dto = JsonUtil.GSON.fromJson(trimmed, persistence.dto.VendaDTO.class);
                    if (dto != null && dto.dataMillis >= a && dto.dataMillis <= b) out.add(dto);
                } catch (Exception ignored) {}
            }
        }
        return out;
    }
    public Path exportarCSV(List<VendaDTO> vendas, String nomeArquivo) throws IOException {
        Path exportDir = pastaDados.resolve("exports");
        Files.createDirectories(exportDir);
        Path arq = exportDir.resolve(nomeArquivo);
        try (BufferedWriter w = Files.newBufferedWriter(arq, StandardCharsets.UTF_8)) {
            w.write("idVenda;data;vendedorId;clienteId;total;metodoPagamento;totalPago;troco\n");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (VendaDTO v : vendas) {
                String totalStr = v.total != null ? v.total.toPlainString() : "0";
                String metodo = v.metodoPagamento != null ? v.metodoPagamento : "";
                String pagoStr = v.totalPago != null ? v.totalPago.toPlainString() : "0";
                String trocoStr = v.troco != null ? v.troco.toPlainString() : "0";
                w.write(v.idVenda + ";" + sdf.format(new Date(v.dataMillis)) + ";" + v.vendedorId + ";" + v.clienteId + ";" + totalStr + ";" + metodo + ";" + pagoStr + ";" + trocoStr + "\n");
            }
        }
        return arq;
    }
    private VendaDTO toDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();
        dto.idVenda = venda.getIdVenda();
        dto.clienteId = venda.getCliente() != null ? venda.getCliente().getId() : null;
        dto.clienteNome = venda.getCliente() != null ? venda.getCliente().getNome() : null;
        dto.vendedorId = venda.getVendedor() != null ? venda.getVendedor().getId() : null;
        dto.vendedorNome = venda.getVendedor() != null ? venda.getVendedor().getNome() : null;
        dto.dataMillis = venda.getData() != null ? venda.getData().getTime() : System.currentTimeMillis();
        dto.total = venda.getTotalComDescontosImpostos();
        dto.desconto = venda.getDesconto();
        dto.imposto = venda.getImposto();
        dto.metodoPagamento = venda.getMetodoPagamento();
        dto.totalPago = venda.getTotalPago();
        dto.troco = venda.getTroco();

        // Converter itens
        if (venda.getItens() != null) {
            dto.itens = new ArrayList<>();
            for (ItemVenda item : venda.getItens()) {
                ItemVendaDTO itemDTO = new ItemVendaDTO();
                itemDTO.equipamentoId = item.getEquipamento().getId();
                itemDTO.quantidade = item.getQuantidade();
                itemDTO.precoUnitario = item.getPrecoUnitario();
                dto.itens.add(itemDTO);
            }
        }

        return dto;
    }


}