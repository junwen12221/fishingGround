package JavaStatic;

import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.List;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Format;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.System.out;

/**
 * Created by karak on 16-10-3.
 */
public final class JavaStatic {
    final static String GHMDRendererUrl = "https://api.github.com/markdown/raw";
    final static int timeout = 10000;
    final static String defaultSource = "source";
    final static String defaultTarget = "target";
    final static String ILLEGAL_CHARACTER = "[^\u4E00-\u9FA5\u3000-\u303F\uFF00-\uFFEF\u0000-\u007F\u201c-\u201d]";
    static RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectTimeout(timeout)
            .setSocketTimeout(timeout)
            .setConnectionRequestTimeout(timeout)
            .build();

    public static void main(String[] args1) throws Exception {
        String[] args = new String[]{"D:/Users/karakapi/zhuomian/static - 副本"};

        val options = validateArgs(args);
        if (!options.isPresent()) return;

        val path = options.get()[0];
        val source = options.get()[1];
        val target = options.get()[2];

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

        val header = stringFromFile(headerPath);
        val footer = stringFromFile(footerPath);

        renderNewPosts(newPath, sourcePostsPath, targetPath, header, footer);
        generateIndex(sourcePostsPath, targetPath, header, footer);
        copyFiles(sourcePath, targetPath, new HashSet<>(List.of("header.html", "footer.html")));
    }

    static void validateFileNames(String folderPath) throws Exception {
        val expected = "Some-blog-post-name-<yyyy-MM-dd-HH-mm>.md";
        val example = "Your-wise-blog-post-name-2015-07-15-00-45.md";
        for (val path : Files.newDirectoryStream(Paths.get(folderPath),
                (p) -> (!Files.isDirectory(p)) && (!p.startsWith(".")))) {
            @NonNull val fileName = path.getFileName().toString();
            @NonNull val fileNamePieces = fileName.split("\\.");
            val errMsg = String.format("File name '%s' does not match the expected format " + "'%s' - e.g. '%s'", fileName, expected, example);
            Assert.check(fileNamePieces.length == 2, errMsg);
            Assert.check(fileNamePieces[0].split("-").length > 5, errMsg);
        }
    }

    static PostSummary fileNameToPostSummary(String fileName) {
        @NonNull val fileNameNoExt = fileName.split("\\.")[0];
        val url = fileNameNoExt + ".html";
        val fileNamePiecesNoExt = List.from(fileNameNoExt.split("-"));
        val fileNameNoDate = fileNamePiecesNoExt.subList(0, fileNamePiecesNoExt.length() - 5);
        val title = fileNameNoDate.stream().collect(Collectors.joining(" "));
        val maxi = fileNamePiecesNoExt.length() - 1;
        val date = LocalDateTime.of(
                Integer.parseInt(fileNamePiecesNoExt.get(maxi - 4)),
                Integer.parseInt(fileNamePiecesNoExt.get(maxi - 3)),
                Integer.parseInt(fileNamePiecesNoExt.get(maxi - 2)),
                Integer.parseInt(fileNamePiecesNoExt.get(maxi - 1)),
                Integer.parseInt(fileNamePiecesNoExt.get(maxi)));
        return new PostSummary(url, title, date);
    }

    static String tabs(final int n) {
        StringBuilder res = new StringBuilder(" ");
        for (int i = 1; i < n; ++i) res.append(" ");
        return res.toString();
    }

    static void generateIndex(String sourcePostsPath, String targetPath, String header, String footer) throws Exception {
        val html = Files.list(Paths.get(sourcePostsPath))
                .filter((p) -> !Files.isDirectory(p))
                .map((p) -> fileNameToPostSummary(p.getFileName().toString()))
                .sorted((f, s) -> f.compare(s))
                .map(PostSummary::toLink)
                .collect(Collectors.joining("<br/>\n"));
        writeFile(header + "\n" + html + "\n" + footer, targetPath.concat("/index.html"));
    }

