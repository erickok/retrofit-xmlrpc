package nl.nl2312.xmlrpc;

import nl.nl2312.xmlrpc.deserialization.MemberName;
import nl.nl2312.xmlrpc.types.*;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.core.PersistenceException;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ValueConverter implements Converter<Value> {

    @Override
    public Value read(InputNode node) throws Exception {
        return getValue(node.getNext());
    }

    @Override
    public void write(OutputNode node, Value value) throws Exception {
        value.write(node);
    }

    private static Value getValue(InputNode node) throws Exception {
        switch (node.getName()) {
            case IntegerValue.CODE:
                return IntegerValue.parse(node.getValue());
            case IntegerValue.CODE_ALTERNTAIVE:
                return IntegerValue.parse(node.getValue());
            case LongValue.CODE:
                return LongValue.parse(node.getValue());
            case DoubleValue.CODE:
                return DoubleValue.parse(node.getValue());
            case BooleanValue.CODE:
                return BooleanValue.parse(node.getValue());
            case StringValue.CODE:
                return StringValue.parse(node.getValue());
            case DateValue.CODE:
                return DateValue.parse(node.getValue());
            case ArrayValue.CODE:
                ArrayList<Value> data = new ArrayList<>();
                InputNode dataNode = node.getNext().getNext();
                while (dataNode != null && dataNode.getName().equals(Value.CODE)) {
                    data.add(getValue(dataNode.getNext()));
                    dataNode = node.getNext();
                }
                return new ArrayValue(data);
            case StructValue.CODE:
                List<Member> members = new ArrayList<>();
                InputNode member = node.getNext();
                do {
                    String name = member.getNext().getValue();
                    Value value = getValue(member.getNext().getNext());
                    members.add(Member.create(name, value));
                    member = node.getNext();
                } while (member != null && member.getName().equals(Member.CODE));
                return new StructValue(members);
        }
        throw new PersistenceException(node.getName() + " is an unsupported type in XML-RPC");
    }

    static Value getValue(Object value) {
        // TODO? Handle null as <nil />?
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<Value> values = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                values.add(getValue(Array.get(value, i)));
            }
            return new ArrayValue(values);
        } else if (value instanceof List) {
            List list = (List) value;
            List<Value> values = new ArrayList<>(list.size());
            for (Object o : list) {
                values.add(getValue(o));
            }
            return new ArrayValue(values);
        } else if (value instanceof Integer) {
            return new IntegerValue((Integer) value);
        } else if (value instanceof Long) {
            return new LongValue((Long) value);
        } else if (value instanceof Double) {
            return new DoubleValue((Double) value);
        } else if (value instanceof Boolean) {
            return new BooleanValue((Boolean) value);
        } else if (value instanceof String) {
            return new StringValue((String) value);
        } else if (value instanceof Date) {
            return new DateValue((Date) value);
        } else {
            try {
                List<Member> members = new ArrayList<>();
                for (Field field : value.getClass().getFields()) {
                    if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }
                    MemberName annotation = field.getAnnotation(MemberName.class);
                    String memberName = annotation != null ? annotation.value() : field.getName();
                    Object fieldValue = field.get(value);
                    if (fieldValue != null) {
                        members.add(Member.create(memberName, getValue(fieldValue)));
                    }
                }
                return new StructValue(members);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(value.getClass().getSimpleName() + " is an unsupported type in XML-RPC");
            }
        }
    }

}
