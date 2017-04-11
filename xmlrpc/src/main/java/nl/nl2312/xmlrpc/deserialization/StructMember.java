package nl.nl2312.xmlrpc.deserialization;

import nl.nl2312.xmlrpc.types.*;

import java.util.Date;
import java.util.List;

public final class StructMember {

    private final DeserializationContext context;
    private final Member rawMember;

    StructMember(DeserializationContext context, Member rawMember) {
        this.context = context;
        this.rawMember = rawMember;
    }

    public boolean asBoolean() {
        if (!(rawMember.value instanceof BooleanValue)) {
            throw new DeserializationException("Member " + rawMember.name + " is not a <boolean>");
        }
        return ((BooleanValue) rawMember.value).value();
    }

    public int asInteger() {
        if (!(rawMember.value instanceof IntegerValue)) {
            throw new DeserializationException("Member " + rawMember.name + " is not an <int> or <i4>");
        }
        return ((IntegerValue) rawMember.value).value();
    }

    public long asLong() {
        if (!(rawMember.value instanceof LongValue)) {
            throw new DeserializationException("Member " + rawMember.name + " is not an <i8>");
        }
        return ((LongValue) rawMember.value).value();
    }

    public double asDouble() {
        if (!(rawMember.value instanceof DoubleValue)) {
            throw new DeserializationException("Member " + rawMember.name + " is not an <double>");
        }
        return ((DoubleValue) rawMember.value).value();
    }

    public String asString() {
        if (!(rawMember.value instanceof StringValue)) {
            throw new DeserializationException("Member " + rawMember.name + " is not a <string>");
        }
        return ((StringValue) rawMember.value).value();
    }

    public Date asDate() {
        if (!(rawMember.value instanceof DateValue)) {
                throw new DeserializationException("Member " + rawMember.name + " is not a <dateTime.iso8601>");
        }
        return ((DateValue) rawMember.value).value();
    }

    @SuppressWarnings("unchecked") // Explicitly requested type by caller is assumed to be correct
    public <T> T asObject(Class<T> type) {
        try {
            if ((rawMember.value instanceof StructValue || rawMember.value instanceof ArrayValue)) {
                return (T) rawMember.value.asObject(context, type, null);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new DeserializationException("Member " + rawMember.name + " can't be converted into a " + type
                    .getSimpleName());
        }
        throw new DeserializationException("Member " + rawMember.name + " is not a <struct> or <array>");
    }

    @SuppressWarnings("unchecked") // Explicitly requested type by caller is assumed to be correct
    public <T> List<T> asList(Class<T> type) {
        if (!(rawMember.value instanceof ArrayValue)) {
            throw new DeserializationException("Member " + rawMember.name + " is not an <array>");
        }
        try {
            return (List<T>) rawMember.value.asObject(context, List.class, type);
        } catch (IllegalAccessException | InstantiationException | RuntimeException e) {
            throw new DeserializationException("Member " + rawMember.name + " can't be converted into a List<" +
                    type.getSimpleName() + ">");
        }
    }

}
