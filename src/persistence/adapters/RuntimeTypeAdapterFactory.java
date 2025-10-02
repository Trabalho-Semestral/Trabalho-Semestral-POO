package persistence.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Implementação local de um RuntimeTypeAdapterFactory para Gson.
 *
 * Permite serializar/deserializar hierarquias (polimorfismo) adicionando um
 * campo discriminador (ex.: "type") no JSON com o rótulo da subclasse.
 *
 * Exemplo de uso:
 *   RuntimeTypeAdapterFactory<Equipamento> rtaf =
 *       RuntimeTypeAdapterFactory.of(Equipamento.class, "type")
 *           .registerSubtype(Computador.class, "Computador")
 *           .registerSubtype(Periferico.class, "Periferico");
 *   Gson gson = new GsonBuilder().registerTypeAdapterFactory(rtaf).create();
 */
public final class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<?> baseType;
    private final String typeFieldName;
    private final boolean maintainTypeFieldInPojo;

    private final Map<String, Class<?>> labelToSubtype = new LinkedHashMap<>();
    private final Map<Class<?>, String> subtypeToLabel = new LinkedHashMap<>();

    private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName, boolean maintainTypeFieldInPojo) {
        this.baseType = Objects.requireNonNull(baseType, "baseType");
        this.typeFieldName = Objects.requireNonNull(typeFieldName, "typeFieldName");
        this.maintainTypeFieldInPojo = maintainTypeFieldInPojo;
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName, false);
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName, boolean maintainTypeFieldInPojo) {
        return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName, maintainTypeFieldInPojo);
    }

    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> subtype) {
        return registerSubtype(subtype, subtype.getSimpleName());
    }

    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> subtype, String label) {
        Objects.requireNonNull(subtype, "subtype");
        Objects.requireNonNull(label, "label");
        if (labelToSubtype.containsKey(label) || subtypeToLabel.containsKey(subtype)) {
            throw new IllegalArgumentException("Subtype or label already registered: " + subtype + " / " + label);
        }
        labelToSubtype.put(label, subtype);
        subtypeToLabel.put(subtype, label);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        if (!baseType.isAssignableFrom(type.getRawType())) {
            return null;
        }

        // Delegates por subtipo
        final Map<String, TypeAdapter<?>> labelToDelegate = new LinkedHashMap<>();
        final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new LinkedHashMap<>();

        for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
            TypeToken<?> subtypeTypeToken = TypeToken.get(entry.getValue());
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, subtypeTypeToken);
            labelToDelegate.put(entry.getKey(), delegate);
            subtypeToDelegate.put(entry.getValue(), delegate);
        }

        // Adapta o tipo base
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<R>() {
            @Override
            public void write(JsonWriter out, R value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                Class<?> srcType = value.getClass();
                String label = subtypeToLabel.get(srcType);
                if (label == null) {
                    throw new JsonParseException("Subtype not registered for class: " + srcType.getName());
                }
                @SuppressWarnings("unchecked")
                TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
                if (delegate == null) {
                    throw new JsonParseException("No delegate adapter for class: " + srcType.getName());
                }
                JsonObject jsonObj = delegate.toJsonTree(value).getAsJsonObject();

                if (!maintainTypeFieldInPojo) {
                    // Evita colisão caso o POJO já tenha o campo de type
                    if (jsonObj.has(typeFieldName)) {
                        throw new JsonParseException("Conflict: object already has field '" + typeFieldName + "'");
                    }
                }
                JsonObject clone = new JsonObject();
                clone.addProperty(typeFieldName, label);
                for (Map.Entry<String, JsonElement> e : jsonObj.entrySet()) {
                    clone.add(e.getKey(), e.getValue());
                }
                elementAdapter.write(out, clone);
            }

            @Override
            public R read(JsonReader in) throws IOException {
                JsonElement element = elementAdapter.read(in);
                if (element == null || element.isJsonNull()) {
                    return null;
                }
                JsonObject jsonObj = element.getAsJsonObject();
                JsonElement labelEl = jsonObj.get(typeFieldName);
                if (labelEl == null) {
                    throw new JsonParseException("Missing type field '" + typeFieldName + "' for base type " + baseType.getName());
                }
                String label = labelEl.getAsString();
                @SuppressWarnings("unchecked")
                TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
                if (delegate == null) {
                    throw new JsonParseException("Unknown subtype label '" + label + "' for base type " + baseType.getName());
                }

                if (!maintainTypeFieldInPojo) {
                    // Remover o campo de tipo antes de delegar, se o POJO não tem esse campo
                    JsonObject clone = new JsonObject();
                    for (Map.Entry<String, JsonElement> e : jsonObj.entrySet()) {
                        if (!typeFieldName.equals(e.getKey())) {
                            clone.add(e.getKey(), e.getValue());
                        }
                    }
                    return delegate.fromJsonTree(clone);
                } else {
                    return delegate.fromJsonTree(jsonObj);
                }
            }
        };
    }
}