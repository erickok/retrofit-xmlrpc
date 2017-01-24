package nl.nl2312.xmlrpc.types;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static nl.nl2312.xmlrpc.types.ArrayValue.CODE;

@Root(name = CODE)
public final class ArrayValue implements Value {

    public static final String CODE = "array";
    public static final String DATA = "data";

    public ArrayValue(List<Value> from) {
        this.data = from;
    }

    @ElementList
    public List<Value> data;

    @Override
    public List<Value> value() {
        return data;
    }

    @Override
    public void write(OutputNode node) throws Exception {
        OutputNode child = node.getChild(CODE);
        OutputNode dataNode = child.getChild(ArrayValue.DATA);
        for (Value datum : data) {
            datum.write(dataNode.getChild(Value.CODE));
        }
    }

    @Override
    public Object asObject(Class<?> type) throws IllegalAccessException, InstantiationException {
        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            if (componentType.equals(Boolean.TYPE)) {
                boolean[] values = new boolean[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((BooleanValue) data.get(i)).value;
                }
                return values;
            } else if (componentType.equals(Integer.TYPE)) {
                int[] values = new int[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((IntegerValue) data.get(i)).value;
                }
                return values;
            } else if (componentType.equals(Long.TYPE)) {
                long[] values = new long[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((LongValue) data.get(i)).value;
                }
                return values;
            } else if (componentType.equals(Double.TYPE)) {
                double[] values = new double[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((DoubleValue) data.get(i)).value;
                }
                return values;
            } else if (componentType.equals(String.class)) {
                String[] values = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((StringValue) data.get(i)).value;
                }
                return values;
            } else if (data.size() > 0 && data.get(0) instanceof StructValue) {
                Object values = Array.newInstance(componentType, data.size());
                for (int i = 0; i < data.size(); i++) {
                    Array.set(values, i, data.get(i).asObject(componentType));
                }
                return values;
            } else if (data.size() > 0 && data.get(0) instanceof ArrayValue) {
                Object values = Array.newInstance(componentType, data.size());
                for (int i = 0; i < data.size(); i++) {
                    Array.set(values, i, data.get(i).asObject(componentType));
                }
                return values;
            } else {
                throw new RuntimeException(componentType.getSimpleName() + "[] types are not supported by XML-RPC");
            }
        } else if (type.isAssignableFrom(List.class)) {
            List list = new ArrayList();
            for (Value datum : data) {
                list.add(datum.asObject(Object.class));
            }
            return list;
        } else {
            Field[] fields = type.getFields();
            if (data.size() != fields.length) {
                throw new RuntimeException("Tried to load array with " + data.size() + " elements into type " + type
                        .getSimpleName() + " with " + fields.length + " fields");
            }
            Object t = type.newInstance();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.set(t, data.get(i).asObject(field.getType()));
            }
            return t;
        }
    }

}
