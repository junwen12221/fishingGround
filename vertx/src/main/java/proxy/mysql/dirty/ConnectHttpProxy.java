package proxy.mysql;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.streams.Pump;

import java.util.Base64;

/**
 * Http Connect Proxy
 *
 * <p>
 * A simple Http CONNECT proxy for testing https proxy functionality. HTTP server running on localhost allowing CONNECT
 * requests only. This is basically a socket forwarding protocol allowing to use the proxy server to connect to the
 * internet.
 *
 * <p>
 * Usually the server will be started in @Before and stopped in @After for a unit test using HttpClient with the
 * setProxyXXX methods.
 *
 * @author <a href="http://oss.lehmann.cx/">Alexander Lehmann</a>
 */
public class ConnectHttpProxy extends AbstractVerticle {

  private static final int PORT = 13128;

  private static final Logger log = LoggerFactory.getLogger(ConnectHttpProxy.class);

  private HttpServer server;

  private int error = 0;

  protected final String username;
  protected String lastUri;
  protected String forceUri;

  public ConnectHttpProxy(String username) {
    this.username=username;
  }


  @Override
  public void start() {
    HttpServerOptions options = new HttpServerOptions();
   // HttpClient client = vertx.createHttpClient();
    options.setHost("localhost").setPort(PORT);
    server = vertx.createHttpServer(options);
    server.requestHandler(request -> {
      HttpMethod method = request.method();
      String uri = request.uri();
      if (username  != null) {
        String auth = request.getHeader("Proxy-Authorization");
        String expected = "Basic " + Base64.getEncoder().encodeToString((username + ":" + username).getBytes());
        if (auth == null || !auth.equals(expected)) {
          request.response().setStatusCode(407).end("proxy authentication failed");
          return;
        }
      }
      if (error != 0) {
        request.response().setStatusCode(error).end("proxy request failed");
      } else if (method != HttpMethod.CONNECT || !uri.contains(":")) {
        ///////////////////////////////////////////////////////////////////
        HttpServerRequest req= request;
        String rawHost=req.host();
        String[] list= rawHost.split(":");
        String host;
        int port;
        if(list.length==2){
          host=list[0];
          port= Integer.parseInt(list[1]);
        }else{
          host=list[0];
          port=80;
        }
        //System.out.println("Proxying request: " + host);

        HttpClientRequest c_req = vertx.createHttpClient().request(req.method(), port,host, req.uri(), c_res -> {
          //System.out.println("Proxying response: " + c_res.statusCode());
          req.response().setChunked(true);
          req.response().setStatusCode(c_res.statusCode());
          req.response().headers().setAll(c_res.headers());
          c_res.handler(data -> {
            //System.out.println("Proxying response body: " + data.toString("ISO-8859-1"));
            req.response().write(data);
          });
          c_res.endHandler((v) -> req.response().end());
        });
        c_req.setChunked(true);
        c_req.headers().setAll(req.headers());
        req.handler(data -> {
          //System.out.println("Proxying request body " + data.toString("ISO-8859-1"));
          c_req.write(data);
        });
        req.endHandler((v) -> c_req.end());
        ////////////////////////////////////////////////////////
        //request.response().setStatusCode(405).end("method not allowed");
      } else {
        lastUri = uri;
        if (forceUri != null) {
          uri = forceUri;
        }
        String[] split = uri.split(":");
        String host = split[0];
        int port;
        try {
          port = Integer.parseInt(split[1]);
        } catch (NumberFormatException ex) {
          port = 443;
        }
        NetSocket serverSocket = request.netSocket();
        NetClientOptions netOptions = new NetClientOptions();
        NetClient netClient = vertx.createNetClient(netOptions);
        netClient.connect(port, host, result -> {
          if (result.succeeded()) {
            NetSocket clientSocket = result.result();
            serverSocket.write("HTTP/1.0 200 Connection established\n\n");
            serverSocket.closeHandler(v -> clientSocket.close());
            clientSocket.closeHandler(v -> serverSocket.close());
            Pump.pump(serverSocket, clientSocket).start();
            Pump.pump(clientSocket, serverSocket).start();
          } else {
            log.error("connect() failed", result.cause());
            request.response().setStatusCode(403).end("request failed");
          }
        });
      }
    });

  }

  /**
   * Stop the server.
   * <p>
   * Doesn't wait for the close operation to finish
   */
  @Override
  public void stop() {
    if (server != null) {
      server.close();
      server = null;
    }
  }


  public int getPort() {
    return PORT;
  }

  public ConnectHttpProxy setError(int error) {
    this.error  = error;
    return this;
  }
}
