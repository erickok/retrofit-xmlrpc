package nl.nl2312.xmlrpc;

import nl.nl2312.xmlrpc.deserialization.MemberName;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

public final class WordpressIntegrationTest {

    private static final String URL = "";
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
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(URL)
                .addConverterFactory(XmlRpcConverterFactory.create())
                .build();
    }

    @Test
    public void posts() throws IOException {
        TestService service = retrofit.create(TestService.class);
        Post[] execute = service.posts(new PostsArgs(0, USERNAME, PASSWORD)).execute().body();
        assertThat(execute).isNotEmpty();
        Post first = execute[0];
        assertThat(first).isNotNull();
        assertThat(first.postId).isNotNull();
        assertThat(first.postTitle).isNotNull();
        assertThat(first.postDate).isNotNull();
        assertThat(first.menuOrder).isAtLeast(0);
        assertThat(first.menuOrder2).isAtLeast(0);
        assertThat(first.sticky).isFalse();
        assertThat(first.sticky2).isFalse();
    }

    interface TestService {

        @XmlRpc("wp.getPosts")
        @POST("/xmlrpc.php")
        Call<Post[]> posts(@Body PostsArgs args);

    }

    static final class PostsArgs {

        final int blogId;
        final String username;
        final String password;

        PostsArgs(int blogId, String username, String password) {
            this.blogId = blogId;
            this.username = username;
            this.password = password;
        }

    }

    public static final class Post {

        @MemberName("post_id")
        public String postId;
        @MemberName("post_title")
        public String postTitle;
        @MemberName("post_date")
        public Date postDate;
        @MemberName("menu_order")
        public Integer menuOrder;
        @MemberName("menu_order")
        public int menuOrder2;
        public Boolean sticky;
        @MemberName("sticky")
        public boolean sticky2;

    }

}
