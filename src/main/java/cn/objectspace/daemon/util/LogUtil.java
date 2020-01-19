package cn.objectspace.daemon.util;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 记录日志的工具
 * @author nocor
 *
 */
public class LogUtil {
    //日志路径
    private static final String basePath = "./cloud_daemon_logs";
    //记录shell执行状况的日志文件的位置
    private static final String executeShellLogFile = basePath+"excuteShell.log";
    public static synchronized void writeLog(String logContent) {
        makeDirPath(executeShellLogFile);
        OutputStream outStream = null;
        OutputStreamWriter outputStreamWriter = null;
        //当前时间
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //追加写模式
            outStream = new FileOutputStream(executeShellLogFile,true);
            outputStreamWriter = new OutputStreamWriter(outStream,"UTF-8");
            outputStreamWriter.write(date.format(new Date())+" "+logContent+"\r\n");
        } catch (Exception e) {
            System.out.println("异常");
            System.out.println("异常信息:"+e.getMessage());
        }finally {
            try {
                if(outStream!=null){
                    outStream.close();
                }
                if(outputStreamWriter!=null){
                    outputStreamWriter.close();
                    outputStreamWriter.flush();
                }
            } catch (IOException e) {
                System.out.println("关闭流异常");
                System.out.println("异常信息:"+e.getMessage());
            }
        }
    }

    private static void makeDirPath(String targetAddr) {
        // TODO Auto-generated method stub
        File dirPath = new File(targetAddr);
        if(!dirPath.exists()) {
            dirPath.mkdirs();
        }
    }
}
