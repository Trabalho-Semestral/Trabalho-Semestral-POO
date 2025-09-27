package persistence;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
        String json = java.nio.file.Files.readString(file);

        if (json == null || json.trim().isEmpty()) {
            cache.clear();
            indexById.clear();
            save();
            return;
        }

        java.util.List<T> list = JsonUtil.GSON.fromJson(json, getListType());
        cache.clear();
        indexById.clear();
        if (list != null) {
            for (T item : list) addToMemory(item);
        }
        save();
    }

    public synchronized List<T> findAll() { return new ArrayList<>(cache); }

    public synchronized Optional<T> findById(String id) { return Optional.ofNullable(indexById.get(id)); }

    public synchronized void upsert(T item) throws Exception {
        String id = idExtractor.apply(item);
        cache.removeIf(x -> Objects.equals(idExtractor.apply(x), id));
        cache.add(item);
        indexById.put(id, item);
        save();
    }

    public synchronized void add(T item) throws Exception { addToMemory(item); save(); }

    public synchronized void removeById(String id) throws Exception {
        cache.removeIf(x -> Objects.equals(idExtractor.apply(x), id));
        indexById.remove(id);
        save();
    }

    public synchronized void replaceAll(Collection<T> items) throws Exception {
        cache.clear();
        indexById.clear();
        for (T item : items) addToMemory(item);
        save();
    }

    protected synchronized void save() throws Exception {
        String json = JsonUtil.GSON.toJson(cache);
        JsonUtil.writeAtomic(file, json);
    }

    private void addToMemory(T item) {
        cache.add(item);
        indexById.put(idExtractor.apply(item), item);
    }

}