# retrofit-xmlrpc
Typed XML-RPC support for Retrofit

## Installation
Annotate your Retrofit service method `@XmlRpc`.
```java
interface MathService {
    @XmlRpc("test.sumprod") @POST("/XMLRPC")
    Call<SumProdResponse> sumprod(@Body SumProdArgs args);
}
```
Add the `XmlRpcConverterFactory` to your Retrofit instance.
```java
Retrofit retrofit = new Retrofit.Builder()
    ...
    .addConverterFactory(XmlRpcConverterFactory.create())
    .build();
```
Create your service and call the method.
```java
MathService service = retrofit.create(MathService.class);
SumProdResponse sumProd = service.sumprod(new SumProdArgs(2, 4)).execute().body();
// sumProd.sum --> 6
// sumProd.product --> 8
```

## Type support

Direct conversion is supported (as request parameters in a `<methodCall>` and as response parameter in a `<methodResponse>`) for:
- `<boolean>` --> `Boolean`
- `<i4>` and `<int>` --> `Integer`
- `<i8>` --> `Long`
- `<double>` --> `Double`
- `<string>` --> `String`
- `<dateTime.iso8601>` --> `Date`
Support for `<base64>` is not yet added.

### Structs
A `<struct>` response param or `<struct>`s as part of an array will be read into your target POJO. The `<member>` names are mapped against the POJO fields (which need to be accessible and writable).
```xml
<struct>
    <member>
        <name>file_nr</name>
        <value><string>DF101364</string></value>
    </member>
    <member>
        <name>case</name>
        <value><string>Deep Throat</string></value>
    </member>
</struct>
```
If it is desired or needed (due to reseved names) to map member `<name>`s to different fields, use the `@MemberName` annotation.
```java
public class File {
    @MemberName("file_nr") String fileNr; // --> "DF101364"
    @MemberName("case") String caseName; // --> "Deep Throat"
}

```

### Arrays
An `<array>` response param can be converted into three distinct targets.

#### Arrays of primitives
```xml
<array>
    <data>
        <value><string>system.listMethods</string></value>
        <value><string>posts.list</string></value>
        <value><string>posts.get</string></value>
    </data>
</array>
```
```java
interface BlogService {
    @XmlRpc("system.listMethods") @POST("/xmlprc")
    Call<String[]> listMethods(@Body Nothing nothing);
}
BlogService service = retrofit.create(BlogService.class);
String[] methods = service.listMethods(NOTHING).execute().body();
// methods[0] --> "system.listMethods"
// methods[0] --> "posts.list"
// methods[0] --> "posts.get"
```
**NOTE:** Since we need a request `@Body` to convert and Retrofit does not allow `null` to be passed, we the `Nothing.NOTHING` value provided by the library, which is effectively ignored. Suggestions for improvements are much welcomed.

#### Arrays of `<struct>`s
```xml
<array>
    <data>
        <value>
            <struct>
                <member>
                    <name>id</name>
                    <value><i4>10</i4></value>
                </member>
                <member>
                    <name>title</name>
                    <value><string>retrofit</string></value>
                </member>
                <member>
                    <name>time</name>
                    <value><dateTime.iso8601>2017-01-24T23:27:41+00:00</dateTime.iso8601></value>
                </member>
            </struct>
        </value>
        <value>
            <struct>
                <member>
                    <name>id</name>
                    <value><i4>11</i4></value>
                </member>
                <member>
                    <name>title</name>
                    <value><string>xmlrpc</string></value>
                </member>
                <member>
                    <name>time</name>
                    <value><dateTime.iso8601>2017-01-23T15:18:09+00:00</dateTime.iso8601></value>
                </member>
            </struct>
        </value>
    </data>
</array>
```
```java
public class Post {
    int id; // or Integer
    String title;
    Date time;
}
interface BlogService {
    @XmlRpc("posts.list") @POST("/xmlprc")
    Call<Post[]> listPosts(@Body String... topics);
}
BlogService service = retrofit.create(BlogService.class);
Post[] posts = service.listPosts("java").execute().body();
// posts[0].id --> 10
// posts[0].title --> "retrofit"
// posts[0].time --> 24 Jan 2017 23:27:41
```

