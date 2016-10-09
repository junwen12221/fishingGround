package cn.lightFish.staticBlog;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import org.pegdown.PegDownProcessor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Format;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static java.util.Arrays.copyOf;

/**
 * Created by karak on 16-10-3.
 */
public final class JavaStatic {
    final static String defaultSource = "source";
    final static String defaultTarget = "target";
    final static Function<String[], String[]> validateArgs = (args) -> {
        switch (args.length) {
            case 1:
                return new String[]{noEndSlash(args[0]), defaultSource, defaultTarget};
            case 2:
                return new String[]{noEndSlash(args[0]), noEndSlash(args[1]), defaultTarget};
            case 3:
                return new String[]{noEndSlash(args[0]), noEndSlash(args[1]), noEndSlash(args[2])};
            default:
                out.println("Usage: java -jar staticBlog-x.x.x <blogPath> ");
                out.println("[<source> default 'source'] [<target> default 'target']");
                return new String[0];
        }
    };
    static Integer pageSize = 5;    // 每页显示的条数
    final static Function<String[], String[]> setPageSize = (args) -> {
        int defaultCount = 10;
        if (args.length > 0) {
            pageSize = Integer.getInteger(args[args.length - 1].trim(), defaultCount);
            return defaultCount == pageSize ? args : Arrays.copyOf(args, args.length - 1);
        } else {
            return args;
        }
    };
    static Function<String, String> markdownToHtml;
    final static Function<String[], String[]> markdownMode = (args) -> {
        if (args.length > 0 && "-l".equals(args[args.length - 1].trim())) {
            final PegDownProcessor peg = new PegDownProcessor();
            markdownToHtml = (s) -> peg.markdownToHtml(s);
            return Arrays.copyOf(args, args.length - 1);
        } else {
            markdownToHtml = JavaStatic::markdownToHtmlByGithub;
            return args;
        }
    };
    static Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");

    public static void main(String[] args) throws Exception {
        args = new String[]{"D:/Users/karakapi/zhuomian/static - 副本", " -g"};
        val options = markdownMode.andThen(setPageSize).andThen(validateArgs).apply(args);
        if (options.length == 0) return;

        val path = options[0];
        val source = options[1];
        val target = options[2];

        val newPath = String.format("%s/new", path);
        requireFolder(newPath);
        validateFileNames(newPath);
        val sourcePath = String.format("%s/%s", path, source);
        requireFolder(sourcePath);

        val sourcePostsPath = sourcePath.concat("/posts");
        createFolderIfNotExists(sourcePostsPath);
        val targetPath = String.format("%s/%s", path, target);
        createFolderIfNotExists(targetPath);

        val headerPath = String.format("%s/header.html", sourcePath);
        requireFile(headerPath);
        val footerPath = String.format("%s/footer.html", sourcePath);
        requireFile(footerPath);
        val linkPath = String.format("%s/link.html", sourcePath);
        requireFile(linkPath);

        val header = stringFromFile(headerPath);
        val footer = stringFromFile(footerPath);
        val link = stringFromFile(linkPath);

        renderNewPosts(newPath, sourcePostsPath, targetPath, header, String.format(footer, "\"\"", "\"\"", "\"\""));
        generateIndex(sourcePostsPath, targetPath, header, link, footer);
        copyFiles(sourcePath, targetPath, new HashSet<>(Arrays.asList("header.html", "footer.html")));
    }

    static void validateFileNames(String folderPath) throws Exception {
        val expected = "Some-blog-post-name-<yyyy-MM-dd-HH-mm>.md";
        val example = "Your-wise-blog-post-name-2015-07-15-00-45.md";
        for (val path : Files.newDirectoryStream(Paths.get(folderPath), (p) -> (!Files.isDirectory(p)) && (!p.startsWith(".")))) {
            val fileName = Objects.requireNonNull(path.getFileName()).toString();
            val fileNamePieces = fileName.split("\\.");
            val errMsg = String.format("File name '%s' does not match the expected format " + "'%s' - e.g. '%s'", fileName, expected, example);
            check(fileNamePieces.length == 2, errMsg);
            check(fileNamePieces[0].split("-").length > 5, errMsg);
        }
    }

