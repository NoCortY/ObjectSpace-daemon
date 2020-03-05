package cn.objectspace.daemon.util;

public class OSCheck {

    public static boolean isWin(){
        return getOSName().contains("win");
    }
    public static boolean isLinux(){
        return getOSName().contains("linux");
    }
    public static String getOSName(){
        return System.getProperty("os.name").toLowerCase();
    }
}
