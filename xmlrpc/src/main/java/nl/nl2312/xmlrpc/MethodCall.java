package nl.nl2312.xmlrpc;

import nl.nl2312.xmlrpc.types.Member;
import nl.nl2312.xmlrpc.types.StructValue;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Root(name = "methodCall")
final class MethodCall {

    @Element
    String methodName;

    @ElementList
    List<Param> params;

    public static MethodCall create(String method, Object param) {
        MethodCall methodCall = new MethodCall();
        methodCall.methodName = method;

        if (param.getClass() == byte[].class) {
            methodCall.params = Collections.singletonList(Param.from(param));
        } else if (param.getClass().isArray()) {
            // Treat param as array of individual parameters
            int length = Array.getLength(param);
            methodCall.params = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                methodCall.params.add(Param.from(Array.get(param, i)));
            }
        } else if (param instanceof Iterable) {
            // Treat param as collection of individual parameters
            Iterator iter = ((Iterable) param).iterator();
            methodCall.params = new ArrayList<>();
            while (iter.hasNext()) {
                methodCall.params.add(Param.from(iter.next()));
            }
        } else if (param instanceof Map) {
            // Treat param as struct with map entries as members
            ArrayList<Member> members = new ArrayList<>();
            //noinspection unchecked Map entrySet is always a Set<Map.Entry>, we just don't know the Map.Entry type
            for (Map.Entry entry : ((Set<Map.Entry>) ((Map) param).entrySet())) {
                members.add(Member.create(entry.getKey().toString(), ValueConverter.getValue(entry.getValue())));
            }
            Param mapAsStruct = new Param();
            mapAsStruct.value = new StructValue(members);
            methodCall.params = Collections.singletonList(mapAsStruct);
        } else if (param instanceof Boolean || param instanceof Integer || param instanceof Long || param
                instanceof Double || param instanceof String) {
            methodCall.params = Collections.singletonList(Param.from(param));
        } else {
            // Treat param a an input object of which the fields represent the parameters
            Field[] fields = param.getClass().getDeclaredFields();
            methodCall.params = new ArrayList<>(fields.length);
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                try {
                    methodCall.params.add(Param.from(field.get(param)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(field.getName() + " could not be accessed to read XML-RPC param");
                }
            }
        }
        return methodCall;
    }

}
