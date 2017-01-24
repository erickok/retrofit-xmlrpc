package nl.nl2312.xmlrpc;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class XmlRpcConverterFactory extends Converter.Factory {

    private final Serializer serializer;

    public static Converter.Factory create() {
        return new XmlRpcConverterFactory(new Persister(new AnnotationStrategy()));
    }

    private XmlRpcConverterFactory(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[]
            methodAnnotations, Retrofit retrofit) {
        XmlRpc annotation = getAnnotation(methodAnnotations);
        if (annotation == null) {
            return null;
        }
        return new XmlRpcRequestBodyConverter<>(serializer, annotation.value());
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        XmlRpc annotation = getAnnotation(annotations);
        if (annotation == null) {
            return null;
        }
        return new XmlRpcResponseBodyConverter<>(serializer, type);
    }

    private XmlRpc getAnnotation(Annotation[] annotations) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof XmlRpc) {
                    return (XmlRpc) annotation;
                }
            }
        }
        return null;
    }

}
