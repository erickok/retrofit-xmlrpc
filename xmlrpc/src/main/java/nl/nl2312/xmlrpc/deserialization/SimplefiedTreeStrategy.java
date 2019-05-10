package nl.nl2312.xmlrpc.deserialization;

import org.simpleframework.xml.strategy.TreeStrategy;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.NodeMap;

import java.util.Map;

public class SimplefiedTreeStrategy extends TreeStrategy {

    @Override
    public boolean write(Type type, Object value, NodeMap node, Map map) {
        return false;
    }

}
