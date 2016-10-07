import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.System.out;

public class ReduceRT {
    // 文件拷贝
    public static boolean copy(String file1, String file2) {
        try // must try and catch,otherwide will compile error
        {
            // instance the File as file_in and file_out
            java.io.File file_in = new java.io.File(file1);
            java.io.File file_out = new java.io.File(file2);
            FileInputStream in1 = new FileInputStream(file_in);
            FileOutputStream out1 = new FileOutputStream(file_out);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = in1.read(bytes)) != -1)
                out1.write(bytes, 0, c);
            in1.close();
            out1.close();
            return (true); // if success then return true
        } catch (Exception e) {
            out.println("Error!");
            return (false); // if fail then return false
        }
    }

    // 读取路径,copy
    public static int dealClass(String needfile, String sdir, String odir)
            throws Exception {
        AtomicInteger sn = new AtomicInteger(0); // 成功个数

        String patternStr = "\\[Loaded.*from.*rt\\.jar\\]";
        final Pattern pattern = Pattern.compile(patternStr);
       /* Matcher isFilteredProtocol = pattern.matcher(line);*/

        File usedclass = new File(needfile);
        if (usedclass.canRead()) {
            LineNumberReader reader = new LineNumberReader(
                    new InputStreamReader(new FileInputStream(usedclass),
                            "UTF-8"));
            List<String> result = reader.lines()
                    .filter((s) -> !s.startsWith("[Loaded com.sun") && s.startsWith("[Loaded") && !s.startsWith("[Opened ") && !"".equals(s) && !s.startsWith("[Loaded java") && !s.startsWith("[Loaded sun") && !s.startsWith("[Loaded jdk"))
                    .map(s -> s.substring("[Loaded ".length(), s.indexOf(" from")).trim().replaceAll("\\.", "/")
                    ).distinct().collect(Collectors.toList());
            result.forEach((line) -> {
                int dirpos = line.lastIndexOf("/");
                if (dirpos > 0) {
                    String dir = odir + line.substring(0, dirpos);
                    File fdir = new File(dir);
                    if (!fdir.exists())
                        fdir.mkdirs();
                    String sf = sdir + line + ".class";
                    String of = odir + line + ".class";
                    out.println(sf);
                    out.println(of);
                    try {
                        Files.copy(Paths.get(sf.trim()), Paths.get(of.trim()));
                        sn.getAndIncrement();
                    } catch (Exception e) {
                        out.println(line);
                    }
                }
            });
        }

        return sn.get();

    }

    public static void main(String[] args) throws Exception {
        try {
            String needfile = "L:\\MY\\staticBlog\\target\\ClassUsedList.txt";
            // out.println(FileUtils.readFileToString(new File(needfile)));
            String sdir = "L:\\MY\\staticBlog\\target\\rt\\";
            String odir = "L:\\MY\\staticBlog\\target\\rt1\\";
            int sn = dealClass(needfile, sdir, odir);
            out.print(sn);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}