#### Arrays of values as POJO fields
Some XML-RPC servers return arrays containing directly the target type field values, without a struct. This is supported by directly setting the array values on the POJO fields in order of declaration. Static and final fields are ignored.
```xml
<array>
    <data>
        <value>
            <array>
                <data>
                    <value><i4>10</i4></value>
                    <value><string>retrofit</string></value>
                    <value><dateTime.iso8601>2017-01-24T23:27:41+00:00</dateTime.iso8601></value>
                </data>
            </array>
        </value>
        <value>
            <array>
                <data>
                    <value><i4>11</i4></value>
                    <value><string>xmlrpc</string></value>
                    <value><dateTime.iso8601>2017-01-23T15:18:09+00:00</dateTime.iso8601></value>
                </data>
            </array>
        </value>
    </data>
</array>
```
```java
public class Post {
    int id;
    String title;
    Date time;
}
interface BlogService {
    @XmlRpc("posts.list") @POST("/xmlprc")
    Call<Post[]> listPosts(@Body String... topics);
}
BlogService service = retrofit.create(BlogService.class);
Post[] posts = service.listPosts("java").execute().body();
// posts[0].id --> 10
// posts[0].title --> "retrofit"
// posts[0].time --> 24 Jan 2017 23:27:41
```

### Fault responses

While XML-RPC defined a struct fault response structure, there is no specific handling for it in the library. As the return types are strictly defined in your Retrofit service interface, there is no conversion from the fault to your target type possible. Instead, an `IOException` will be thrown. I recommend installing [an OkHttp logging interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor) with BODY level logging to see fault response details.

## Examples

Many examples can be found in the unit and integration tests such as [listing Wordpress blog posts](https://github.com/erickok/retrofit-xmlrpc/blob/master/xmlrpc/src/test/java/nl/nl2312/xmlrpc/WordpressIntegrationTest.java), [listing rTorrent torrents](https://github.com/erickok/retrofit-xmlrpc/blob/master/xmlrpc/src/test/java/nl/nl2312/xmlrpc/RtorrentTest.java) and several simple [test services](https://github.com/erickok/retrofit-xmlrpc/blob/master/xmlrpc/src/test/java/nl/nl2312/xmlrpc/SimpleIntegrationTest.java).

## Under the hood
The service method `@Body` argument is converted for you into an XML-RPC `<methodCall>` request. The method name from the `@XmlRpc` annotation becomes the `<methodName>` while the `@Body` object itself is persisted either directly or from the given type fields, via reflection. For the `MultiplicationArgs` example above:
```xml
<methodCall>
   <methodName>test.sumprod</methodName>
   <params>
      <param>
         <value><i4>2</i4></value>
      </param>
      <param>
         <value><i4>4</i4></value>
      </param>
   </params>
</methodCall>
```

The xml response is first parsed via [Simple](http://simple.sourceforge.net/) into a POJO representation of the `<methodResponse>`, which has always one response `<param>`. This is then converted into the specified target type either directly or set on the fields of your complex return type, via reflection. For the `Call<Integer>` example:
```xml
<methodResponse>
  <params>
    <param>
      <value>
        <array>
          <data>
            <value><int>6</int></value>
            <value><int>8</int></value>
          </data>
        </array>
      </value>
    </param>
  </params>
</methodResponse>
```

## License and credits
Designed and developed by [Eric Kok](mailto:eric@2312.nl) of [2312 development](http://2312.nl). Inspired by the [retrofit-jsonrpc](https://github.com/segmentio/retrofit-jsonrpc) project. Includes `Iso8601Utils` and `Iso8601DateFormat` classes from the [Jackson Databind](https://github.com/FasterXML/jackson-databind) project under Apache License 2.0.

    Copyright 2017 Eric Kok
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
