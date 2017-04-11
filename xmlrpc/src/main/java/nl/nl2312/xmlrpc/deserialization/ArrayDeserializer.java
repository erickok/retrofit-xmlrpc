package nl.nl2312.xmlrpc.deserialization;

public interface ArrayDeserializer<T> {

    T deserialize(ArrayValues values);

}
