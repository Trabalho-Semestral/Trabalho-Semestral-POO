package persistence;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class BaseListRepository<T> {
    protected final Path file;
    protected final List<T> cache = new ArrayList<>();
    protected final Map<String, T> indexById = new HashMap<>();
    private final Function<T, String> idExtractor;

    protected BaseListRepository(String filePath, Function<T, String> idExtractor) {
        this.file = Paths.get(filePath);
        this.idExtractor = idExtractor;
    }

    protected abstract Type getListType();

    public synchronized void init() throws Exception {
        JsonUtil.ensureFile(file);

        try {
            String json = Files.readString(file, StandardCharsets.UTF_8).trim();

            if (json.isEmpty()) {
                initializeEmptyRepository();
                return;
            }

            if (json.startsWith("\uFEFF")) {
                json = json.substring(1);
            }
            if (!isValidJsonArray(json)) {
                System.err.println("Formato JSON inválido em " + file + ". Inicializando com dados vazios.");
                initializeEmptyRepository();
                return;
            }
            List<T> list = loadJsonSafely(json);

            cache.clear();
            indexById.clear();
            if (list != null) {
                for (T item : list) {
                    if (item != null && idExtractor.apply(item) != null) {
                        addToMemory(item);
                    }
                }
            }
            save();

        } catch (Exception e) {
            System.err.println("Erro crítico ao inicializar " + file + ": " + e.getMessage());
            handleCorruptedFile(e);
        }
    }

    private List<T> loadJsonSafely(String json) {
        try {
            // Primeira tentativa: GSON padrão
            return JsonUtil.GSON.fromJson(json, getListType());
        } catch (JsonSyntaxException e1) {
            System.err.println("Primeira tentativa falhou para " + file + ": " + e1.getMessage());

            try {
                // Segunda tentativa: GSON leniente
                GsonBuilder lenientGson = new GsonBuilder()
                        .setLenient()
                        .serializeNulls();

                // Configurar adaptadores de tipo se necessário
                if (getListType().toString().contains("Equipamento")) {
                    lenientGson.registerTypeAdapterFactory(JsonUtil.EQUIPAMENTO_RTAF);
                }

                return lenientGson.create().fromJson(json, getListType());
            } catch (JsonSyntaxException e2) {
                System.err.println("Segunda tentativa falhou para " + file + ": " + e2.getMessage());
                return attemptJsonRecovery(json);
            }
        }
    }

    private List<T> attemptJsonRecovery(String json) {
        try {
            System.err.println("Tentando recuperar JSON corrompido: " + file);

            // Tentativa 1: Remover caracteres problemáticos
            String cleanedJson = json
                    .replaceAll("[\\x00-\\x1F\\x7F]", "")
                    .replaceAll(",{2,}", ",")
                    .replaceAll(",}", "}")
                    .replaceAll(",]", "]");

            // Tentativa 2: Garantir que é um array válido
            if (!cleanedJson.startsWith("[")) {
                cleanedJson = "[" + cleanedJson;
            }
            if (!cleanedJson.endsWith("]")) {
                cleanedJson = cleanedJson + "]";
            }

            GsonBuilder recoveryGson = new GsonBuilder()
                    .setLenient()
                    .serializeNulls();

            if (getListType().toString().contains("Equipamento")) {
                recoveryGson.registerTypeAdapterFactory(JsonUtil.EQUIPAMENTO_RTAF);
            }

            List<T> result = recoveryGson.create().fromJson(cleanedJson, getListType());
            System.err.println("Recuperação parcial bem-sucedida para " + file);
            return result;

        } catch (Exception e) {
            System.err.println("Falha na recuperação do JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean isValidJsonArray(String json) {
        String trimmed = json.trim();
        return trimmed.startsWith("[") && trimmed.endsWith("]");
    }

    private void initializeEmptyRepository() {
        cache.clear();
        indexById.clear();
        try {
            save(); // Salva array vazio []
        } catch (Exception e) {
            System.err.println("Erro ao inicializar repositório vazio: " + e.getMessage());
        }
    }

    private void handleCorruptedFile(Exception originalError) {
        try {
            System.err.println("Recriando arquivo corrompido: " + file);

            // Fazer backup do arquivo corrompido
            Path backupFile = file.resolveSibling(file.getFileName() + ".corrupted");
            if (Files.exists(file)) {
                Files.move(file, backupFile, StandardCopyOption.REPLACE_EXISTING);
                System.err.println("Backup criado: " + backupFile);
            }

            // Recriar arquivo limpo
            JsonUtil.ensureFile(file);
            initializeEmptyRepository();

            System.err.println("Arquivo recriado com sucesso: " + file);

        } catch (Exception backupError) {
            System.err.println("Falha ao recriar arquivo: " + backupError.getMessage());
            // Último recurso: limpar em memória
            cache.clear();
            indexById.clear();
        }
    }

    public synchronized List<T> findAll() {
        return new ArrayList<>(cache);
    }

    public synchronized Optional<T> findById(String id) {
        return Optional.ofNullable(indexById.get(id));
    }

    public synchronized void upsert(T item) throws Exception {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }

        String id = idExtractor.apply(item);
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do item não pode ser nulo ou vazio");
        }

        cache.removeIf(x -> Objects.equals(idExtractor.apply(x), id));
        cache.add(item);
        indexById.put(id, item);
        save();
    }

    public synchronized void add(T item) throws Exception {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }

        String id = idExtractor.apply(item);
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do item não pode ser nulo ou vazio");
        }

        if (indexById.containsKey(id)) {
            throw new IllegalArgumentException("Item com ID " + id + " já existe");
        }

        addToMemory(item);
        save();
    }

    public synchronized void removeById(String id) throws Exception {
        if (id == null) return;

        boolean removed = cache.removeIf(x -> Objects.equals(idExtractor.apply(x), id));
        indexById.remove(id);

        if (removed) {
            save();
        }
    }

    public synchronized void replaceAll(Collection<T> items) throws Exception {
        cache.clear();
        indexById.clear();

        if (items != null) {
            for (T item : items) {
                if (item != null && idExtractor.apply(item) != null) {
                    addToMemory(item);
                }
            }
        }

        save();
    }

    protected synchronized void save() throws Exception {
        try {
            String json = JsonUtil.GSON.toJson(cache);
            JsonUtil.writeAtomic(file, json);
        } catch (Exception e) {
            System.err.println("Erro ao salvar " + file + ": " + e.getMessage());
            throw e;
        }
    }

    private void addToMemory(T item) {
        if (item != null) {
            String id = idExtractor.apply(item);
            if (id != null) {
                cache.add(item);
                indexById.put(id, item);
            }
        }
    }

    public synchronized void diagnostic() {
        System.out.println("=== Diagnóstico: " + file + " ===");
        System.out.println("Itens em cache: " + cache.size());
        System.out.println("IDs indexados: " + indexById.size());
        System.out.println("Primeiros 3 itens:");
        cache.stream().limit(3).forEach(item ->
                System.out.println("  - " + idExtractor.apply(item) + ": " + item));
        System.out.println("==================");
    }
}
