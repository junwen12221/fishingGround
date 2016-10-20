package cn.lightFish.staticBlog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

class Tree {
    public List<Tree> children;
    public File file;
    public Tree top;

    public Tree(File file, Tree top) {
        if (file.isDirectory()) {
            children = new ArrayList<>();
        }
        this.file = file;
        this.top = top;
    }

    public static void transform(File f, Tree tree, Set<File> pointDir) {
        File[] childs = f.listFiles();
        if (childs == null) return;
        for (File it : childs) {
            if (it.isDirectory()) {
                Tree child = new Tree(it, tree);
                tree.children.add(child);
                transform(it, child, pointDir);
            } else {
                tree.children.add(new Tree(it, tree));
                pointDir.add(it.getParentFile());
            }
        }
    }

    public void toJsonWithDirectory(StringBuilder s, BiConsumer<StringBuilder, File> fileNameFun) {
        s.append("{").append("\"name\":").append("\"");
        fileNameFun.accept(s, file);
        if (top != null) {
            s.append("\",\"top\":\"");
            fileNameFun.accept(s, top.file);
            s.append("\"");
        }
        if (children == null) {
            s.append("}");
        } else {
            s.append(",\"children\": [");
            if (children.size() != 0) {
                int i = 0;
                for (; i < (children.size() - 1); ++i) {
                    children.get(i).toJsonWithDirectory(s, fileNameFun);
                    s.append(",\n");
                }
                children.get(i).toJsonWithDirectory(s, fileNameFun);
            }
            s.append("]}");
        }
    }
}