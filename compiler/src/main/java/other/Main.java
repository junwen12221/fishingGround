package other;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.ByteArrayInputStream;
import java.nio.file.Paths;


public class Main {

    public static void main(String[] args) throws Exception {
        String[] jarPath = new String[1];
        jarPath[0] = "";
        Vertx.vertx().createHttpServer().requestHandler(req -> {
            System.out.println(req.path());
            switch (req.path()) {
                case "/op": {
                    req.setExpectMultipart(true).bodyHandler(buffer -> {


                        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.getBytes())) {
                            ClassPool cp = ClassPool.getDefault();
                            CtClass cc = cp.makeClass(byteArrayInputStream);
                            CtMethod m = cc.getDeclaredMethod("run");
                            m.addLocalVariable("elapsedTime", CtClass.longType);
                            m.insertBefore("elapsedTime = System.currentTimeMillis();");
                            m.insertAfter("{elapsedTime = System.currentTimeMillis() - elapsedTime;"
                                    + "System.out.println(\"Method   Executed in ms: \" + elapsedTime);}");
                            byte[] byteCode = cc.toBytecode();
                            cc.detach();
                            req.response().setChunked(true).write(Buffer.buffer(byteCode)).end();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    });
                    break;
                }
                case "/init": {
                    req.bodyHandler(buffer -> {
                        jarPath[0] = new String(buffer.getBytes());
                    });
                    req.response().end();
                    break;
                }
                default: {
                    break;
                }
            }

        }).listen(8080);
        TestWatcherService watcherService = new TestWatcherService(Paths.get("D:\\Users\\karakapi\\Downloads\\java-agent-asm-javassist-sample-master\\java-agent-asm-javassist-sample-master\\"));
        DynamicCompilerUtil compilerUtil = new DynamicCompilerUtil();
        new Thread() {
            @Override
            public void run() {
                try {
                    watcherService.handleEvents(name -> {
                        try {
                            // 编译F:\\亚信工作\\SDL文件\\sdl\\src目录下的所有java文件
                            String filePath = "D:\\Users\\karakapi\\Downloads\\java-agent-asm-javassist-sample-master\\java-agent-asm-javassist-sample-master\\other\\src";
                            String sourceDir = "D:\\Users\\karakapi\\Downloads\\java-agent-asm-javassist-sample-master\\java-agent-asm-javassist-sample-master\\other\\src";
                            String targetDir = "D:\\Users\\karakapi\\Downloads\\java-agent-asm-javassist-sample-master\\java-agent-asm-javassist-sample-master\\other\\target\\classes";
                            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

                            boolean compilerResult = compilerUtil.compiler("UTF-8", jarPath[0], filePath, sourceDir, targetDir, diagnostics);
                            if (compilerResult) {
                                System.out.println("编译成功!");
                            } else {
                                System.out.println("编译失败");
                                for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                                    // System.out.format("%s[line %d column %d]-->%s%n", diagnostic.getKind(), diagnostic.getLineNumber(),
                                    // diagnostic.getColumnNumber(),
                                    // diagnostic.getMessage(null));
                                    System.out.println(diagnostic.getMessage(null));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


        while (true)

        {
            Stuff stuff = new Stuff();
            stuff.run();
            Thread.sleep(1000);
        }

    }

}
