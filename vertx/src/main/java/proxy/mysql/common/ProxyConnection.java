package proxy.mysql.dirty;

import io.vertx.core.net.NetSocket;
import proxy.mysql.common.BaseProxyConnection;

/**
 * Created by karak on 16-9-8.
 */
public class ProxyConnection extends BaseProxyConnection {
    public ProxyConnection(NetSocket clientSocket, NetSocket serverSocket) {
        super(clientSocket, serverSocket);
    }
}
