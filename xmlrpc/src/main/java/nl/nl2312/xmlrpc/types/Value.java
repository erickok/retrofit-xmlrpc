package nl.nl2312.xmlrpc.types;

import nl.nl2312.xmlrpc.deserialization.DeserializationContext;
import org.simpleframework.xml.stream.OutputNode;

public interface Value {

    String CODE = "value";

    Object value();

    void write(OutputNode node) throws Exception;

    Object asObject(DeserializationContext context, Class<?> type, Class<?> param)
            throws IllegalAccessException, InstantiationException;

}
