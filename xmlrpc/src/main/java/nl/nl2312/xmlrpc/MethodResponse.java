package nl.nl2312.xmlrpc;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "methodResponse")
public final class MethodResponse {

	@ElementList public List<Param> params;

}
