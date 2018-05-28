package com.czh.tool.lantransfer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Enumeration;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static String getContentTypeByResourceName(String resourceName) {
        if (resourceName.endsWith(".css")) {
            return Constants.ContentType.CSS;
        } else if (resourceName.endsWith(".js")) {
            return Constants.ContentType.JS;
        } else if (resourceName.endsWith(".swf")) {
            return Constants.ContentType.SWF;
        } else if (resourceName.endsWith(".png")) {
            return Constants.ContentType.PNG;
        } else if (resourceName.endsWith(".jpg") || resourceName.endsWith(".jpeg")) {
            return Constants.ContentType.JPG;
        } else if (resourceName.endsWith(".woff")) {
            return Constants.ContentType.WOFF;
        } else if (resourceName.endsWith(".ttf")) {
            return Constants.ContentType.TTF;
        } else if (resourceName.endsWith(".svg")) {
            return Constants.ContentType.SVG;
        } else if (resourceName.endsWith(".eot")) {
            return Constants.ContentType.EOT;
        } else if (resourceName.endsWith(".mp3")) {
            return Constants.ContentType.MP3;
        } else if (resourceName.endsWith(".mp4")) {
            return Constants.ContentType.MP4;
        }
        return "";
    }

    public static String getFileSize(long fileLen) {
        String size;
        DecimalFormat df = new DecimalFormat("0.00");
        if (fileLen > 1024 * 1024) {
            size = df.format(fileLen * 1f / 1024 / 1024) + "MB";
        } else if (fileLen > 1024) {
            size = df.format(fileLen * 1f / 1024) + "KB";
        } else {
            size = fileLen + "B";
        }
        return size;
    }

}
