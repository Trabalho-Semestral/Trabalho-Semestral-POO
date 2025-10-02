package persistence;

import model.concretas.Reserva;
import persistence.dto.ReservaDTO;
import persistence.mapper.ReservaMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReservaFileRepository implements ReservaRepository {
    private final Path pastaDados;
    private final Path arquivoNdjson;

    public ReservaFileRepository(String pasta) {
        this.pastaDados = Paths.get(pasta);
        this.arquivoNdjson = pastaDados.resolve("reservas.ndjson");
    }

    public void init() throws IOException {
        Files.createDirectories(pastaDados);
        JsonUtil.ensureFile(arquivoNdjson);
    }

    public synchronized void atualizar(Reserva reserva) {
        try {
            List<ReservaDTO> todas = listarTodasDTO();
            boolean found = false;
            ReservaDTO novo = persistence.mapper.ReservaMapper.toDTO(reserva);
            for (int i = 0; i < todas.size(); i++) {
                if (Objects.equals(todas.get(i).idReserva, reserva.getIdReserva())) {
                    todas.set(i, novo);
                    found = true;
                    break;
                }
            }
            if (!found) {
                // se não existe, adiciona (comportamento similar ao salvar)
                todas.add(novo);
            }
            // regrava ficheiro
            Files.writeString(arquivoNdjson, "", StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            com.google.gson.Gson gsonCompact = new com.google.gson.GsonBuilder().serializeNulls().create();
            for (ReservaDTO dto : todas) {
                Files.writeString(arquivoNdjson, gsonCompact.toJson(dto) + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao atualizar reserva", e);
        }
    }

    @Override
    public Optional<Reserva> buscarPorId(String idReserva) {
        try {
            List<Reserva> todas = listarTodas();
            return todas.stream().filter(r -> Objects.equals(r.getIdReserva(), idReserva)).findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar por id", e);
        }
    }

    @Override
    public List<Reserva> listarAtivas() {
        List<Reserva> out = new ArrayList<>();
        for (Reserva r : listarTodas()) {
            if (r.getStatus() == Reserva.StatusReserva.ATIVA) out.add(r);
        }
        return out;
    }

    @Override
    public List<Reserva> buscarPorNomeCliente(String nomeCliente) {
        List<Reserva> out = new ArrayList<>();
        for (Reserva r : listarTodas()) {
            if (r.getCliente() != null && r.getCliente().getNome().toLowerCase().contains(nomeCliente.toLowerCase())) out.add(r);
        }
        return out;
    }

    @Override
    public List<Reserva> buscarPorNomeVendedor(String nomeVendedor) {
        List<Reserva> out = new ArrayList<>();
        for (Reserva r : listarTodas()) {
            if (r.getVendedor() != null && r.getVendedor().getNome().toLowerCase().contains(nomeVendedor.toLowerCase())) out.add(r);
        }
        return out;
    }

    private List<ReservaDTO> listarTodasDTO() throws IOException {
        List<ReservaDTO> out = new ArrayList<>();
        if (!Files.exists(arquivoNdjson)) return out;
        try (var br = Files.newBufferedReader(arquivoNdjson, StandardCharsets.UTF_8)) {
            String ln;
            boolean first = true;
            while ((ln = br.readLine()) != null) {
                if (ln.isBlank()) continue;
                String trimmed = ln.trim();
                if (first && trimmed.startsWith("\uFEFF")) {
                    trimmed = trimmed.substring(1);
                }
                first = false;

                if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) continue;
                try {
                    var dto = JsonUtil.GSON.fromJson(trimmed, ReservaDTO.class);
                    if (dto != null && dto.idReserva != null) out.add(dto); // Validar DTO
                } catch (Exception e) {
                    System.err.println("Linha inválida no arquivo de reservas: " + trimmed);
                }
            }
        }
        return out;
    }

    // remover por id (reescreve arquivo)
    public synchronized void remover(String idReserva) {
        try {
            List<ReservaDTO> todas = listarTodasDTO();
            todas.removeIf(r -> Objects.equals(r.idReserva, idReserva));
            Files.writeString(arquivoNdjson, "", StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            com.google.gson.Gson gsonCompact = new com.google.gson.GsonBuilder().serializeNulls().create();
            for (var dto : todas) {
                Files.writeString(arquivoNdjson, gsonCompact.toJson(dto) + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao remover reserva", e);
        }
    }

    // export CSV opcional
    public Path exportarCSV(List<ReservaDTO> reservas, String nomeArquivo) throws IOException {
        Path exportDir = pastaDados.resolve("exports");
        Files.createDirectories(exportDir);
        Path arq = exportDir.resolve(nomeArquivo);
        try (BufferedWriter w = Files.newBufferedWriter(arq, StandardCharsets.UTF_8)) {
            w.write("idReserva;cliente;vendedor;status;data;expiraEm;itens\n");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (var r : reservas) {
                StringBuilder itensStr = new StringBuilder();
                if (r.itens != null) {
                    for (var it : r.itens) itensStr.append(it.equipamentoId).append("x").append(it.quantidade).append(" | ");
                }
                w.write(r.idReserva + ";" + r.clienteNome + ";" + r.vendedorNome + ";" + r.status + ";" +
                        sdf.format(new Date(r.dataMillis)) + ";" + sdf.format(new Date(r.expiraEmMillis)) + ";" +
                        itensStr.toString() + "\n");
            }
        }
        return arq;
    }
    @Override
    public List<Reserva> listarTodas() {
        List<Reserva> out = new ArrayList<>();
        try {
            List<ReservaDTO> dtos = listarTodasDTO();
            System.out.println("=== DEBUG ReservaRepository: " + dtos.size() + " DTOs encontrados ===");

            for (var dto : dtos) {
                System.out.println("DTO: idReserva=" + dto.idReserva +
                        ", cliente=" + dto.clienteNome +
                        ", itens=" + (dto.itens != null ? dto.itens.size() : 0));

                Reserva reserva = persistence.mapper.ReservaMapper.fromDTO(dto);
                if (reserva != null && reserva.getIdReserva() != null) {
                    out.add(reserva);
                    System.out.println("Reserva mapeada: " + reserva.getIdReserva());
                } else {
                    System.out.println("Falha ao mapear reserva do DTO");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar reservas: " + e.getMessage());
            e.printStackTrace();
        }
        return out;
    }
    public void debugArquivoReservas() {
        try {
            System.out.println("=== DEBUG ARQUIVO RESERVAS ===");
            System.out.println("Caminho: " + arquivoNdjson);
            System.out.println("Existe: " + Files.exists(arquivoNdjson));

            if (Files.exists(arquivoNdjson)) {
                String conteudo = Files.readString(arquivoNdjson, StandardCharsets.UTF_8);
                System.out.println("Tamanho: " + conteudo.length() + " caracteres");
                System.out.println("Conteúdo: " + conteudo);
            } else {
                System.out.println("Arquivo não existe!");
            }
            System.out.println("=== FIM DEBUG ===");
        } catch (Exception e) {
            System.err.println("Erro no debug: " + e.getMessage());
        }
    }
    @Override
    public synchronized void salvar(Reserva reserva) {
        try {
            System.out.println("=== SALVANDO NO ARQUIVO ===");
            System.out.println("Reserva ID: " + reserva.getIdReserva());

            ReservaDTO dto = persistence.mapper.ReservaMapper.toDTO(reserva);
            com.google.gson.Gson gsonCompact = new com.google.gson.GsonBuilder().serializeNulls().create();
            String linha = gsonCompact.toJson(dto) + System.lineSeparator();

            System.out.println("Linha JSON: " + linha);

            Files.writeString(arquivoNdjson, linha, StandardCharsets.UTF_8, StandardOpenOption.APPEND);

            System.out.println("Reserva salva no arquivo!");

        } catch (IOException e) {
            System.err.println("ERRO ao salvar reserva: " + e.getMessage());
            throw new RuntimeException("Erro ao salvar reserva", e);
        }
    }
}
