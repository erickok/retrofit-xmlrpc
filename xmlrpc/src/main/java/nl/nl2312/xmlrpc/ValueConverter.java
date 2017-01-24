package nl.nl2312.xmlrpc;

import nl.nl2312.xmlrpc.types.*;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.core.PersistenceException;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

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

    static Value getValue(InputNode node) throws Exception {
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
                StructValue structValue = new StructValue();
                structValue.members = new ArrayList<>();
                InputNode member = node.getNext();
                do {
                    String name = member.getNext().getValue();
                    Value value = getValue(member.getNext().getNext());
                    structValue.members.add(Member.create(name, value));
                    member = node.getNext();
                } while (member != null && member.getName().equals(Member.CODE));
                return structValue;
        }
        throw new PersistenceException(node.getName() + " is an unsupported type in XML-RPC");
    }

    static Value getValue(Object value) {
        if (value instanceof Integer) {
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
        } else if (value instanceof List) {
            List list = (List) value;
            List<Value> values = new ArrayList<>(list.size());
            for (Object o : list) {
                values.add(getValue(o));
            }
            return new ArrayValue(values);
        }
        throw new RuntimeException(value.getClass().getSimpleName() + " is an unsupported type in XML-RPC");
    }

}
