package one.tranic.pfc.helpful;

import java.net.Proxy;

public class YggdrasilProxyHelpful {
    // If YggdrasilProxy is installed, its bundled method is used.
    public static Proxy determineProxy(Proxy proxy) {
        try {
            Class.forName("one.tranic.t.proxy");
            return one.tranic.t.proxy.ProxyConfigReader.getProxy(proxy);
        } catch (ClassNotFoundException e) {
            return proxy;
        }
    }
}