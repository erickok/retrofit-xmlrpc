package nl.nl2312.xmlrpc.deserialization;

import nl.nl2312.xmlrpc.types.*;

import java.util.Date;
import java.util.List;

public final class ArrayValues {

    private final DeserializationContext context;
    private final List<Value> data;

    public ArrayValues(DeserializationContext context, List<Value> data) {
        this.context = context;
        this.data = data;
    }

    private Object get(int index) {
        return data.get(index).value();
    }

    public boolean asBoolean(int index) {
        Value value = data.get(index);
        if (!(value instanceof BooleanValue)) {
            throw new DeserializationException("Value " + value.value() + " is not a <boolean>");
        }
        return ((BooleanValue) value).value();
    }

    public int asInteger(int index) {
        Value value = data.get(index);
        if (!(value instanceof IntegerValue)) {
            throw new DeserializationException("Value " + value.value() + " is not an <int> or <i4>");
        }
        return ((IntegerValue) value).value();
    }

    public long asLong(int index) {
        Value value = data.get(index);
        if (!(value instanceof LongValue)) {
            throw new DeserializationException("Value " + value.value() + " is not an <i8>");
        }
        return ((LongValue) value).value();
    }

    public double asDouble(int index) {
        Value value = data.get(index);
        if (!(value instanceof DoubleValue)) {
            throw new DeserializationException("Value " + value.value() + " is not a <double>");
        }
        return ((DoubleValue) value).value();
    }

    public String asString(int index) {
        Value value = data.get(index);
        if (!(value instanceof StringValue)) {
            throw new DeserializationException("Value " + value.value() + " is not a <string>");
        }
        return ((StringValue) value).value();
    }

    public Date asDate(int index) {
        Value value = data.get(index);
        if (!(value instanceof DateValue)) {
            throw new DeserializationException("Value " + value.value() + " is not a <dateTime.iso8601>");
        }
        return ((DateValue) value).value();
    }

    @SuppressWarnings("unchecked") // Explicitly requested type by caller is assumed to be correct
    public <T> T asObject(int index, Class<T> type) {
        Value value = data.get(index);
        try {
            if (value instanceof StructValue || value instanceof ArrayValue) {
                return (T) value.asObject(context, type, null);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new DeserializationException("Value " + value.value() + " can't be converted into a " + type
                    .getSimpleName());
        }
        throw new DeserializationException("Value " + value.value() + " is not a <struct> or <array>");
    }

    @SuppressWarnings("unchecked") // Explicitly requested type by caller is assumed to be correct
    public <T> List<T> asList(int index, Class<?> type) {
        Value value = data.get(index);
        if (!(value instanceof ArrayValue)) {
            throw new DeserializationException("Value " + value.value() + " is not an <array>");
        }
        try {
            return (List<T>) value.asObject(context, List.class, type);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new DeserializationException("Value " + value.value() + " can't be converted into a List<" + type
                    .getSimpleName() + ">");
        }
    }

}
