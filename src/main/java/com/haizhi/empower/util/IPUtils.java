package com.haizhi.empower.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CristianWindy
 * @date
 **/
public class IPUtils {

    public static final String IPREG = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";


    /**
     * IP是否符合规范
     *
     * @param input
     * @return
     */
    public static boolean isIPMatches(String input) {
        boolean matches = false;
        Pattern ipPattern = Pattern.compile(IPREG);
        if (input.contains(";")) {
            String[] split = input.split(";");
            for (String temp : split) {
                matches = isIPMatches(temp);
            }
        } else if (input.contains("-")) {
            String[] split = input.split("-");
            for (String temp : split) {
                matches = isIPMatches(temp);
            }
        } else if (input.contains("*")) {
            input = input.replace("*", "1");
            matches = isIPMatches(input);
        } else {
            matches = ipPattern.matcher(input).matches();
        }

        return matches;
    }

    public static List<String> ips = new ArrayList<>();

    static {
        ips.add("111.207.151.84");
        ips.add("111.207.151.84;111.207.151.85");
        ips.add("111.207.151.80-111.207.151.86");
        ips.add("111.207.151.80-111.207.151.86;111.207.151.87");
        ips.add("111.207.151.*");
        ips.add("111.207.151.1*-111.207.151.2*");
        ips.add("*.*.*.1");
        ips.add("*.168.1.*");
        ips.add("*.168.1.1");
    }

    /**
     * 校验IP地址合法性
     *
     * @param str
     * @return
     */
    public static boolean isURL(String str) {
        //转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"  //https、http、ftp、rtsp、mms
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return str.matches(regex);
    }

    /**
     * 获取当前请求的ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    public static void main(String[] args) {
        for (String temp : ips) {
            boolean flag = isIPMatches(temp);
            System.out.println(flag);
        }
    }
}
