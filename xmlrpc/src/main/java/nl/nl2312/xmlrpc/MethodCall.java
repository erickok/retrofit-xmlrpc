package nl.nl2312.xmlrpc;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Root(name = "methodCall")
public final class MethodCall {

    @Element
    public String methodName;
    @ElementList
    public List<Param> params;

    public static MethodCall create(String method, Object params) {
        MethodCall methodCall = new MethodCall();
        methodCall.methodName = method;

        if (params.getClass().isArray()) {
            // Treat params as array of individual parameters
            int length = Array.getLength(params);
            methodCall.params = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                methodCall.params.add(Param.from(Array.get(params, i)));
            }
        } else if (params instanceof Iterable) {
            // Treat params as collection of individual parameters
            Iterator iter = ((Iterable) params).iterator();
            methodCall.params = new ArrayList<>();
            while (iter.hasNext()) {
                methodCall.params.add(Param.from(iter.next()));
            }
        } else if (params instanceof Boolean || params instanceof Integer || params instanceof Long || params
                instanceof Double || params instanceof String) {
            methodCall.params = Collections.singletonList(Param.from(params));
        } else {
            // Treat params a an input object of which the fields represent the parameters
            Field[] fields = params.getClass().getDeclaredFields();
            methodCall.params = new ArrayList<>(fields.length);
            for (Field field : fields) {
                try {
                    methodCall.params.add(Param.from(field.get(params)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(field.getName() + " could not be accessed to read XML-RPC param");
                }
            }
        }
        return methodCall;
    }

}
