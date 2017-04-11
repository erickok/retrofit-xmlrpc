package nl.nl2312.xmlrpc.deserialization;

import nl.nl2312.xmlrpc.types.Member;

import java.util.Date;
import java.util.List;

public final class StructMembers {

    private final DeserializationContext context;
    private final List<Member> rawMembers;

    public StructMembers(DeserializationContext context, List<Member> rawMembers) {
        this.context = context;
        this.rawMembers = rawMembers;
    }

    private StructMember get(String name) {
        for (Member rawMember : rawMembers) {
            if (rawMember.name.equals(name)) {
                return new StructMember(context, rawMember);
            }
        }
        return null;
    }

    public Boolean asBoolean(String name) {
        StructMember member = get(name);
        return member == null? null: member.asBoolean();
    }

    public Integer asInteger(String name) {
        StructMember member = get(name);
        return member == null? null: member.asInteger();
    }

    public Long asLong(String name) {
        StructMember member = get(name);
        return member == null? null: member.asLong();
    }

    public Double asDouble(String name) {
        StructMember member = get(name);
        return member == null? null: member.asDouble();
    }

    public String asString(String name) {
        StructMember member = get(name);
        return member == null? null: member.asString();
    }

    public Date asDate(String name) {
        StructMember member = get(name);
        return member == null? null: member.asDate();
    }

    public <T> T asObject(String name, Class<T> type) {
        StructMember member = get(name);
        return member == null? null: member.asObject(type);
    }

    public <T> List<T> asList(String name, Class<T> type) {
        StructMember member = get(name);
        return member == null? null: member.asList(type);
    }

}
