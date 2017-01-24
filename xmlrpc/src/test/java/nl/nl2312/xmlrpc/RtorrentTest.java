package nl.nl2312.xmlrpc;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public final class RtorrentTest {

    private static final String URL = "https://frdedi2tb008.xirvik.com/";
    private static final String USERNAME = "eric";
    private static final String PASSWORD = "development";

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
        String[] execute = service.listMethods(new Object()).execute().body();
        assertThat(execute).isNotEmpty();
    }

    @Test
    public void clientVersion() throws IOException {
        TestService service = retrofit.create(TestService.class);
        String execute = service.clientVersion(new Object()).execute().body();
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

    interface TestService {

        @XmlRpc("system.listMethods")
        @POST("/RPC2")
        Call<String[]> listMethods(@Body Object nothing);

        @XmlRpc("system.client_version")
        @POST("/RPC2")
        Call<String> clientVersion(@Body Object nothing);

        @XmlRpc("download_list")
        @POST("/RPC2")
        Call<String[]> downloadList(@Body String arg);

        @XmlRpc("d.multicall2")
        @POST("/RPC2")
        Call<Torrent[]> torrents(@Body String... args);

    }

    public static final class Torrent {

        public String hash;
        public String name;
        public long size;

    }

}
