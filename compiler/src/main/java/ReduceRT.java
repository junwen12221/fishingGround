import lombok.val;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.lang.System.out;

public class ReduceRT {
    // 读取路径,copy
    public static void dealClass(String needfile, String source, String object) throws Exception {
        String sdir = source.replaceAll("\\\\", "/");
        String odir = object.replaceAll("\\\\", "/");
        File usedclass = new File(needfile.replaceAll("\\\\", "/"));
        final AtomicLong counter = new AtomicLong(0);
        if (usedclass.canRead()) {
            try (InputStream is = new FileInputStream(new File(needfile)); InputStreamReader ir = new InputStreamReader(is, "UTF-8"); LineNumberReader reader = new LineNumberReader(ir)) {
                Stream<String> result = reader.lines()
                        .filter((s) ->
                                !s.startsWith("[Loaded com.sun") &&
                                        s.startsWith("[Loaded") &&
                                        !s.startsWith("[Opened ") &&
                                        !"".equals(s) && !s.startsWith("[Loaded java") &&
                                        !s.startsWith("[Loaded sun") &&
                                        !s.startsWith("[Loaded jdk") &&
                                        !s.contains("$$Lambda$"))
                        .map(s -> s.substring("[Loaded ".length(), s.indexOf(" from")).trim().replaceAll("\\.", "/") + ".class")
                        .distinct();

                result.forEach((n) -> {
                    try {
                        String s = sdir + n;
                        String o = odir + n;
                        String dir = o.substring(0, s.lastIndexOf("/") + 1);
                        File file = new File(dir);
                        if (!file.exists()) {
                            boolean res = file.mkdirs();
                            out.println("创建文件夹" + file + ":" + res);
                        }
                        out.println(s + ";" + o);
                        Files.copy(Paths.get(s), Paths.get(o));
                        counter.getAndIncrement();
                    } catch (Exception e) {
                        out.println("失败:" + n);
                        e.printStackTrace();
                    }
                });
                out.println("=> class个数:" + (counter.get() - 1L));
            }
        } else {
            out.println("目录不能读取" + needfile);
        }
/*        LongAdder s=new LongAdder();
        ProcessBuilder p= new ProcessBuilder();
        p.command()*/
    }

    public static void main(String[] args) throws Exception {
        String jarPath = "L:\\\\MY\\\\staticBlog\\\\target\\\\staticBlog-0.1-SNAPSHOT.jar".replaceAll("\\\\", "/");
        Path workspace = Files.createTempDirectory(Paths.get(jarPath.substring(0, jarPath.lastIndexOf("/"))), "simplifyjre");
        Path ClassUsedList = Paths.get(workspace + "/ClassUsedList.txt");
        String cmd = "java -jar -verbose:class " + jarPath + "> " + ClassUsedList;
        val prpcess = Runtime.getRuntime().exec(cmd);
        ///////////////////////////////////////
        FileSystem jar = FileSystems.newFileSystem(Paths.get(jarPath), null);
        jar.
                new JarFile(jarPath).stream().map((s) -> s).forEach((s) -> {
            try {
                out.println(s.getExtra());
            } catch (Exception e) {

            }


        });

        prpcess.waitFor();
/*

        try {
            String needfile = "L:\\MY\\staticBlog\\target\\ClassUsedList.txt";
            String sdir = "L:\\MY\\staticBlog\\target\\rt\\".replaceAll("\\\\", "/");
            String odir = "L:\\MY\\staticBlog\\target\\rt1\\".replaceAll("\\\\", "/");
            File file = Paths.get(odir).toFile();
            delFolder(odir);
            file.mkdirs();
            dealClass(needfile, sdir, odir);
            *//*FileUtils.copyDirectory(Paths.get(sdir+"/META-INF").toFile(),Paths.get(odir+"/META-INF").toFile());*//*
            String MANIFEST = sdir + "/META-INF/MANIFEST.MF";
            String buildJar = "jar cvfmn classes.jar " + MANIFEST + " -C " + odir + "/ .";
            out.println(buildJar);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

//删除文件夹
//param folderPath 文件夹完整绝对路径

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除指定文件夹下所有文件
//param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
}