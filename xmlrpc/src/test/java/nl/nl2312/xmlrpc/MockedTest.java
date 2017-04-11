package nl.nl2312.xmlrpc;

import nl.nl2312.xmlrpc.deserialization.ArrayDeserializer;
import nl.nl2312.xmlrpc.deserialization.ArrayValues;
import nl.nl2312.xmlrpc.deserialization.StructDeserializer;
import nl.nl2312.xmlrpc.deserialization.StructMembers;
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
import static nl.nl2312.xmlrpc.Nothing.NOTHING;

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
                .addConverterFactory(XmlRpcConverterFactory.builder()
                        .addStructDeserializer(PersonWithConstructor.class, new
                                StructDeserializer<PersonWithConstructor>() {
                                    @Override
                                    public PersonWithConstructor deserialize(StructMembers structMembers) {
                                        return new PersonWithConstructor(
                                                structMembers.asString("name"),
                                                structMembers.asObject("mother", Person.class),
                                                structMembers.asObject("father", Person.class),
                                                structMembers.asList("siblings", Person.class));
                                    }
                                })
                        .addArrayDeserializer(PostWithConstructor.class, new ArrayDeserializer<PostWithConstructor>() {
                            @Override
                            public PostWithConstructor deserialize(ArrayValues values) {
                                return new PostWithConstructor(
                                        values.asInteger(0),
                                        values.asString(1),
                                        values.asDate(2));
                            }
                        })
                        .create())
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

    @Test
    public void family() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .setBody(BODY_FAMILY));

        TestService service = retrofit.create(TestService.class);
        Person execute = service.family(NOTHING).execute().body();
        server.takeRequest();
        assertThat(execute).isNotNull();
        assertThat(execute.name).isEqualTo("Me");
        assertThat(execute.father.name).isEqualTo("Dad");
        assertThat(execute.father.father.name).isEqualTo("Grandpa");
        assertThat(execute.mother.name).isEqualTo("Mom");
        assertThat(execute.siblings[0].name).isEqualTo("Sis");
        assertThat(execute.siblings[1].name).isEqualTo("Bro");
    }

    @Test
    public void family_structDeserializer() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .setBody(BODY_FAMILY));

        TestService service = retrofit.create(TestService.class);
        PersonWithConstructor execute = service.familyWithConstructor(NOTHING).execute().body();
        server.takeRequest();
        assertThat(execute).isNotNull();
        assertThat(execute.name).isEqualTo("Me");
        assertThat(execute.father.name).isEqualTo("Dad");
        assertThat(execute.father.father.name).isEqualTo("Grandpa");
        assertThat(execute.mother.name).isEqualTo("Mom");
        assertThat(execute.siblings.get(0).name).isEqualTo("Sis");
        assertThat(execute.siblings.get(1).name).isEqualTo("Bro");
    }

    @Test
    public void addChildren() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .setBody("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                        "<methodResponse>\n" +
                        "   <params>\n" +
                        //"      <param>\n" +
                        //"         <value><nil /></value>\n" +
                        //"      </param>\n" +
                        "   </params>\n" +
                        "</methodResponse>"));

        TestService service = retrofit.create(TestService.class);
        List<Person> boys = Arrays.asList(new Person("Boy"), new Person("Garçon"));
        Person[] girls = new Person[]{new Person("Girl"), new Person("Fille")};
        Void execute = service.addChildren(new AddChildrenArgs(boys, girls)).execute().body();
        server.takeRequest();
        assertThat(execute).isNull();
    }

    @Test
    public void posts() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .setBody(BODY_POSTS));

        TestService service = retrofit.create(TestService.class);
        List<Post> posts = service.posts(NOTHING).execute().body();
        server.takeRequest();
        assertThat(posts).isNotEmpty();
        assertThat(posts.size()).isEqualTo(2);
        assertThat(posts.get(0).id).isEqualTo(10);
        assertThat(posts.get(0).name).isEqualTo("retrofit");
        assertThat(posts.get(0).published).isEqualTo(new Date(1485300461000L));
        assertThat(posts.get(1).id).isEqualTo(11);
        assertThat(posts.get(1).name).isEqualTo("xmlrpc");
        assertThat(posts.get(1).published).isEqualTo(new Date(1485184689000L));
    }

    @Test
    public void posts_arrayDeserializer() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .setBody(BODY_POSTS));

        TestService service = retrofit.create(TestService.class);
        PostWithConstructor[] posts = service.postsWithConstructor(NOTHING).execute().body();
        server.takeRequest();
        assertThat(posts).isNotEmpty();
        assertThat(posts.length).isEqualTo(2);
        assertThat(posts[0].id).isEqualTo(10);
        assertThat(posts[0].name).isEqualTo("retrofit");
        assertThat(posts[0].published).isEqualTo(new Date(1485300461000L));
        assertThat(posts[1].id).isEqualTo(11);
        assertThat(posts[1].name).isEqualTo("xmlrpc");
        assertThat(posts[1].published).isEqualTo(new Date(1485184689000L));
    }

    interface TestService {

        @XmlRpc("system.listMethods")
        @POST("/mocked")
        Call<String[]> listMethods();

        @XmlRpc("multiply")
        @POST("/mocked")
        Call<Integer> multiply(@Body MultiplicationArgs args);

        @XmlRpc("dateSubstract")
        @POST("/mocked")
        Call<Long> dateSubstract(@Body List<Date> args);

        @XmlRpc("family")
        @POST("/mocked")
        Call<Person> family(@Body Nothing nothing);

        @XmlRpc("family")
        @POST("/mocked")
        Call<PersonWithConstructor> familyWithConstructor(@Body Nothing nothing);

        @XmlRpc("addChildren")
        @POST("/mocked")
        Call<Void> addChildren(@Body AddChildrenArgs args);

        @XmlRpc("posts")
        @POST("/mocked")
        Call<List<Post>> posts(@Body Nothing nothing);

        @XmlRpc("posts")
        @POST("/mocked")
        Call<PostWithConstructor[]> postsWithConstructor(@Body Nothing nothing);

    }

    static final class MultiplicationArgs {

        final int a;
        final int b;

        MultiplicationArgs(int a, int b) {
            this.a = a;
            this.b = b;
        }

    }

    static final class AddChildrenArgs {

        final List<Person> boys;
        final Person[] girls;

        public AddChildrenArgs(List<Person> boys, Person[] girls) {
            this.boys = boys;
            this.girls = girls;
        }

    }

    public static final class Person {

        public String name;
        public PersonWithConstructor mother;
        public PersonWithConstructor father;
        public PersonWithConstructor[] siblings;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

    }

    public static class PersonWithConstructor {

        public final String name;
        public final Person mother;
        public final Person father;
        public final List<Person> siblings;

        public PersonWithConstructor(String name, Person mother, Person father, List<Person> siblings) {
            this.name = name;
            this.mother = mother;
            this.father = father;
            this.siblings = siblings;
        }

    }

    public static class Post {

        public int id;
        public String name;
        public Date published;

    }

    public static class PostWithConstructor {

        public final int id;
        public final String name;
        public final Date published;

        public PostWithConstructor(int id, String name, Date published) {
            this.id = id;
            this.name = name;
            this.published = published;
        }

    }

    private static final String BODY_FAMILY = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
            "<methodResponse>\n" +
            "   <params>\n" +
            "      <param>\n" +
            "         <value>\n" +
            "           <struct>\n" +
            "             <member>\n" +
            "               <name>name</name>\n" +
            "               <value><string>Me</string></value>\n" +
            "             </member>\n" +
            "             <member>\n" +
            "               <name>father</name>\n" +
            "               <value>\n" +
            "                 <struct>\n" +
            "                   <member>\n" +
            "                     <name>name</name>\n" +
            "                     <value><string>Dad</string></value>\n" +
            "                   </member>\n" +
            "                   <member>\n" +
            "                     <name>father</name>\n" +
            "                     <value>\n" +
            "                       <struct>\n" +
            "                         <member>\n" +
            "                           <name>name</name>\n" +
            "                           <value><string>Grandpa</string></value>\n" +
            "                         </member>\n" +
            "                       </struct>\n" +
            "                     </value>\n" +
            "                   </member>\n" +
            "                 </struct>\n" +
            "               </value>\n" +
            "             </member>\n" +
            "             <member>\n" +
            "               <name>mother</name>\n" +
            "               <value>\n" +
            "                 <struct>\n" +
            "                   <member>\n" +
            "                     <name>name</name>\n" +
            "                     <value><string>Mom</string></value>\n" +
            "                   </member>\n" +
            "                 </struct>\n" +
            "               </value>\n" +
            "             </member>\n" +
            "             <member>\n" +
            "               <name>siblings</name>\n" +
            "               <value>\n" +
            "                 <array>\n" +
            "                   <data>\n" +
            "                     <value>\n" +
            "                       <struct>\n" +
            "                         <member>\n" +
            "                           <name>name</name>\n" +
            "                           <value><string>Sis</string></value>\n" +
            "                         </member>\n" +
            "                       </struct>\n" +
            "                     </value>\n" +
            "                     <value>\n" +
            "                       <struct>\n" +
            "                         <member>\n" +
            "                           <name>name</name>\n" +
            "                           <value><string>Bro</string></value>\n" +
            "                         </member>\n" +
            "                       </struct>\n" +
            "                     </value>\n" +
            "                   </data>\n" +
            "                 </array>\n" +
            "               </value>\n" +
            "             </member>\n" +
            "           </struct>\n" +
            "         </value>\n" +
            "      </param>\n" +
            "   </params>\n" +
            "</methodResponse>";

    private static final String BODY_POSTS = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
            "<methodResponse>\n" +
            "   <params>\n" +
            "       <param>\n" +
            "          <value>\n" +
            "             <array>\n" +
            "                 <data>\n" +
            "                     <value>\n" +
            "                         <array>\n" +
            "                             <data>\n" +
            "                                 <value><i4>10</i4></value>\n" +
            "                                 <value><string>retrofit</string></value>\n" +
            "                                 <value><dateTime.iso8601>2017-01-24T23:27:41+00:00</dateTime" +
            ".iso8601></value>\n" +
            "                             </data>\n" +
            "                         </array>\n" +
            "                     </value>\n" +
            "                     <value>\n" +
            "                         <array>\n" +
            "                             <data>\n" +
            "                                 <value><i4>11</i4></value>\n" +
            "                                 <value><string>xmlrpc</string></value>\n" +
            "                                 <value><dateTime.iso8601>2017-01-23T15:18:09+00:00</dateTime" +
            ".iso8601></value>\n" +
            "                             </data>\n" +
            "                         </array>\n" +
            "                     </value>\n" +
            "                 </data>\n" +
            "             </array>\n" +
            "          </value>\n" +
            "       </param>\n" +
            "   </params>\n" +
            "</methodResponse>";

}
