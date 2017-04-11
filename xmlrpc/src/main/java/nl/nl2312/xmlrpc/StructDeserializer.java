package nl.nl2312.xmlrpc;

import nl.nl2312.xmlrpc.types.Member;

import java.util.List;

public interface StructDeserializer<T> {

    T deserialize(List<Member> members);

}
