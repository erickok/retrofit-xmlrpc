package nl.nl2312.xmlrpc;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static nl.nl2312.xmlrpc.Nothing.NOTHING;

public final class BugzillaIntegrationTest {

    private Retrofit retrofit;

    @Before
    public void setUp() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .addInterceptor(logging)
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://bugzilla.mozilla.org/")
                .addConverterFactory(XmlRpcConverterFactory.create())
                .build();
    }

    @Test
    public void bugzillaVersion() throws IOException {
        BugillaTestService service = retrofit.create(BugillaTestService.class);
        BugzillaVersion version = service.bugzillaVersion(NOTHING).execute().body();
        assertThat(version).isNotNull();
        assertThat(version.version).isNotEmpty();
    }

    @Test(expected = IOException.class)
    public void fault() throws IOException {
        BugillaTestService service = retrofit.create(BugillaTestService.class);
        service.nonExistentMethod(NOTHING).execute().body();
    }

    interface BugillaTestService {

        @XmlRpc("Bugzilla.version")
        @POST("xmlrpc.cgi")
        @Headers("Content-Type: text/xml")
        Call<BugzillaVersion> bugzillaVersion(@Body Nothing nothing);

        @XmlRpc("non.existent.method")
        @POST("xmlrpc.cgi")
        @Headers("Content-Type: text/xml")
        Call<String> nonExistentMethod(@Body Nothing nothing);

    }

    public static final class BugzillaVersion {

        public String version;

    }

}
