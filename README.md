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
