package nl.nl2312.xmlrpc;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import org.simpleframework.xml.Serializer;
import retrofit2.Converter;

import java.io.IOException;
import java.io.OutputStreamWriter;

final class XmlRpcRequestBodyConverter<T> implements Converter<T, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/xml; charset=UTF-8");
    private static final String CHARSET = "UTF-8";

    private final Serializer serializer;
    private final String method;

    XmlRpcRequestBodyConverter(Serializer serializer, String method) {
        this.serializer = serializer;
        this.method = method;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        MethodCall methodCall = MethodCall.create(method, value);
        Buffer buffer = new Buffer();
        try {
            OutputStreamWriter osw = new OutputStreamWriter(buffer.outputStream(), CHARSET);
            serializer.write(methodCall, osw);
            osw.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }

}
