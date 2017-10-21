package cn.lightFish.staticBlog;

import lombok.SneakyThrows;
import lombok.val;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

/**
 * Created by karak on 16-10-18.
 */
public final class K {

    final static Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");

    public static void requireFile(Path pathToFile) {
        requireFile(pathToFile, false);
    }

    public static void requireFolder(Path pathToFile) {
        requireFile(pathToFile, true);
    }

    public static void requireFile(Path requiredFile, boolean mustBeFolder) {
        boolean isFolder = Files.isDirectory(requiredFile);
        String folderOrFile = mustBeFolder ? "folder" : "file";
        check(Files.exists(requiredFile) && ((mustBeFolder) == isFolder), String.format("%s does not exist or is not a %s", requiredFile, folderOrFile));
    }

    @SneakyThrows
    public static void moveFile(Path srcFile, Path destFile) {
        out.format("Moving %s to %s ...%n", srcFile.toString(), destFile.toString());
        Path dir = Objects.requireNonNull(destFile.getParent());
        if (!Files.exists(dir)) Files.createDirectory(dir);
        Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
    }

    @SneakyThrows
    public static void writeFile(String fileContent, Path filePath) {
        out.format("Writing %s ...%n", filePath);
        Files.write(filePath, fileContent.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static void createFolderIfNotExists(Path pathToFolder) {
        if (Files.notExists(pathToFolder) || !Files.isDirectory(pathToFolder)) Files.createDirectory(pathToFolder);
    }

    public static void copyFiles(Path srcFolderPath, Path destFolderPath, final Set<Path> excludeFiles) throws Exception {
        for (Path file : Files.newDirectoryStream(srcFolderPath, (p) -> !excludeFiles.contains(p) && !Files.isDirectory(p))) {
            Path destFile = destFolderPath.resolve(file.getFileName());
            out.format("Copying %s to %s ...%n", file.toString(), destFile.toString());
            Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void check(boolean expression, String message) {
        if (!expression) throw new IllegalStateException(message);
    }

    @SneakyThrows
    public static String stringFromFile(Path filePath) {
        final String ILLEGAL_CHARACTER = "[^\u4E00-\u9FA5\u3000-\u303F\uFF00-\uFFEF\u0000-\u007F\u201c-\u201d]";
        return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8).replaceAll(ILLEGAL_CHARACTER, " ").trim();
    }

    public static String noEndSlash(String str) {
        return str.endsWith("/") || str.endsWith("\\") ? str.substring(0, str.length() - 1) : str;
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
            Object value = data.get(key);
            if (value != null) {
                matcher.appendReplacement(newValue, value.toString().replaceAll("\\\\", "\\\\\\\\"));
                continue;
            }
            if (defaultNullReplaceVals.length > 0) {
                matcher.appendReplacement(newValue, nullReplaceVal);
            }
        }
        matcher.appendTail(newValue);
        return newValue.toString();
    }
}
