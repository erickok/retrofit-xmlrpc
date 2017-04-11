package nl.nl2312.xmlrpc.types;

import nl.nl2312.xmlrpc.deserialization.DeserializationContext;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

@Root
public final class BooleanValue implements Value {

    public static final String CODE = "boolean";
    public static final String FALSE = "0";
    public static final String TRUE = "1";

    @Element(name = CODE)
    boolean value;

    public BooleanValue(Boolean from) {
        this.value = from;
    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public void write(OutputNode node) throws Exception {
        OutputNode child = node.getChild(CODE);
        child.setValue(value ? TRUE : FALSE);
    }

    @Override
    public Object asObject(DeserializationContext context, Class<?> type, Class<?> param) throws IllegalAccessException, InstantiationException {
        return value;
    }

    public static BooleanValue parse(String value) {
        return new BooleanValue(Integer.parseInt(value) == 1);
    }

}
