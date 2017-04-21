package nl.nl2312.xmlrpc;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
