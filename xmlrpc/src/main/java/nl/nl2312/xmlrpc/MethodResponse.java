package nl.nl2312.xmlrpc;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "methodResponse")
final class MethodResponse {

    @ElementList
    List<Param> params;

}
