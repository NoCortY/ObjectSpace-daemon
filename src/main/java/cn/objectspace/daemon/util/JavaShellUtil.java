package cn.objectspace.daemon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaShellUtil {

    /**
     * 执行shell命令并返回控制台输出的结果,执行失败返回null
     * @param shellCommand
     * @return
     */
    public static String executeShellAndReturnShell(String shellCommand) {
        int success = 0;

        StringBuffer resStr = new StringBuffer();
        //日志串
        StringBuffer stringBuffer = new StringBuffer();
        //接受控制台输出
        BufferedReader bufferedReader = null;

        try {
            Process pid = null;
            String[] cmd = {"/bin/sh", "-c", shellCommand};
            //执行Shell命令
            pid = Runtime.getRuntime().exec(cmd);
            stringBuffer.append("执行命令:"+shellCommand+"\r\n");
            if (pid != null) {
                stringBuffer.append("进程号:"+pid.toString()+"\r\n");
                bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024);
                //获取执行结果
                success = pid.waitFor();
                if(success!=0) {
                    stringBuffer.append("命令执行失败\r\n");
                }
            } else {
                stringBuffer.append("Exception:命令未执行\r\n");
                return null;
            }
            stringBuffer.append("执行结果:\r\n");
            String line = null;
            //读取Shell的输出内容，并添加到stringBuffer中
            while (bufferedReader != null && (line = bufferedReader.readLine()) != null) {
                resStr.append(line).append("\r\n");
                stringBuffer.append(line).append("\r\n");
            }
        } catch (Exception ioe) {
            stringBuffer.append("命令执行时发生异常:");
            stringBuffer.append("异常信息:"+ioe.getMessage());
            return null;
        } finally {
            LogUtil.writeLog(stringBuffer.toString());
            //关闭流
            if(bufferedReader!=null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return resStr.toString();
    }

    /**
     * 执行命令，并直接返回是否执行成功
     * @param shellCommand
     * @return
     */
    public static boolean executeShell(String shellCommand) {
        int success = 0;

        //日志串
        StringBuffer stringBuffer = new StringBuffer();
        //接受控制台输出
        BufferedReader bufferedReader = null;

        try {
            Process pid = null;
            String[] cmd = {"/bin/sh", "-c", shellCommand};
            //执行Shell命令
            pid = Runtime.getRuntime().exec(cmd);
            stringBuffer.append("执行命令:"+shellCommand+"\r\n");
            if (pid != null) {
                stringBuffer.append("进程号:"+pid.toString()+"\r\n");
                bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024);
                //获取执行结果
                success = pid.waitFor();
            } else {
                stringBuffer.append("Exception:命令未执行\r\n");
                return false;
            }
            stringBuffer.append("执行结果:\r\n");
            String line = null;
            //读取Shell的输出内容，并添加到stringBuffer中
            while (bufferedReader != null && (line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line).append("\r\n");
            }
        } catch (Exception ioe) {
            stringBuffer.append("命令执行时发生异常:");
            stringBuffer.append("异常信息:"+ioe.getMessage());
            return false;
        } finally {
            LogUtil.writeLog(stringBuffer.toString());
            //关闭流
            if(bufferedReader!=null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if(success==0) return true;
        else return false;
    }
}