package nl.nl2312.xmlrpc;

/**
 * An always-empty object to use as request {@link retrofit2.http.Body} parameter when no arguments need to be
 * supplied to an XML-RPC call. It is suggested to statically import {@link @NOTHING}.
 */
public final class Nothing {

    public static final Nothing NOTHING = new Nothing();

    private Nothing() {
    }

}
