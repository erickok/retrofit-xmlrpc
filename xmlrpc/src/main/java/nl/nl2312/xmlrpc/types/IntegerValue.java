package nl.nl2312.xmlrpc.types;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

import java.lang.reflect.Field;

@Root
public final class IntegerValue implements Value {

    public static final String CODE = "i4";
    public static final String CODE_ALTERNTAIVE = "int";

    @Element(name = CODE)
    public int value;

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
    public Object asObject(Class<?> type) throws IllegalAccessException, InstantiationException {
        return value;
    }

    public static IntegerValue parse(String value) {
        return new IntegerValue(Integer.parseInt(value));
    }

}
