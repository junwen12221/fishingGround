import lombok.Cleanup;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.System.out;

public class ReduceRT {
    public static void dealClass(Stream<String> stream, FileSystem jar, String odir) {
        final AtomicLong counter = new AtomicLong(0);
        Stream<String> result = stream.filter((s) -> {
            out.println(s);
            return s.startsWith("[Loaded") &&
                    !(s.startsWith("[Loaded com.sun") ||
                            s.startsWith("[Loaded java") ||
                            s.startsWith("[Loaded jdk") ||
                            s.startsWith("[Loaded sun") ||
                            s.contains("$$Lambda$"));
        })
                .map(s -> "/" + s.substring("[Loaded ".length(), s.indexOf(" from")).replaceAll("\\.", "/").trim() + ".class")
                .distinct();
        result.forEach((n) -> {
            Path c = jar.getPath(n);
            if (Files.exists(c)) {
                try {
                    String o = odir + n;
                    String dir = o.substring(0, o.lastIndexOf("/") + 1);
                    File file = new File(dir);
                    if (!file.exists()) {
                        boolean res = file.mkdirs();
                        out.println("创建文件夹" + file + ":" + res);
                    }
                    out.println(c + " -> " + o);
                    Files.copy(c, Paths.get(o));
                    counter.getAndIncrement();
                } catch (Exception e) {
                    out.println("失败:" + n);
                    e.printStackTrace();
                }
            } else {
                out.println("文件不匹配" + c);
            }
        });
        out.println("=> class个数:" + (counter.get()));
    }


    static void cmd(String cmd, Consumer<Stream> fun) throws Exception {
        Process process = Runtime.getRuntime().exec(cmd);
        @Cleanup InputStream in = process.getInputStream();
        @Cleanup InputStreamReader ir = new InputStreamReader(in, StandardCharsets.UTF_8);
        @Cleanup LineNumberReader input = new LineNumberReader(ir);
        fun.accept(input.lines());
        process.waitFor();
    }

    public static void main(String[] args) throws Exception {
        String jarPath = "L:\\\\MY\\\\staticBlog\\\\target\\\\staticBlog-0.1-SNAPSHOT.jar";
        String jarArgs = " \"D:\\Users\\karakapi\\zhuomian\\static - 副本\"";
        _main(new String[]{jarPath, jarArgs});
    }

    public static void _main(String[] args) throws Exception {
        String jarPath = args[0].replaceAll("\\\\", "/");
        String jarArgs = args[1].replaceAll("\\\\", "/");
        //临时存放抽取需要的class的目录
        Path workspace = Files.createTempDirectory(Paths.get(jarPath.substring(0, jarPath.lastIndexOf("/"))), "simplifyjre");
        out.println(workspace);
        //构造运行jar 分析运行依赖的的命令
        String cmdString = "java -jar -verbose:class  " + jarPath + jarArgs;
        out.println(cmdString);
        String odir = workspace.toString();
        File file = Paths.get(odir).toFile();
        FileUtils.deleteDirectory(file);
        if (!file.mkdirs()) throw new Exception("创建文件夹:" + file.toString() + "失败");
        //利用zip文件系统读取class
        FileSystem jar = FileSystems.newFileSystem(Paths.get(jarPath), null);
        //从jar包抽取需要的class并复制到odir
        cmd(cmdString, s -> dealClass(s, jar, odir));
        Files.createDirectory(Paths.get(odir + "/META-INF"));
        //复制MANIFEST.MF
        Files.copy(jar.getPath("/META-INF/MANIFEST.MF"), Paths.get(odir + "/META-INF/MANIFEST.MF"));
        String MANIFEST = odir + "/META-INF/MANIFEST.MF";
        //构造新的jar包执行命令
        String buildJar = "jar cvfmn " + jarPath.substring(0, jarPath.lastIndexOf(".") - 1) + "-new.jar " + MANIFEST + " -C " + odir + "/ .";
        out.println(buildJar);
        //运行构造jar包命令
        cmd(buildJar, (s) -> s.forEach(System.out::println));
    }
}