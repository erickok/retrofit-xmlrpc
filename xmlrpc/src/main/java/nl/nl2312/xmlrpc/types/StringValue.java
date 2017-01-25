package nl.nl2312.xmlrpc.types;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

import java.lang.reflect.Field;

@Root
public final class StringValue implements Value {

    public static final String CODE = "string";

    @Element(name = CODE)
    String value;

    public StringValue(String from) {
        this.value = from;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public void write(OutputNode node) throws Exception {
        OutputNode child = node.getChild(CODE);
        child.setValue(value);
    }

    @Override
    public Object asObject(Class<?> type) throws IllegalAccessException, InstantiationException {
        return value;
    }

    public static StringValue parse(String value) {
        return new StringValue(value);
    }

}
