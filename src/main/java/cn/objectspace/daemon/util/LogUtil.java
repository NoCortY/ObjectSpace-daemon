package cn.objectspace.daemon.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ��¼��־�Ĺ���
 * @author nocor
 *
 */
public class LogUtil {
	//��־·��
	private static final String basePath = "./cloud_daemon_logs";
	//��¼shellִ��״������־�ļ���λ��
	private static final String executeShellLogFile = basePath+"excuteShell.log";
	public static synchronized void writeLog(String logContent) {
		makeDirPath(executeShellLogFile);
		OutputStream outStream = null;
		OutputStreamWriter outputStreamWriter = null;
		//��ǰʱ��
		DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//׷��дģʽ
			outStream = new FileOutputStream(executeShellLogFile,true);
			outputStreamWriter = new OutputStreamWriter(outStream,"UTF-8");
			outputStreamWriter.write(date.format(new Date())+" "+logContent+"\r\n");
		} catch (Exception e) {
			System.out.println("�쳣");
			System.out.println("�쳣��Ϣ:"+e.getMessage());
		}finally {
			try {
				outStream.close();
				outputStreamWriter.flush();
				outputStreamWriter.close();
			} catch (IOException e) {
				System.out.println("�ر����쳣");
				System.out.println("�쳣��Ϣ:"+e.getMessage());
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
