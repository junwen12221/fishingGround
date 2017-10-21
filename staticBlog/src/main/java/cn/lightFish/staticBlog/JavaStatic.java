package cn.lightFish.staticBlog;

import lombok.SneakyThrows;
import lombok.val;
import org.pegdown.PegDownProcessor;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.lightFish.staticBlog.K.*;
import static java.lang.System.out;

public final class JavaStatic {
    final static String defaultSource = "source";
    final static String defaultTarget = "target";
    static String host = "";
    static Integer pageSize = 5;    // 每页显示的条数
    static Function<String, String> markdownToHtml;
    static String linkSeparator = "<br/>\n";

    public static void main(String[] args) throws Exception {
        String[] options = validateArgs(args);
        if (options.length == 0) return;
        String path = options[0];
        String source = options[1];
        String target = options[2];

        Path newPath = Paths.get(path, "new");
        requireFolder(newPath);
        validateFileNames(newPath);
        Path sourcePath = Paths.get(path, source);
        requireFolder(sourcePath);

        Path sourcePostsPath = sourcePath.resolve("posts");
        createFolderIfNotExists(sourcePostsPath);
        Path targetPath = Paths.get(path, target);
        createFolderIfNotExists(targetPath);

        Path headerPath = sourcePath.resolve("header.html");
        requireFile(headerPath);
        Path footerPath = sourcePath.resolve("footer.html");
        requireFile(footerPath);
        Path linkPath = sourcePath.resolve("link.html");
        requireFile(linkPath);

        init(sourcePath);
        Map<String, String> map = new HashMap<>();
        map.put("host", host);
        String header = simpleTemplate(stringFromFile(headerPath), map);
        String footer = simpleTemplate(stringFromFile(footerPath), map);
        String link = stringFromFile(linkPath);

        Tree tree = new Tree(sourcePath.toFile(), null);
        Set<File> pointFile = new HashSet<>();
        Tree.transform(newPath.toFile(), tree, pointFile);
        StringBuilder content = new StringBuilder();
        tree.toJsonWithDirectory(content, (stringBuilder, file) -> {
            content.append(file.getName().replace(".md", ".html"));
        });
        Files.write(targetPath.resolve("content.js"), content.toString().getBytes(StandardCharsets.UTF_8));

        Map<String, String> indexMap = new HashMap<>(2);
        indexMap.put("currentPage", "\"\"");
        indexMap.put("size", "\"\"");
        String postFooter = simpleTemplate(footer, indexMap);
        pointFile.forEach((it) -> processNewPost(it.toPath(), sourcePostsPath, targetPath, header, postFooter));

        generateIndex(sourcePostsPath, targetPath, header, link, linkSeparator, footer);
        copyFiles(sourcePath, targetPath, new HashSet<>(Arrays.asList(
                Paths.get("header.html"),
                Paths.get("footer.html"),
                Paths.get("setting.properties"),
                Paths.get("link.html"))));
    }

    static String[] validateArgs(String[] args) {
        switch (args.length) {
            case 1:
                return new String[]{noEndSlash(args[0]), defaultSource, defaultTarget};
            case 2:
                return new String[]{noEndSlash(args[0]), noEndSlash(args[1]), defaultTarget};
            case 3:
                return new String[]{noEndSlash(args[0]), noEndSlash(args[1]), noEndSlash(args[2])};
            default:
                out.println("Usage: java -jar " + JavaStatic.class.getPackage().getName() + " <blogPath> ");
                out.println("[<source> default 'source'] [<target> default 'target']");
                return new String[0];
        }
    }

    static void init(Path sourcePath) throws IOException {
        Properties settings = new Properties();
        try (BufferedReader in = Files.newBufferedReader(sourcePath.resolve("setting.properties"), StandardCharsets.UTF_8)) {
            settings.load(in);
        }
        linkSeparator = settings.getProperty("linkseparator", "<br/>\n");
        pageSize = Integer.getInteger(settings.getProperty("pageSize"), 5);
        String mode = settings.getProperty("markdownMode", "pegDownProcessor");
        host = settings.getProperty("host", "");
        switch (mode) {
            case "github":
                markdownToHtml = JavaStatic::markdownToHtmlByGithub;
                break;
            default:
                final PegDownProcessor peg = new PegDownProcessor();
                markdownToHtml = (s) -> peg.markdownToHtml(s);
                break;
        }
    }

    @SneakyThrows
    static void processNewPost(Path newPath, Path sourcePostsPath, Path targetPath, String header, String footer) {
        Path relativizeNew = sourcePostsPath.relativize(newPath);
        if (relativizeNew.getNameCount() > 3) {// ../../new
            sourcePostsPath = sourcePostsPath.resolve(relativizeNew.subpath(3, relativizeNew.getNameCount()));
        }
        renderNewPosts(newPath, sourcePostsPath, targetPath, header, footer);
    }

