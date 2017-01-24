package nl.nl2312.xmlrpc;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public final class SimpleIntegrationTest {

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
                .baseUrl("http://www.advogato.org/")
                .addConverterFactory(XmlRpcConverterFactory.create())
                .build();
    }

    @Test
    public void sumprodArray() throws IOException {
        SimpleTestService service = retrofit.create(SimpleTestService.class);
        int[] execute = service.sumprodArray(new SumProdArgs(2, 4)).execute().body();
        assertThat(execute).isNotEmpty();
        assertThat(execute[0]).isEqualTo(6);
        assertThat(execute[1]).isEqualTo(8);
    }

    @Test
    public void sumprod() throws IOException {
        SimpleTestService service = retrofit.create(SimpleTestService.class);
        SumProdResponse execute = service.sumprod(new SumProdArgs(2, 4)).execute().body();
        assertThat(execute).isNotNull();
        assertThat(execute.sum).isEqualTo(6);
        assertThat(execute.product).isEqualTo(8);
    }

    @Test
    public void capitalize() throws IOException {
        SimpleTestService service = retrofit.create(SimpleTestService.class);
        String execute = service.capitalize("Hello, World!").execute().body();
        assertThat(execute).isEqualTo("HELLO, WORLD!");
    }

    @Test(expected = IOException.class)
    public void fault() throws IOException {
        SimpleTestService service = retrofit.create(SimpleTestService.class);
        service.nonExistentMethod().execute().body();
    }

    interface SimpleTestService {

        @XmlRpc("test.sumprod")
        @POST("XMLRPC")
        Call<int[]> sumprodArray(@Body SumProdArgs args);

        @XmlRpc("test.sumprod")
        @POST("XMLRPC")
        Call<SumProdResponse> sumprod(@Body SumProdArgs args);

        @XmlRpc("test.capitalize")
        @POST("http://www.advogato.org/XMLRPC")
        Call<String> capitalize(@Body String s);

        @XmlRpc("non.existent.method")
        @POST("XMLRPC")
        Call<String> nonExistentMethod(@Body Object... nothing);

    }

    static final class SumProdArgs {

        final int a;
        final int b;

        SumProdArgs(int a, int b) {
            this.a = a;
            this.b = b;
        }

    }

    public static final class SumProdResponse {

        public int sum;
        public int product;

    }

}