    static PostSummary fileNameToPostSummary(String fileName) {
        @NonNull val fileNameNoExt = fileName.split("\\.")[0];
        val url = fileNameNoExt + ".html";
        val fileNamePiecesNoExt = fileNameNoExt.split("-");
        val fileNameNoDate = Arrays.copyOf(fileNamePiecesNoExt, fileNamePiecesNoExt.length - 5);
        val title = String.join(" ", fileNameNoDate);
        val maxi = fileNamePiecesNoExt.length - 1;
        val date = LocalDateTime.of(
                Integer.parseInt(fileNamePiecesNoExt[maxi - 4]),
                Integer.parseInt(fileNamePiecesNoExt[maxi - 3]),
                Integer.parseInt(fileNamePiecesNoExt[maxi - 2]),
                Integer.parseInt(fileNamePiecesNoExt[maxi - 1]),
                Integer.parseInt(fileNamePiecesNoExt[maxi]));
        return new PostSummary(url, title, date);
    }

    static String tabs(final int n) {
        StringBuilder res = new StringBuilder(" ");
        for (int i = 1; i < n; ++i) res.append(" ");
        return res.toString();
    }

    static void generateIndex(String sourcePostsPath, String targetPath, String header, final String link, final String footer) throws Exception {
        final AtomicInteger counter = new AtomicInteger(1);
        val html = Files.list(Paths.get(sourcePostsPath))
                .filter((p) -> !Files.isDirectory(p))
                .map((p) -> fileNameToPostSummary(p.getFileName().toString()))
                .sorted((f, s) -> s.getDate().compareTo(f.getDate()))
                .map((s) -> PostSummary.toLink(link, s))
                .collect(Collectors.groupingBy((k) -> {
                    int no = counter.getAndIncrement();
                    return 1 <= no && no <= pageSize ? 1 : no % pageSize == 0 ? no / pageSize : no / pageSize + 1;
                }, Collectors.joining("<br/>\n")));
        final Integer pageCount = html.size();
        html.forEach((k, v) -> {
            String pageFooter = String.format(footer, k, pageCount, pageSize);
            writeFile(header + "\n" + v + "\n" + pageFooter, targetPath + ("/index" + (k == 1 ? "" : k) + ".html"));
        });
    }

    static void renderNewPosts(String newPath, String sourcePostsPath, String targetPath, String header, String footer) throws Exception {
        for (val newSrcFile : Files.newDirectoryStream(Paths.get(newPath), (p) -> (!Files.isDirectory(p)))) {
            val html = render(newSrcFile, header, footer);
            @NonNull val srcFileName = Objects.requireNonNull(newSrcFile.getFileName()).toString();
            @NonNull val array = srcFileName.split("\\.");
            val destFileName = String.join(" ", copyOf(array, array.length - 1)).concat(".html");
            writeFile(html, targetPath + "/" + destFileName);
            val processedSrcFilePath = sourcePostsPath + "/" + srcFileName;
            moveFile(newSrcFile, Paths.get(processedSrcFilePath));
        }
    }

    static void requireFile(String pathToFile) {
        requireFile(pathToFile, false);
    }

    static void requireFolder(String pathToFile) {
        requireFile(pathToFile, true);
    }

    static void requireFile(String pathToFile, boolean mustBeFolder) {
        val requiredFile = Paths.get(pathToFile);
        val isFolder = Files.isDirectory(requiredFile);
        val folderOrFile = mustBeFolder ? "folder" : "file";
        check(Files.exists(requiredFile) && ((mustBeFolder) == isFolder), String.format("%s does not exist or is not a %s", pathToFile, folderOrFile));
    }

    static void moveFile(Path srcFile, Path destFile) throws Exception {
        out.println(String.format("Moving %s to %s ...", srcFile.toString(), destFile.toString()));
        Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
    }

