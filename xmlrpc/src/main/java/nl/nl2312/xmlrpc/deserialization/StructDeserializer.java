package nl.nl2312.xmlrpc.deserialization;

public interface StructDeserializer<T> {

    T deserialize(StructMembers members);

}
