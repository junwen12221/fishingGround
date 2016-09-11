package proxy.mysql.common;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetSocket;
import io.vertx.core.streams.Pump;

public abstract class BaseProxyConnection {


    private final NetSocket clientSocket;

    private final NetSocket serverSocket;

    private static final Logger logger = LoggerFactory.getLogger(BaseProxyConnection.class);

    private final Pump clientToServerPump;
    private final Pump serverToClientPump;

    public BaseProxyConnection(NetSocket clientSocket, NetSocket serverSocket) {

        this.clientSocket = clientSocket;

        this.serverSocket = serverSocket;

        this.clientToServerPump = Pump.pump(clientSocket, serverSocket);
        this.serverToClientPump = Pump.pump(serverSocket, clientSocket);
    }


    public void proxy() {

        //当代理与mysql服务器连接关闭时，关闭client与代理的连接

        serverSocket.closeHandler(v -> clientSocket.close());

        //反之亦然

        clientSocket.closeHandler(v -> serverSocket.close());

        //不管那端的连接出现异常时，关闭两端的连接

        serverSocket.exceptionHandler(e -> {

            logger.error(e.getMessage(), e);

            close();

        });

        clientSocket.exceptionHandler(e -> {

            logger.error(e.getMessage(), e);

            close();

        });

        //当收到来自客户端的数据包时，转发给mysql目标服务器

        clientToServerPump.start();
        serverToClientPump.start();
        // clientSocket.handler(buffer -> serverSocket.write(buffer));

        //当收到来自mysql目标服务器的数据包时，转发给客户端

        //serverSocket.handler(buffer -> clientSocket.write(buffer));

    }


    public void close() {

        clientToServerPump.stop();
        serverToClientPump.stop();
        clientSocket.close();
        serverSocket.close();

    }

}
