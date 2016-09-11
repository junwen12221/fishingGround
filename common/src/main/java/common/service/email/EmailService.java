//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package common.service.email;

public interface EmailService {
    void sendMail(String var1, String var2, String var3) throws Exception;

    void sendBatchMail(String[] var1, String var2, String var3);

    void sendMailWithFile(String var1, String var2, String var3, String[] var4) throws Exception;

    void sendBatchMailWithFile(String[] var1, String var2, String var3, String[] var4) throws Exception;
}
