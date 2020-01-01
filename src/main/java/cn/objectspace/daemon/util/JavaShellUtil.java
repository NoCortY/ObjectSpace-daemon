package cn.objectspace.daemon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaShellUtil {
    
    /**
     * ִ��shell������ؿ���̨����Ľ��,ִ��ʧ�ܷ���null
     * @param shellCommand
     * @return
     */
    public static String executeShellAndReturnShell(String shellCommand) {
    	int success = 0;
        
    	StringBuffer resStr = new StringBuffer();
        //��־��
        StringBuffer stringBuffer = new StringBuffer();
        //���ܿ���̨���
        BufferedReader bufferedReader = null;

        try {
            Process pid = null;
            String[] cmd = {"/bin/sh", "-c", shellCommand};
            //ִ��Shell����
            pid = Runtime.getRuntime().exec(cmd);
            stringBuffer.append("ִ������:"+shellCommand+"\r\n");
            if (pid != null) {
            	stringBuffer.append("���̺�:"+pid.toString()+"\r\n");
                bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024);
                //��ȡִ�н��
                success = pid.waitFor();
                if(success!=0) {
                	stringBuffer.append("����ִ��ʧ��\r\n");
                }
            } else {
                stringBuffer.append("Exception:����δִ��\r\n");
                return null;
            }
            stringBuffer.append("ִ�н��:\r\n");
            String line = null;
            //��ȡShell��������ݣ�����ӵ�stringBuffer��
            while (bufferedReader != null && (line = bufferedReader.readLine()) != null) {
            	resStr.append(line).append("\r\n");
                stringBuffer.append(line).append("\r\n");
            }
        } catch (Exception ioe) {
        	stringBuffer.append("����ִ��ʱ�����쳣:");
        	stringBuffer.append("�쳣��Ϣ:"+ioe.getMessage());
        	return null;
        } finally {
        	LogUtil.writeLog(stringBuffer.toString());
        	//�ر���
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
     * ִ�������ֱ�ӷ����Ƿ�ִ�гɹ�
     * @param shellCommand
     * @return
     */
    public static boolean executeShell(String shellCommand) {
        int success = 0;
        
        //��־��
        StringBuffer stringBuffer = new StringBuffer();
        //���ܿ���̨���
        BufferedReader bufferedReader = null;

        try {
            Process pid = null;
            String[] cmd = {"/bin/sh", "-c", shellCommand};
            //ִ��Shell����
            pid = Runtime.getRuntime().exec(cmd);
            stringBuffer.append("ִ������:"+shellCommand+"\r\n");
            if (pid != null) {
            	stringBuffer.append("���̺�:"+pid.toString()+"\r\n");
                bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024);
                //��ȡִ�н��
                success = pid.waitFor();
            } else {
                stringBuffer.append("Exception:����δִ��\r\n");
                return false;
            }
            stringBuffer.append("ִ�н��:\r\n");
            String line = null;
            //��ȡShell��������ݣ�����ӵ�stringBuffer��
            while (bufferedReader != null && (line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line).append("\r\n");
            }
        } catch (Exception ioe) {
        	stringBuffer.append("����ִ��ʱ�����쳣:");
        	stringBuffer.append("�쳣��Ϣ:"+ioe.getMessage());
        	return false;
        } finally {
        	LogUtil.writeLog(stringBuffer.toString());
        	//�ر���
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