    static void validateFileNames(Path folderPath) throws IOException {
        String expected = "Some-blog-post-name-<yyyy-MM-dd-HH-mm>.md";
        String example = "Your-wise-blog-post-name-2015-07-15-00-45.md";
        for (Path path : Files.newDirectoryStream(folderPath, (p) -> (!Files.isDirectory(p)) && (!p.startsWith(".")))) {
            String fileName = Objects.requireNonNull(path.getFileName()).toString();
            String[] fileNamePieces = fileName.split("\\.");
            String errMsg = String.format("File name '%s' does not match the expected format " + "'%s' - e.g. '%s'", fileName, expected, example);
            check(fileNamePieces.length == 2, errMsg);
            check(fileNamePieces[0].split("-").length > 5, errMsg);
        }
    }

    static PostSummary fileNameToPostSummary(String path, String fileName) {
        String fileNameNoExt = fileName.split("\\.")[0];
        String url = "".equals(path) ? fileNameNoExt + ".html" : path + "/" + fileNameNoExt + ".html";
        String[] fileNamePiecesNoExt = fileNameNoExt.split("-");
        String[] fileNameNoDate = Arrays.copyOf(fileNamePiecesNoExt, fileNamePiecesNoExt.length - 5);
        String title = String.join(" ", fileNameNoDate);
        int maxi = fileNamePiecesNoExt.length - 1;
        LocalDateTime date = LocalDateTime.of(
                Integer.parseInt(fileNamePiecesNoExt[maxi - 4]),
                Integer.parseInt(fileNamePiecesNoExt[maxi - 3]),
                Integer.parseInt(fileNamePiecesNoExt[maxi - 2]),
                Integer.parseInt(fileNamePiecesNoExt[maxi - 1]),
                Integer.parseInt(fileNamePiecesNoExt[maxi]));
        return new PostSummary(url, title, date);
    }

    static void generateIndex(Path sourcePostsPath, Path targetPath, String header, final String link, final String linkSeparator, final String footer) throws Exception {
        final AtomicInteger counter = new AtomicInteger(1);
        Map<Integer, String> htmlMap = Files.walk(sourcePostsPath)
                .filter((p) -> !Files.isDirectory(p))
                .map((file) -> {
                    Path relativizePath = targetPath.relativize(file);
                    Path url = relativizePath.subpath(3, relativizePath.getNameCount());
                    String fileName = url.getFileName().normalize().toString();
                    return url.getNameCount() == 1 ?
                            fileNameToPostSummary("", fileName) :
                            fileNameToPostSummary(url.getParent().toString(), fileName);
                })//目录对应的url
                .sorted((f, s) -> s.getDate().compareTo(f.getDate()))
                .map((s) -> PostSummary.toLink(link, s))
                .distinct()
                .collect(Collectors.groupingBy((k) -> {
                    int no = counter.getAndIncrement();
                    return 1 <= no && no <= pageSize ? 1 : no % pageSize == 0 ? no / pageSize : no / pageSize + 1;
                }, Collectors.joining(linkSeparator)));
        final Integer pageCount = htmlMap.size();
        htmlMap.forEach((k, v) -> {
            Map<String, String> map = new HashMap<>(3);
            map.put("currentPage", k.toString());
            map.put("pageCount", pageCount.toString());
            map.put("pageSize", pageSize.toString());
            String pageFooter = simpleTemplate(footer, map);
            writeFile(header + "\n" + v + "\n" + pageFooter, targetPath.resolve("index" + (k == 1 ? "" : k) + ".html"));
        });
    }

    static void renderNewPosts(Path newPath, Path sourcePostsPath, Path targetPath, String header, String footer) throws Exception {
        for (Path newSrcFile : Files.newDirectoryStream(newPath, (p) -> (!Files.isDirectory(p)))) {
            String html = render(newSrcFile, header, footer);
            String srcFileName = Objects.requireNonNull(newSrcFile.getFileName()).toString();
            String[] array = srcFileName.split("\\.");
            Path destFileName = Paths.get(array[0].concat(".html"));
            Path relativizePath = targetPath.relativize(newPath);
            int count = relativizePath.getNameCount();
            if ((count >= 3)) {// ../../new
                relativizePath = relativizePath.subpath(2, relativizePath.getNameCount());
                targetPath = targetPath.resolve(relativizePath);
            }
            //与new 与post 目录结构一致
            if (!Files.exists(targetPath)) Files.createDirectories(targetPath);
            writeFile(html, targetPath.resolve(destFileName));
            Path processedSrcFilePath = sourcePostsPath.resolve(srcFileName);
            moveFile(newSrcFile, processedSrcFilePath);
        }
    }

    static String render(Path srcFilePath, String header, String footer) throws Exception {
        out.format("%nRendering %s ...%n", srcFilePath);
        String markdown = stringFromFile(srcFilePath);
        String html = markdownToHtml.apply(markdown);
        return header + "\n" + html + "\n" + footer;
    }

    @SneakyThrows
    static String markdownToHtmlByGithub(String markdown) {
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
    }
}