package proxy.mysql.httpServer;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

import java.net.URL;

public class CodeProxyServerVerticle extends AbstractVerticle {

    private final int port = 13128;

    private final String mysqlHost = "127.0.0.1";
    private static final Logger logger = LoggerFactory.getLogger(CodeProxyServerVerticle.class);
/*

    String[] var17 = splitInitialLine(var12);
                if(var17.length < 3) {
        this.currentState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
        return;
    }
*/

    @Override
    public void start() throws Exception {

       HttpRequestDecoder decoder= new HttpRequestDecoder();
        NetServer netServer = vertx.createNetServer();//创建对外socket->socket服务器
        NetClient netClient = vertx.createNetClient();//创建连接本机socket->http内部客户端

        netServer.connectHandler(socket -> {

            socket.handler(buffer->{
             //   HttpClient proxyClient = HttpClient.

            });
            vertx.createNetClient().connect(port, mysqlHost, result -> {
                //响应来自客户端的连接请求，成功之后，在建立一个与目标mysql服务器的连接

                if (result.succeeded()) {
                    //与目标mysql服务器成功连接连接之后，创造一个MysqlProxyConnection对象,并执行代理方法
                    new MysqlProxyConnection(socket, result.result()).proxy();
                } else {
                    logger.error(result.cause().getMessage(), result.cause());
                    socket.close();
                }
            });
        }).listen(1, listenResult -> {//代理服务器的监听端口
            if (listenResult.succeeded()) {
                //成功启动代理服务器
                logger.info("Mysql proxy server start up.");
            } else {
                //启动代理服务器失败
                logger.error("Mysql proxy exit. because: " + listenResult.cause().getMessage(), listenResult.cause());
                System.exit(1);
            }
        });
    }


    public static void main(String[] args) {

    }

}