    static void writeFile(String fileContent, String filePath) {
        out.println(String.format("Writing %s ...", filePath));
        try {
            Files.write(Paths.get(filePath), fileContent.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            out.println("写入文件失败:" + filePath);
            e.printStackTrace();
        }
    }

    static void createFolderIfNotExists(String pathToFolder) throws Exception {
        val folder = Paths.get(pathToFolder);
        if (Files.notExists(folder) || !Files.isDirectory(folder)) Files.createDirectory(folder);
    }

    static String render(Path file, String header, String footer) throws Exception {
        val srcFilePath = file.toString();
        out.println(String.format("%nRendering %s ...", srcFilePath));
        val markdown = stringFromFile(srcFilePath);
        val html = markdownToHtml.apply(markdown);
        return header + "\n" + html + "\n" + footer;
    }

    static String stringFromFile(String filePath) throws Exception {
        final String ILLEGAL_CHARACTER = "[^\u4E00-\u9FA5\u3000-\u303F\uFF00-\uFFEF\u0000-\u007F\u201c-\u201d]";
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8).replaceAll(ILLEGAL_CHARACTER, " ").trim();
    }

    static String noEndSlash(String str) {
        return str.endsWith("/") || str.endsWith("\\") ? str.substring(0, str.length() - 1) : str;
    }

    static void copyFiles(String srcFolderPath, String destFolderPath, final Set<String> excludeFiles) throws Exception {
        for (val file : Files.newDirectoryStream(Paths.get(srcFolderPath), (p) -> !excludeFiles.contains(Objects.requireNonNull(p.getFileName()).toString()) && !Files.isDirectory(p))) {
            val destFile = Paths.get(String.format("%s/%s", destFolderPath, file.getFileName().toString()));
            out.println(String.format("Copying %s to %s ...", file.toString(), destFile.toString()));
            Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void check(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    static String markdownToHtmlByGithub(String markdown) {
        try {
            final String url = "https://api.github.com/markdown/raw";
            final int timeout = 10000;
            final URL connectionBuilder = new URL(url);
            URLConnection connection = connectionBuilder.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setDoOutput(true);
            connection.connect();
            try (OutputStream o = connection.getOutputStream(); OutputStreamWriter out = new OutputStreamWriter(o, StandardCharsets.UTF_8)) {
                out.write(markdown);
            }
            try (InputStream i = connection.getInputStream(); ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = i.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                return result.toString("UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    public static String simpleTemplate(String templateStr, Map<String, ?> data, String... defaultNullReplaceVals) {
        if (templateStr == null) return null;
        if (data == null) data = Collections.EMPTY_MAP;
        String nullReplaceVal = defaultNullReplaceVals.length > 0 ? defaultNullReplaceVals[0] : "";
        StringBuffer newValue = new StringBuffer(templateStr.length());
        Matcher matcher = pattern.matcher(templateStr);
        while (matcher.find()) {
            String key = matcher.group(1);
            String r = data.get(key) != null ? data.get(key).toString() : nullReplaceVal;
            matcher.appendReplacement(newValue, r.replaceAll("\\\\", "\\\\\\\\")); //这个是为了替换windows下的文件目录在java里用\\表示
        }
        matcher.appendTail(newValue);
        return newValue.toString();
    }

    @Value
    static class PostSummary {
        final static Format df = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).toFormat();
        final static Format dfIso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.toFormat();
        String url;
        String title;
        LocalDateTime date;

        public static String toLink(String tpl, PostSummary ps) {
            Map<String, String> map = new HashMap<>();
            map.put("tabs1", tabs(2));
            map.put("tabs2", tabs(3));
            map.put("tabs3", tabs(4));
            map.put("tabs4", tabs(4));
            map.put("tabs5", tabs(3));
            map.put("tabs6", tabs(2));
            map.put("ps.url", ps.getUrl());
            map.put("ps.title", ps.getTitle());
            map.put("datetime", dfIso.format(ps.getDate()));
            map.put("time", df.format(ps.getDate()));
            return simpleTemplate(tpl, map);
        }
    }
}