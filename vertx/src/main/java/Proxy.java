import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.*;

/**
 * Created by karak on 16-9-7.
 */
public final class Proxy extends AbstractVerticle {
    NetClient client;
    HttpServer server;

/*
    @Override
    public void start() throws Exception {

        server = vertx.createHttpServer();
        server.requestHandler(req -> {
            if (req.method() == HttpMethod.CONNECT) {
                // Determine proxied server address
                String proxyAddress = req.uri();
                final int idx = proxyAddress.indexOf(':');
                String host = proxyAddress.substring(0, idx);
                final int port = Integer.parseInt(proxyAddress.substring(idx + 1));

                System.out.println("Connecting to proxy " + proxyAddress);
                client.connect(port, host, ar -> {

                    if (ar.succeeded()) {
                        System.out.println("Connected to proxy");
                        NetSocket clientSocket = req.netSocket();
                        clientSocket.write("HTTP/1.0 200 Connection established\n\n");
                        final NetSocket serverSocket = ar.result();

                        serverSocket.handler(buff -> {
                            System.out.println("Forwarding server packet to the client");
                            clientSocket.write(buff);
                        });
                        serverSocket.closeHandler(v -> {
                            System.out.println("Server socket closed");
                            clientSocket.close();
                        });

                        clientSocket.handler(buff -> {
                            System.out.println("Forwarding client packet to the server");
                            serverSocket.write(buff);
                        });
                        clientSocket.closeHandler(v -> {
                            System.out.println("Client socket closed");
                            serverSocket.close();
                        });
                    } else {
                        System.out.println("Fail proxy connection");
                        req.response().setStatusCode(403).end();
                    }
                });
            } else {
                req.response().setStatusCode(405).end();
            }
        }).listen(8080);
    }
*/

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {

        // If you have slow cleanup tasks to perform, you can similarly override the async stop method

        vertx.setTimer(2000, tid -> {
            System.out.println("Cleanup tasks are now complete, OtherVerticle is now stopped!");
            client.close();
            server.close();
            stopFuture.complete();

        });

    }

    enum RepltType {
        FORWARDING,
        CLOSED
    }

    static class Package {
        public RepltType type;
        public Buffer buffer;
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClient client = vertx.createNetClient(new NetClientOptions());


        {

            vertx.createHttpServer().requestHandler(req -> {
                if (req.method() == HttpMethod.CONNECT) {
                    // Determine proxied server address
                    String proxyAddress = req.uri();
                    int idx = proxyAddress.indexOf(':');
                    String host = proxyAddress.substring(0, idx);
                    int port = Integer.parseInt(proxyAddress.substring(idx + 1));

                    System.out.println("Connecting to proxy " + proxyAddress);
                    client.connect(port, host, ar -> {

                        if (ar.succeeded()) {
                            System.out.println("Connected to proxy");
                            NetSocket clientSocket = req.netSocket();
                            clientSocket.write("HTTP/1.0 200 Connection established\n\n");


                            NetSocket serverSocket = ar.result();
                            serverSocket.handler(buff -> {
                                System.out.println("Forwarding server packet to the client");
                                clientSocket.write(buff);
                            });
                            serverSocket.closeHandler(v -> {
                                System.out.println("Server socket closed");
                                clientSocket.close();
                            });

                            clientSocket.handler(buff -> {
                                System.out.println("Forwarding client packet to the server");
                                serverSocket.write(buff);
                            });
                            clientSocket.closeHandler(v -> {
                                System.out.println("Client socket closed");
                                serverSocket.close();
                            });
                        } else {

                            System.out.println("Fail proxy connection");
                            req.response().setStatusCode(403).end();
                        }
                    });
                } else {
                    req.response().setStatusCode(405).end();
                }
            }).listen(8080);
        }

    }
}

