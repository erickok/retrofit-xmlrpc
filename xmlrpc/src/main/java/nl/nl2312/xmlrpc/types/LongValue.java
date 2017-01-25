package nl.nl2312.xmlrpc.types;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

import java.lang.reflect.Field;

@Root
public final class LongValue implements Value {

    public static final String CODE = "i8";

    @Element(name = CODE)
    long value;

    public LongValue(Long from) {
        this.value = from;
    }

    @Override
    public Long value() {
        return value;
    }

    @Override
    public void write(OutputNode node) throws Exception {
        OutputNode child = node.getChild(CODE);
        child.setValue(Long.toString(value));
    }

    @Override
    public Object asObject(Class<?> type) throws IllegalAccessException, InstantiationException {
        return value;
    }

    public static LongValue parse(String value) {
        return new LongValue(Long.parseLong(value));
    }

}
