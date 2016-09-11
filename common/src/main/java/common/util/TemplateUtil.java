//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package common.util;

import java.io.File;
import java.util.Map;

public interface TemplateUtil<T, S extends Map> {

    public boolean createHtmlFile(T engine, String tempFileName, File file, S context);

    public T initEngine(String genDir) throws Exception;

    public  String createTemplateFile(String fileContext, String createDirUrl) ;
}
