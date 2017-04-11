package nl.nl2312.xmlrpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The serialized name of a struct member. This overrides the use of the POJO field name.
 * <p>
 * Useful to map alternative casing or Java reserved names to your fields, such as:
 * <pre><code>
 * &lt;struct&gt;
 *   &lt;member&gt;
 *     &lt;name&gt;file_nr&lt;/name&gt;
 *     &lt;value&gt;&lt;string&gt;DF101364&lt;/string&gt;&lt;/value&gt;
 *   &lt;/member&gt;
 *   &lt;member&gt;
 *     &lt;name&gt;case&lt;/name&gt;
 *     &lt;value&gt;&lt;string&gt;Deep Throat&lt;/string&gt;&lt;/value&gt;
 *   &lt;/member&gt;
 * &lt;/struct&gt;
 * </code></pre>
 * This would map to an object with fields:
 * <pre><code>
 * &#064;MemberName("file_nr") String fileNr; // "DF101364"
 * &#064;MemberName("case") String caseName; // "Deep Throat"
 * </code></pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MemberName {

    String value();

}
