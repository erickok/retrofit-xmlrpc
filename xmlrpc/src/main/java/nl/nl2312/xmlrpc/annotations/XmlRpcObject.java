package nl.nl2312.xmlrpc.annotations;

import nl.nl2312.xmlrpc.DeserialisationMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlRpcObject {

    DeserialisationMode value();

    Class<? extends > creator() default void.class;

}
