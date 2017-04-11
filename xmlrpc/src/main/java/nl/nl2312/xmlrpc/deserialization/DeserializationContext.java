package nl.nl2312.xmlrpc.deserialization;

import java.util.HashMap;
import java.util.Map;

public final class DeserializationContext {

    private final Map<Class<?>, StructDeserializer<?>> structDeserializers = new HashMap<>();
    private final Map<Class<?>, ArrayDeserializer<?>> arrayDeserializers = new HashMap<>();

    public boolean hasStructDeserializer(Class<?> type) {
        return structDeserializers.containsKey(type);
    }

    public <T> void addStructDeserializer(Class<T> clazz, StructDeserializer<T> structDeserializer) {
        structDeserializers.put(clazz, structDeserializer);
    }

    @SuppressWarnings("unchecked") // Type parity between map key and deserializer is enforced during addition
    public <T> StructDeserializer<T> structDeserializer(Class<T> type) {
        return (StructDeserializer<T>) structDeserializers.get(type);
    }

    public boolean hasArrayDeserializer(Class<?> type) {
        return arrayDeserializers.containsKey(type);
    }

    public <T> void addArrayDeserializer(Class<T> clazz, ArrayDeserializer<T> arrayDeserializer) {
        arrayDeserializers.put(clazz, arrayDeserializer);
    }

    @SuppressWarnings("unchecked") // Type parity between map key and deserializer is enforced during addition
    public <T> ArrayDeserializer<T> arrayDeserializer(Class<T> type) {
        return (ArrayDeserializer<T>) arrayDeserializers.get(type);
    }

}