    static void renderNewPosts(
            String newPath,
            String sourcePostsPath,
            String targetPath,
            String header,
            String footer) throws Exception {
        for (val newSrcFile : Files.newDirectoryStream(Paths.get(newPath),
                (p) -> (!Files.isDirectory(p)))) {
            val html = render(newSrcFile, header, footer);
            @NonNull val srcFileName = newSrcFile.getFileName().toString();
            @NonNull val list = List.from(srcFileName.split("\\."));
            val destFileName = list.subList(0, list.length() - 1).stream().collect(Collectors.joining(" ")).concat(".html");
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
        Assert.check(Files.exists(requiredFile) && ((mustBeFolder) == isFolder), String.format("%s does not exist or is not a %s", pathToFile, folderOrFile));
    }

    static Optional<String[]> validateArgs(String[] args) {
        switch (args.length) {
            case 1:
                return Optional.of(new String[]{noEndSlash(args[0]), defaultSource, defaultTarget});
            case 2:
                return Optional.of(new String[]{noEndSlash(args[0]), noEndSlash(args[1]), defaultTarget});
            case 3:
                return Optional.of(new String[]{noEndSlash(args[0]), noEndSlash(args[1]), noEndSlash(args[2])});
            default:
                out.println("Usage: java -jar scalatic-x.x.x <blogPath> ");
                out.println("[<source> default 'source'] [<target> default 'target']");
                return Optional.empty();
        }
    }

    static void moveFile(Path srcFile, Path destFile) throws Exception {
        out.println(String.format("Moving %s to %s ...", srcFile.toString(), destFile.toString()));
        Files.move(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
    }

    static Path writeFile(String fileContent, String filePath) throws Exception {
        out.println(String.format("Writing %s ...", filePath));
        return Files.write(Paths.get(filePath), fileContent.getBytes(StandardCharsets.UTF_8));
    }

    static void createFolderIfNotExists(String pathToFolder) throws Exception {
        val folder = Paths.get(pathToFolder);
        if (Files.notExists(folder) || !Files.isDirectory(folder)) Files.createDirectory(folder);
    }

    static String render(Path file, String header, String footer) throws Exception {
        val srcFilePath = file.toString();
        out.println(String.format("%nRendering %s ...", srcFilePath));
        val markdown = stringFromFile(srcFilePath);
        HttpPost httppost = new HttpPost(GHMDRendererUrl);
        httppost.setConfig(defaultRequestConfig);
        httppost.setHeader("Content-Type", "text/plain");
        httppost.setHeader("Charset", "UTF-8");
        httppost.setEntity(new StringEntity(markdown));
        @Cleanup CloseableHttpClient httpclient = HttpClients.createDefault();
        @Cleanup CloseableHttpResponse response = httpclient.execute(httppost);
        val html = EntityUtils.toString(response.getEntity());
        val htmlFull = header + "\n" + html + "\n" + footer;
        return htmlFull;
    }

    static String stringFromFile(String filePath) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8).replaceAll(ILLEGAL_CHARACTER, " ").trim();
    }

    static String noEndSlash(String str) {
        return str.endsWith("/") || str.endsWith("\\") ? str.substring(0, str.length() - 1) : str;
    }

    static void copyFiles(String srcFolderPath, String destFolderPath, Set<String> excludeFiles) throws Exception {
        for (val file : Files.newDirectoryStream(Paths.get(srcFolderPath),
                (p) -> !excludeFiles.contains(p.getFileName().toString()) && !Files.isDirectory(p))) {
            @NonNull val destFile = Paths.get(String.format("%s/%s", destFolderPath, file.getFileName().toString()));
            out.println(String.format("Copying %s to %s ...", file.toString(), destFile.toString()));
            Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Value
    static class PostSummary {
        final static Format df = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).toFormat();
        final static Format dfIso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.toFormat();
        String url;
        String title;
        LocalDateTime date;

        public static String toLink(PostSummary ps) {
            return String.format("%s<article>%n%s<header>%n", tabs(2), tabs(3)) +
                    String.format("%s<a href='%s' class='blog-index-link'>%s</a>%n", tabs(4), ps.getUrl(), ps.getTitle()) +
                    String.format("%s<time pubdate datetime='%s' class='blog-index-date'>", tabs(4), dfIso.format(ps.getDate())) +
                    String.format("%s</time>%n%s</header>%n%s</article>", df.format(ps.getDate()), tabs(3), tabs(2));
        }

        public int compare(PostSummary o2) {
            return o2.date.compareTo(this.date);
        }
    }

}