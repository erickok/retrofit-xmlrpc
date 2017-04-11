package nl.nl2312.xmlrpc.types;

import nl.nl2312.xmlrpc.ValueConverter;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.stream.OutputNode;

@Root
public final class Member {

    public static final String CODE = "member";
    public static final String NAME = "name";

    @Element(name = NAME)
    public String name;

    @Element(name = Value.CODE)
    @Convert(ValueConverter.class)
    public Value value;

    public void write(OutputNode node) throws Exception {
        OutputNode member = node.getChild(CODE);
        OutputNode nameNode = member.getChild(Member.NAME);
        nameNode.setValue(name);
        OutputNode valueNode = member.getChild(Value.CODE);
        value.write(valueNode);
    }

    public static Member create(String name, Value from) {
        Member param = new Member();
        param.name = name;
        param.value = from;
        return param;
    }

}
