package agent;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by karak on 16-9-4.
 */
final class AgentClient implements ClassFileTransformer {
    private final String packageName;
    private final HttpPost init;
    private final HttpPost op;
    private final HttpPost end;
    private final CloseableHttpClient httpclient;
    private final String jarPath;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    volatile boolean state = true;

    AgentClient(String host, int port, String packageName) {
        this.packageName = packageName;
        this.httpclient = HttpClients.createDefault();
        this.init = new HttpPost("http://" + host + ":" + port + "/init");
        this.op = new HttpPost("http://" + host + ":" + port + "/op");
        this.end = new HttpPost("http://" + host + ":" + port + "/end");
        this.jarPath = System.getProperty("java.class.path");
    }


    private byte[] send(byte[] bytes) throws Exception {
        op.setEntity(new ByteArrayEntity(bytes));
        try (CloseableHttpResponse response = httpclient.execute(op)) {
            byte[] byteArray = EntityUtils.toByteArray(response.getEntity());
            return byteArray;
        }
    }


    @Override
    public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        System.out.println("载入启动");
      /*  if (state) {
            executorService.submit(() -> {
                CloseableHttpResponse response = null;
                try {
                    init.setEntity(new StringEntity(jarPath));
                    response = httpclient.execute(init);
                } catch (Exception e) {
                    state = false;
                } finally {
                    try {
                        response.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {

        }*/


        try {

            if (s == null) {
                return bytes;
            }
            if (packageName.startsWith(s)) {
                System.out.println("reroad   s");
              //  return state?send(bytes):bytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
