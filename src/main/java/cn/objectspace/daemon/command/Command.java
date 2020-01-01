package cn.objectspace.daemon.command;


import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.objectspace.daemon.cache.DaemonCache;
import cn.objectspace.daemon.util.JavaShellUtil;

public class Command {
	/**
	 * ���ݽ��������ر�ĳ��΢����
	 * @return
	 */
	public static synchronized boolean closeService(String processName) {
		if(JavaShellUtil.executeShell("ps -ef | grep "+processName+" | grep -v grep | awk '{print $2}' | xargs kill -9")) {
			//���ĳ��΢����رճɹ�����ô�������е�Ҳ���
			DaemonCache.getMicroServiceCache().remove(processName);
			return true;
		}else {
			//����ر�ʧ�ܣ���ֱ�ӷ���false
			return false;
		}
	}
	
	/**
	 * ����ĳ��΢����
	 * @param jarName
	 * @param path
	 * @return
	 */
	public static synchronized boolean openServiceFirst(String jarName,String path) {
		boolean successFlag = JavaShellUtil.executeShell("nohup java -jar "+path+jarName+" > ./sys-log.log &  ");
		String pid = JavaShellUtil.executeShellAndReturnShell("ps -ef|grep \"+processName+\"|grep -v grep|awk '{print $2}'");
		if(successFlag) {
			//��������ɹ�����΢����jar���ͽ��̺ŷ��뻺����
			DaemonCache.getMicroServiceCache().put(jarName, pid);
		}
		return successFlag;
	}
	
	/**
	 * �鿴�ĸ�΢�����Ѿ�崻�
	 * @param processMap
	 * @return
	 */
	public static synchronized List<String> microServiceIsDown(Map<String,Object> processMap) {
		List<String> downList = new LinkedList<String>();
		//processName��΢����Ľ�������Ӧ����΢����ʵ����
		for(String processName :processMap.keySet()) {
			String pid = JavaShellUtil.executeShellAndReturnShell("ps -ef|grep "+processName+"|grep -v grep|awk '{print $2}'");
			if(!processMap.get(processName).equals(pid)) {
				//ȡ���Ľ��̺Ų����ڸý��̣���˵���ý����Ѿ��������ر�
				//����������崻��б�
				downList.add(processName);
				//�����Ƴ���������΢����Ļ�����
				processMap.remove(processName);
			}
		}
		return downList;
	}
	
	/**
	 * ����������
	 * @return
	 */
	public static synchronized boolean reboot() {
		return JavaShellUtil.executeShell("reboot");
	}
}
