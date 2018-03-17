package nl.nl2312.xmlrpc.types;

import nl.nl2312.xmlrpc.deserialization.DeserializationContext;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

@Root
public final class IntegerValue implements Value {

    public static final String CODE = "i4";
    public static final String CODE_ALTERNTAIVE = "int";

    @Element(name = CODE)
    int value;

    public IntegerValue(Integer from) {
        this.value = from;
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public void write(OutputNode node) throws Exception {
        OutputNode child = node.getChild(CODE);
        child.setValue(Integer.toString(value));
    }

    @Override
    public Object asObject(DeserializationContext context, Class<?> type, Class<?> param) {
        return value;
    }

    public static IntegerValue parse(String value) {
        return new IntegerValue(Integer.parseInt(value));
    }

}
