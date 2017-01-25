package nl.nl2312.xmlrpc;

import nl.nl2312.xmlrpc.types.Value;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

@Root
public final class Param {

    @Element(name = Value.CODE)
    @Convert(ValueConverter.class)
    Value value;

    public static Param from(Object from) {
        Param param = new Param();
        param.value = ValueConverter.getValue(from);
        return param;
    }

}
