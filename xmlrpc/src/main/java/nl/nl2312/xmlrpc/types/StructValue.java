package nl.nl2312.xmlrpc.types;

import nl.nl2312.xmlrpc.MemberName;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import static nl.nl2312.xmlrpc.types.StructValue.CODE;

@Root(name = CODE)
public final class StructValue implements Value {

    public static final String CODE = "struct";

    @ElementList(inline = true)
    public List<Member> members;

    @Override
    public List<Member> value() {
        return members;
    }

    @Override
    public void write(OutputNode node) throws Exception {
        OutputNode struct = node.getChild(CODE);
        for (Member member : members) {
            member.write(struct);
        }
    }

    @Override
    public Object asObject(Class<?> type) throws IllegalAccessException, InstantiationException {
        Object t = type.newInstance();
        for (Field field : type.getDeclaredFields()) {
            Member fieldMember = findMember(field);
            if (fieldMember != null) {
                field.set(t, fieldMember.value.value());
            }
        }
        return t;
    }

    private Member findMember(Field field) {
        for (Member member : members) {
            if (field.getName().equals(member.name)) {
                return member;
            }
            // TODO Optimize initialization of annotations
            MemberName memberName = findFieldAnnotation(field.getAnnotations());
            if (memberName != null && memberName.value().equals(member.name)) {
                return member;
            }
        }
        return null;
    }

    private MemberName findFieldAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof MemberName) {
                return (MemberName) annotation;
            }
        }
        return null;
    }

}