package cn.lightfish.watch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author chuer
 * @Description: 类转换
 * @date 2015年5月22日 下午3:32:56 
 * @version V1.0
 */
public class Transformer implements ClassFileTransformer {


    /**
     * 此方法获得新类的字节数据，发送给JVM，JVM会自动替换旧类的字节码数据
     */
    public byte[] transform(ClassLoader l, String className, Class<?> c,
            ProtectionDomain pd, byte[] b) throws IllegalClassFormatException {

        className = className.replace('/', '.');
        // 重新载入类文件（完整路径）
        String classNameSimple = className.substring(className.lastIndexOf('.') + 1);
        if (!classNameSimple.equals("TransClass")) {
            return null;
        }

        String newClassFile = "D:/com/instrument/" + classNameSimple + ".class";

        System.out.println(newClassFile);
        return getBytesFromFile(newClassFile);
    }
    /**
     * 获得class文件的字节数据
     * @param fileName
     * @return
     */
    public static byte[] getBytesFromFile(String fileName) {
        File file = new File(fileName);
        long length = file.length();
        try (InputStream is = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) length];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new Exception("Could not completely read file "
                        + file.getName());
            }
            is.close();
            return bytes;
        } catch (Exception e) {
            System.out.println("error occurs in _ClassTransformer!"
                    + e.getClass().getName());
            return null;
        }
    }
}