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
            return !s.startsWith("[Loaded com.sun") &&
                    s.startsWith("[Loaded") &&
                    !s.startsWith("[Opened ") &&
                    !"".equals(s) && !s.startsWith("[Loaded java") &&
                    !s.startsWith("[Loaded sun") &&
                    !s.startsWith("[Loaded jdk") &&
                    !s.contains("$$Lambda$");
        })
                .map(s -> "/" + s.substring("[Loaded ".length(), s.indexOf(" from")).trim().replaceAll("\\.", "/") + ".class")
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
        Path workspace = Files.createTempDirectory(Paths.get(jarPath.substring(0, jarPath.lastIndexOf("/"))), "simplifyjre");
        out.println(workspace);
        String cmd = "java -jar -verbose:class  " + jarPath + jarArgs;
        out.println(cmd);
        String odir = workspace.toString();
        File file = Paths.get(odir).toFile();
        FileUtils.deleteDirectory(file);
        if (!file.mkdirs()) throw new Exception("创建文件夹:" + file.toString() + "失败");
        FileSystem jar = FileSystems.newFileSystem(Paths.get(jarPath), null);
        cmd(cmd, s -> dealClass(s, jar, odir));
        Files.createDirectory(Paths.get(odir + "/META-INF"));
        Files.copy(jar.getPath("/META-INF/MANIFEST.MF"), Paths.get(odir + "/META-INF/MANIFEST.MF"));
        String MANIFEST = odir + "/META-INF/MANIFEST.MF";
        String buildJar = "jar cvfmn " + jarPath.substring(0, jarPath.lastIndexOf(".") - 1) + "-new.jar " + MANIFEST + " -C " + odir + "/ .";
        out.println(buildJar);
        cmd(buildJar, (s) -> s.forEach(System.out::println));
    }
}