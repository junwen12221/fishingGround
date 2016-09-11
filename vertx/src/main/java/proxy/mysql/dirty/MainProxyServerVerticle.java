package proxy.mysql.dirty;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;

public class MysqlProxyServerVerticle extends AbstractVerticle {

    private final int port = 1;

    private final String mysqlHost = "localhost";
    private static final Logger logger = LoggerFactory.getLogger(MysqlProxyServerVerticle.class);

    @Override
    public void start() throws Exception {

        final NetServer netServer = vertx.createNetServer();//创建代理服务器
        final NetClient netClient = vertx.createNetClient();//创建连接mysql客户端

        netServer.connectHandler(socket ->

                vertx.createNetClient().connect(1, mysqlHost, result -> {

                    //响应来自客户端的连接请求，成功之后，在建立一个与目标mysql服务器的连接
                    if (result.succeeded()) {
                        //与目标mysql服务器成功连接连接之后，创造一个MysqlProxyConnection对象,并执行代理方法
                        new ProxyConnection(socket, result.result()).proxy();
                    } else {
                        logger.error(result.cause().getMessage(), result.cause());
                        socket.close();
                    }
                })).listen(1234, listenResult -> {//代理服务器的监听端口
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
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(  new ConnectHttpProxy(null), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new CodeProxyServerVerticle(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new MysqlProxyServerVerticle(), new DeploymentOptions().setWorker(true));


    }

}
