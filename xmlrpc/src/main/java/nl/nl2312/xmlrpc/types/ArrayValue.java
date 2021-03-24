package nl.nl2312.xmlrpc.types;

import nl.nl2312.xmlrpc.deserialization.ArrayValues;
import nl.nl2312.xmlrpc.deserialization.DeserializationContext;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

import java.lang.reflect.*;
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
    List<Value> data;

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
    public Object asObject(DeserializationContext context, Class<?> type, Class<?> param) throws
            IllegalAccessException, InstantiationException {
        if (context.hasArrayDeserializer(type)) {
            return context.arrayDeserializer(type).deserialize(new ArrayValues(context, data));
        } else if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            if (componentType.equals(Boolean.TYPE)) {
                boolean[] values = new boolean[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((BooleanValue) data.get(i)).value();
                }
                return values;
            } else if (componentType.equals(Integer.TYPE)) {
                int[] values = new int[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((IntegerValue) data.get(i)).value();
                }
                return values;
            } else if (componentType.equals(Long.TYPE)) {
                long[] values = new long[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((LongValue) data.get(i)).value();
                }
                return values;
            } else if (componentType.equals(Double.TYPE)) {
                double[] values = new double[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((DoubleValue) data.get(i)).value();
                }
                return values;
            } else if (componentType.equals(String.class)) {
                String[] values = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    values[i] = ((StringValue) data.get(i)).value();
                }
                return values;
            } else if (data.size() > 0 && data.get(0) instanceof StructValue) {
                Object values = Array.newInstance(componentType, data.size());
                for (int i = 0; i < data.size(); i++) {
                    Array.set(values, i, data.get(i).asObject(context, componentType, null));
                }
                return values;
            } else if (data.size() > 0 && data.get(0) instanceof ArrayValue) {
                Object values = Array.newInstance(componentType, data.size());
                for (int i = 0; i < data.size(); i++) {
                    Array.set(values, i, data.get(i).asObject(context, componentType, null));
                }
                return values;
            } else if (data.isEmpty()) {
                return Array.newInstance(componentType, 0);
            } else {
                throw new RuntimeException(componentType.getSimpleName() + "[] types are not supported by XML-RPC");
            }
        } else if (List.class.isAssignableFrom(type)) {
            List list = new ArrayList();
            for (Value datum : data) {
                //noinspection unchecked Raw List used, but type is enforced via explicit parameter class
                list.add(datum.asObject(context, param, null));
            }
            return list;
        } else {
            // Deserialize using value to field mapping
            Field[] fields = type.getFields();
            List<Field> targetFields = new ArrayList<>(fields.length);
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()) && !Modifier
                        .isTransient(field.getModifiers())) {
                    targetFields.add(field);
                }
            }
            if (data.size() != targetFields.size()) {
                throw new RuntimeException("Tried to load array with " + data.size() + " elements into type " + type
                        .getSimpleName() + " with " + targetFields.size() + " suitable fields (non-static, " +
                        "non-final, non-transient)");
            }
            Object t = type.newInstance();
            for (int i = 0; i < targetFields.size(); i++) {
                Field targetField = targetFields.get(i);
                Class<?> targetFieldParam = null;
                Type targetFieldType = targetField.getGenericType();
                if (targetFieldType instanceof ParameterizedType) {
                    targetFieldParam = (Class<?>) ((ParameterizedType) targetFieldType).getActualTypeArguments()[0];
                }
                targetField.set(t, data.get(i).asObject(context, targetField.getType(), targetFieldParam));
            }
            return t;
        }
    }

}
