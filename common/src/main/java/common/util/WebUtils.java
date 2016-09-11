//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package common.util;


public interface WebUtils<T> {
    public void setCookie(T response, String key, String value, int days);

    public void setCookie(T response, String key, String value, int days, String domain);

    public void setCookieOnSeconds(T response, String key, String value, int seconds);

    public String getCookie(T request, String key);

    public <S> void deleteCookie(T request, S response, String name);

    public <S> void deleteCookieDomain(T request, S response, String name, String domain);

    public String getIpAddr(T request);

    public String getServletRequestUrlParms(T request);

    public String getServletRequestUriParms(T request);

    public boolean checkLoginName(String value);

    public boolean checkMobile(String value);

    public boolean checkEmail(String value, int length) ;

    public  boolean isPasswordAvailable(String password) ;

    public boolean isAjaxRequest(T request);

    public boolean isNotAjaxRequest(T request);

    public String getUserAgent(T request);

    String regex = "\\<(.+?)\\>";

    public static String replaceTagHTML(String src) {
        return StringUtils.isNotEmpty(src) ? src.replaceAll(regex, "") : "";
    }


}
