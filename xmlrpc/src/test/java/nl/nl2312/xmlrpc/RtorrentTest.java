package nl.nl2312.xmlrpc;

import nl.nl2312.xmlrpc.annotations.XmlRpcObject;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static nl.nl2312.xmlrpc.Nothing.NOTHING;

public final class RtorrentTest {

    private static final String URL = "http://localhost:8008/";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    private Retrofit retrofit;

    @Before
    public void setUp() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .addInterceptor(logging)
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        return response
                                .request()
                                .newBuilder()
                                .addHeader("Authorization", Credentials.basic(USERNAME, PASSWORD))
                                .build();
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(URL)
                .addConverterFactory(XmlRpcConverterFactory.create())
                .build();
    }

    @Test
    public void listMethods() throws IOException {
        TestService service = retrofit.create(TestService.class);
        String[] execute = service.listMethods(NOTHING).execute().body();
        assertThat(execute).isNotEmpty();
    }

    @Test
    public void clientVersion() throws IOException {
        TestService service = retrofit.create(TestService.class);
        String execute = service.clientVersion(NOTHING).execute().body();
        assertThat(execute).isNotEmpty();
    }

    @Test
    public void downloadList() throws IOException {
        TestService service = retrofit.create(TestService.class);
        String[] execute = service.downloadList("").execute().body();
        assertThat(execute).isNotEmpty();
    }

    @Test
    public void torrents() throws IOException {
        TestService service = retrofit.create(TestService.class);
        Torrent[] execute = service.torrents(
                "",
                "main",
                "d.hash=",
                "d.get_name=",
                "d.get_size_bytes=").execute().body();
        assertThat(execute).isNotEmpty();
        assertThat(execute[0].hash).isNotEmpty();
        assertThat(execute[0].name).isNotEmpty();
        assertThat(execute[0].size).isGreaterThan(0L);
    }

    @Test
    public void torrents_viaConstructor() throws IOException {
        TestService service = retrofit.create(TestService.class);
        List<Torrent2> execute = service.torrents2(
                "",
                "main",
                "d.hash=",
                "d.get_name=",
                "d.get_size_bytes=").execute().body();
        assertThat(execute).isNotEmpty();
        assertThat(execute.get(0).hash).isNotEmpty();
        assertThat(execute.get(0).name).isNotEmpty();
        assertThat(execute.get(0).size).isGreaterThan(0L);
    }

    interface TestService {

        @XmlRpc("system.listMethods")
        @POST("/RPC2")
        Call<String[]> listMethods(@Body Nothing nothing);

        @XmlRpc("system.client_version")
        @POST("/RPC2")
        Call<String> clientVersion(@Body Nothing nothing);

        @XmlRpc("download_list")
        @POST("/RPC2")
        Call<String[]> downloadList(@Body String view);

        @XmlRpc("d.multicall2")
        @POST("/RPC2")
        Call<Torrent[]> torrents(@Body String... fields);

        @XmlRpc("d.multicall2")
        @POST("/RPC2")
        Call<List<Torrent2>> torrents2(@Body String... fields);

    }

    public static final class Torrent {

        static String staticField = "ignored";
        public final String finalField = "ignore";

        public String hash;
        public String name;
        public long size;

    }

    @XmlRpcObject(DeserialisationMode.CONSTRUCTOR)
    public static final class Torrent2 {

        static String staticField = "ignored";
        public final String finalField = "ignore";

        public final String hash;
        public final String name;
        public final long size;

        public Torrent2(String hash, String name, long size) {
            this.hash = hash;
            this.name = name;
            this.size = size;
        }

    }

}
