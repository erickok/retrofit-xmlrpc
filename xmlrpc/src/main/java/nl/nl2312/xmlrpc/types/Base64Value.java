package nl.nl2312.xmlrpc.types;

import net.iharder.Base64;
import nl.nl2312.xmlrpc.deserialization.DeserializationContext;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.PersistenceException;
import org.simpleframework.xml.stream.OutputNode;

import java.io.IOException;

@Root
public final class Base64Value implements Value {

    public static final String CODE = "base64";

    @Element(name = CODE)
    byte[] value;

    public Base64Value(byte[] from) {
        this.value = from;
    }

    @Override
    public byte[] value() {
        return value;
    }

    @Override
    public void write(OutputNode node) throws Exception {
        OutputNode child = node.getChild(CODE);
        child.setValue(Base64.encodeBytes(value));
    }

    @Override
    public Object asObject(DeserializationContext context, Class<?> type, Class<?> param) throws
            IllegalAccessException, InstantiationException {
        return value;
    }

    public static Base64Value parse(String value) throws PersistenceException {
        try {
            return new Base64Value(Base64.decode(value));
        } catch (IOException e) {
            throw new PersistenceException("Cannot Base64-decode: " + value);
        }
    }

}
