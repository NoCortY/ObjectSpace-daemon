package cn.objectspace.daemon.command;

import cn.objectspace.daemon.cache.DaemonCache;
import cn.objectspace.daemon.util.JavaShellUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Command {
    /**
     * 根据进程名，关闭某个微服务
     * @return
     */
    public static synchronized boolean closeService(String processName) {
        if(JavaShellUtil.executeShell("ps -ef | grep "+processName+" | grep -v grep | awk '{print $2}' | xargs kill -9")) {
            //如果某个微服务关闭成功，那么将缓存中的也清除
            DaemonCache.getMicroServiceCache().remove(processName);
            return true;
        }else {
            //如果关闭失败，则直接返回false
            return false;
        }
    }

    /**
     * 启动某个微服务
     * @param jarName
     * @param path
     * @return
     */
    public static synchronized boolean openServiceFirst(String jarName,String path) {
        boolean successFlag = JavaShellUtil.executeShell("nohup java -jar "+path+jarName+" > ./sys-log.log &  ");
        String pid = JavaShellUtil.executeShellAndReturnShell("ps -ef|grep \"+processName+\"|grep -v grep|awk '{print $2}'");
        if(successFlag) {
            //如果启动成功，将微服务jar名和进程号放入缓存中
            DaemonCache.getMicroServiceCache().put(jarName, pid);
        }
        return successFlag;
    }

    /**
     * 查看哪个微服务已经宕机
     * @param processMap
     * @return
     */
    public static synchronized List<String> microServiceIsDown(Map<String,Object> processMap) {
        List<String> downList = new LinkedList<String>();
        //processName是微服务的进程名，应该是微服务实例名
        for(String processName :processMap.keySet()) {
            String pid = JavaShellUtil.executeShellAndReturnShell("ps -ef|grep "+processName+"|grep -v grep|awk '{print $2}'");
            if(!processMap.get(processName).equals(pid)) {
                //取出的进程号不等于该进程，则说明该进程已经非正常关闭
                //将它加入已宕机列表
                downList.add(processName);
                //并且移除出已启动微服务的缓存中
                processMap.remove(processName);
            }
        }
        return downList;
    }

    /**
     * 重启服务器
     * @return
     */
    public static synchronized boolean reboot() {
        return JavaShellUtil.executeShell("reboot");
    }
}