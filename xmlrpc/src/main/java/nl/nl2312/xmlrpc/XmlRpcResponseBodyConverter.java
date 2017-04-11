package nl.nl2312.xmlrpc;

import nl.nl2312.xmlrpc.deserialization.DeserializationContext;
import okhttp3.ResponseBody;
import org.simpleframework.xml.Serializer;
import retrofit2.Converter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

final class XmlRpcResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Serializer serializer;
    private final DeserializationContext context;
    private final Type returnType;

    XmlRpcResponseBodyConverter(Serializer serializer, DeserializationContext context, Type returnType) {
        this.serializer = serializer;
        this.context = context;
        this.returnType = returnType;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        MethodResponse response;
        try {
            response = serializer.read(MethodResponse.class, value.charStream());
        } catch (Exception e) {
            throw new IOException("Unexpected XML-RPC response body", e);
        }

        return create(returnType, response.params);
    }

    @SuppressWarnings("unchecked")
    private T create(Type returnType, List<Param> params) {
        if (params.size() == 0) {
            return null;
        }
        if (params.size() > 1) {
            throw new RuntimeException("Unexpected XML-RPC response: methodResponse can only contain a single <param>");
        }
        Class<T> clz;
        Class<T> param = null;
        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            clz = (Class<T>) parameterizedType.getRawType();
            param = (Class<T>) parameterizedType.getActualTypeArguments()[0];
        } else {
            clz = (Class<T>) returnType;
        }
        try {
            return (T) params.get(0).value.asObject(context, clz, param);
        } catch (InstantiationException e) {
            throw new RuntimeException("Can't construct instance of " + clz.getSimpleName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("No access to constructor or field of " + clz.getSimpleName(), e);
        }
    }

}
