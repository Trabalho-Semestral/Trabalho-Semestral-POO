package service;

import persistence.VendaFileRepository;
import persistence.dto.VendaDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class RelatorioVendasService {
    private final VendaFileRepository repo;
    public RelatorioVendasService(VendaFileRepository repo) { this.repo = repo; }

    public Map<String, BigDecimal> totalPorDia(Date inicio, Date fim) throws IOException {
        List<VendaDTO> vendas = repo.listarPorPeriodo(inicio, fim);
        Map<String, BigDecimal> mapa = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (VendaDTO v : vendas) {
            String dia = sdf.format(new Date(v.dataMillis));
            mapa.merge(dia, v.total != null ? v.total : BigDecimal.ZERO, BigDecimal::add);
        }
        return mapa;
    }

    public Map<String, Integer> itensMaisVendidos(Date inicio, Date fim) throws IOException {
        List<VendaDTO> vendas = repo.listarPorPeriodo(inicio, fim);
        Map<String, Integer> contagem = new HashMap<>();
        for (VendaDTO v : vendas) {
            if (v.itens == null) continue;
            v.itens.forEach(it -> contagem.merge(it.equipamentoId, it.quantidade, Integer::sum));
        }
        return contagem;
    }

    public java.nio.file.Path exportarCSVPeriodo(Date inicio, Date fim, String nomeArquivo) throws IOException {
        List<VendaDTO> vendas = repo.listarPorPeriodo(inicio, fim);
        return repo.exportarCSV(vendas, nomeArquivo);
    }
}