package nl.nl2312.xmlrpc.types;

import nl.nl2312.xmlrpc.DeserialisationMode;
import nl.nl2312.xmlrpc.annotations.MemberName;
import nl.nl2312.xmlrpc.annotations.XmlRpcObject;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.stream.OutputNode;

import java.lang.reflect.*;
import java.util.List;

import static nl.nl2312.xmlrpc.types.StructValue.CODE;

@Root(name = CODE)
public final class StructValue implements Value {

    public static final String CODE = "struct";

    public StructValue(List<Member> members) {
        this.members = members;
    }

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
    public Object asObject(Class<?> type, Class<?> param) throws IllegalAccessException, InstantiationException {
        Object t;
        XmlRpcObject annotation = type.getAnnotation(XmlRpcObject.class);
        if (annotation != null && annotation.value() == DeserialisationMode.CREATOR) {
            // Deserialize using a custom creator
            if (annotation.creator() == void.class) {
                throw new RuntimeException("DeserialisationMode.CREATOR used for " + type.getSimpleName() + " but no " +
                        "creator type defined");
            }
            Object theCreator = annotation.creator().newInstance();
        } else if (annotation != null && annotation.value() == DeserialisationMode.CONSTRUCTOR) {
            // Deserialize using member to constructor parameters
            Constructor<?> ctor = null;
            for (Constructor<?> constructor : type.getConstructors()) {
                if (constructor.getParameterTypes().length == members.size()) {
                    ctor = constructor;
                    break;
                }
            }
            if (ctor == null) {
                throw new RuntimeException("No " + type.getSimpleName() + " constructor found with " + members.size()
                        + " parameters, matching the " + members.size() + " array elements");
            }
            try {
                Object[] params = new Object[members.size()];
                Type[] paramTypes = ctor.getGenericParameterTypes();
                for (int i = 0; i < members.size(); i++) {
                    Type paramType = paramTypes[i];
                    Class<?> paramTypeParam = null;
                    if (paramType instanceof ParameterizedType) {
                        paramTypeParam = (Class<?>) ((ParameterizedType) paramType).getActualTypeArguments()[0];
                    }
                    params[i] = members.get(i).value.asObject(ctor.getParameterTypes()[i], paramTypeParam);
                }
                t = ctor.newInstance(params);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("The " + type.getSimpleName() + " constructor with " + members.size()
                        + " parameters was suitable, but it is not accessible");
            }
        } else {
            // Deserialize using member to field mapping
            t = type.newInstance();
            for (Field field : type.getDeclaredFields()) {
                Member fieldMember = findMember(field);
                if (fieldMember != null && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field
                        .getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                    Class<?> targetFieldParam = null;
                    if (field.getGenericType() instanceof ParameterizedType) {
                        targetFieldParam = (Class<?>) ((ParameterizedType) field.getGenericType())
                                .getActualTypeArguments()[0];
                    }
                    field.set(t, fieldMember.value.asObject(field.getType(), targetFieldParam));
                }
            }
        }
        return t;
    }

    private Member findMember(Field field) {
        for (Member member : members) {
            MemberName memberName = field.getAnnotation(MemberName.class);
            if (memberName != null && memberName.value().equals(member.name)) {
                return member;
            } else if (memberName == null && field.getName().equals(member.name)) {
                return member;
            }
        }
        return null;
    }

}
