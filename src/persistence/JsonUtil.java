package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import persistence.adapters.RuntimeTypeAdapterFactory;

import model.abstractas.Equipamento;
import model.concretas.Computador;
import model.concretas.Periferico;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;


public class JsonUtil {
    static final RuntimeTypeAdapterFactory<Equipamento> EQUIPAMENTO_RTAF =
            RuntimeTypeAdapterFactory
                    .of(Equipamento.class, "type")
                    .registerSubtype(Computador.class, "Computador")
                    .registerSubtype(Periferico.class, "Periferico");

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(EQUIPAMENTO_RTAF)
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    public static void ensureFile(java.nio.file.Path path) throws java.io.IOException {
        if (path.getParent() != null) {
            java.nio.file.Files.createDirectories(path.getParent());
        }
        if (!java.nio.file.Files.exists(path)) {
            java.nio.file.Files.createFile(path);

            String name = path.getFileName().toString().toLowerCase();
            if (name.endsWith(".json")) {
                writeAtomic(path, "[]\n");
            }
        }
    }
    // Escrita atômica para arquivos JSON de lista
    public static void writeAtomic(Path target, String content) throws IOException {
        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
        Files.writeString(tmp, content, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }
}