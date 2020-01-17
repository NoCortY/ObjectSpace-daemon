package cn.objectspace.daemon.util;

import org.hyperic.sigar.Sigar;

import java.io.*;

/**
* @Description: 服务器工具
* @Author: NoCortY
* @Date: 2020/1/17
*/
public class ServerUtil {
    /**
     * @Description: 初始化Sigar,自动加载sigar的依赖dll库
     * @Param: []
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/1/17
     */
    public static Sigar initSigar(String classPath){
        if(isWin()){
            if(classPath==null||classPath.length()==0)
                classPath = "C:\\Users\\NoCortY\\Downloads\\hyperic-sigar-1.6.4\\hyperic-sigar-1.6.4\\sigar-bin\\";
            //windows依赖的第一个dll
            InputStream inStream = ServerUtil.class.getResourceAsStream("/sigar/sigar-amd64-winnt.dll");
            FileUtil.copyFile(inStream,classPath+"sigar-amd64-winnt.dll");
            //windows依赖的第二个dll
            inStream = ServerUtil.class.getResourceAsStream("/sigar/sigar-x86-winnt.dll");
            FileUtil.copyFile(inStream,classPath+"sigar-x86-winnt.dll");
            //windows依赖的第三个dll
            inStream = ServerUtil.class.getResourceAsStream("/sigar/sigar-x86-winnt.lib");
            FileUtil.copyFile(inStream,classPath+"sigar-x86-winnt.lib");
        }else{
            if(classPath==null||classPath.length()==0) classPath="/usr/lib64";
            //linux64位依赖的dll
            InputStream inputStream = ServerUtil.class.getResourceAsStream("/sigar/libsigar-amd64-linux.so");
            FileUtil.copyFile(inputStream,classPath+"libsigar-amd64-linux.so");
            //linux32位依赖的dll
            inputStream = ServerUtil.class.getResourceAsStream("/sigar/libsigar-x86-linux.so");
            FileUtil.copyFile(inputStream,classPath+"libsigar-x86-linux.so");
        }
        //获取系统变量
        String path = System.getProperty("java.library.path");
        //为防止java.library.path重复加，此处判断了一下
        if (!path.contains(classPath)) {
            if (isWin()) {
                path += ";" + classPath;
                System.out.println(path);
            } else {
                path += ":" + classPath;
            }
            System.setProperty("java.library.path", path);
        }
        return new Sigar();
    }

    /**
     * @Description: 判断是否为windows系统
     * @Param: []
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/1/17
     */
    public static boolean isWin(){
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            return true;
        } else return false;
    }
    public static void main(String[] args){
        ServerUtil.initSigar(null);
    }
}

