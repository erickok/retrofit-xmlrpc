package nl.nl2312.xmlrpc.types;

import nl.nl2312.xmlrpc.deserialization.DeserializationContext;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

@Root
public final class DoubleValue implements Value {

    public static final String CODE = "double";

    @Element(name = CODE)
    double value;

    public DoubleValue(Double from) {
        this.value = from;
    }

    @Override
    public Double value() {
        return value;
    }

    @Override
    public void write(OutputNode node) throws Exception {
        OutputNode child = node.getChild(CODE);
        child.setValue(Double.toString(value));
    }

    @Override
    public Object asObject(DeserializationContext context, Class<?> type, Class<?> param) {
        return value;
    }

    public static DoubleValue parse(String value) {
        return new DoubleValue(Double.parseDouble(value));
    }

}
