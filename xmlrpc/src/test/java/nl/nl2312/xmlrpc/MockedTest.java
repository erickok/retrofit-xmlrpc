package nl.nl2312.xmlrpc;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public final class MockedTest {

    private Retrofit retrofit;
    private MockWebServer server;

    @Before
    public void setUp() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .addInterceptor(logging)
                .build();

        server = new MockWebServer();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(server.url("/"))
                .addConverterFactory(XmlRpcConverterFactory.create())
                .build();
    }

    @Test
    public void listMethods() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .setBody("<?xml version=\"1.0\"?>\n" +
                        "<methodResponse>\n" +
                        "  <params>\n" +
                        "    <param>\n" +
                        "        <value>" +
                        "            <array>" +
                        "                <data>" +
                        "                    <value><string>system.listMethods</string></value>\n" +
                        "                    <value><string>multiply</string></value>\n" +
                        "                </data>" +
                        "            </array>" +
                        "        </value>" +
                        "    </param>\n" +
                        "  </params>\n" +
                        "</methodResponse>"));

        TestService service = retrofit.create(TestService.class);
        String[] execute = service.listMethods().execute().body();
        server.takeRequest();
        assertThat(execute).isNotEmpty();
    }

    @Test
    public void multiply() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .setBody("<?xml version=\"1.0\"?>\n" +
                        "<methodResponse>\n" +
                        "  <params>\n" +
                        "    <param>\n" +
                        "        <value><i4>6</i4></value>\n" +
                        "    </param>\n" +
                        "  </params>\n" +
                        "</methodResponse>"));

        TestService service = retrofit.create(TestService.class);
        Integer execute = service.multiply(new MultiplicationArgs(2, 3)).execute().body();
        server.takeRequest();
        assertThat(execute).isEqualTo(6);
    }

    @Test
    public void dateSubstract() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .setBody("<?xml version=\"1.0\"?>\n" +
                        "<methodResponse>\n" +
                        "  <params>\n" +
                        "    <param>\n" +
                        "        <value><i8>60000</i8></value>\n" +
                        "    </param>\n" +
                        "  </params>\n" +
                        "</methodResponse>"));

        TestService service = retrofit.create(TestService.class);
        Date now = new Date();
        Date inOneMinute = new Date(now.getTime() + 60_000);
        Long execute = service.dateSubstract(Arrays.asList(inOneMinute, now)).execute().body();
        server.takeRequest();
        assertThat(execute).isEqualTo(60_000);
    }

    interface TestService {

        @XmlRpc("system.listMethods")
        @POST("/RPC2")
        Call<String[]> listMethods();

        @XmlRpc("multiply")
        @POST("/RPC2")
        Call<Integer> multiply(@Body MultiplicationArgs args);

        @XmlRpc("dateSubstract")
        @POST("/RPC2")
        Call<Long> dateSubstract(@Body List<Date> args);

    }

    static final class MultiplicationArgs {

        final int a;
        final int b;

        MultiplicationArgs(int a, int b) {
            this.a = a;
            this.b = b;
        }

    }

}
