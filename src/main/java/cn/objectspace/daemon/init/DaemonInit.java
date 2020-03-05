package cn.objectspace.daemon.init;

import cn.objectspace.daemon.cache.DaemonCache;
import cn.objectspace.daemon.pool.ConstantPool;
import cn.objectspace.daemon.util.FileUtil;
import cn.objectspace.daemon.util.OSCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DaemonInit implements ObjectInit {
    private Logger logger = LoggerFactory.getLogger(DaemonInit.class);
    @Override
    public boolean init(String[] args) {
        File file = null;
        if(OSCheck.isWin()){
            file = new File(ConstantPool.WINDOWS_INSTALL_PATH+"ocdae.os");
            if(!file.exists()){
                //如果文件不存在
                try {
                    file.createNewFile();
                    System.out.println("守护线程第一次启动...写入用户id和心跳地址");
                    //写入缓存
                    //明天修改：1.修改缓存的API。2.修改对象存储方式。3.写文件和读文件改为二进制流。4.继续优化代码
                    DaemonCache.getCacheMap().put("userId",args[0]);
                    DaemonCache.getCacheMap().put("pingUrl",args[1]);
                    //写入文件
                    FileU
                    FileUtil.writeFileAsString("userId="+args[0]+"\n"+"pingUrl="+args[1],file.getAbsolutePath());
                } catch (IOException e) {
                    logger.error("文件写入失败");
                    return false;
                }
            }else{
                //如果文件已经存在，那么直接从文件中读，放入缓存中
                String initStr = FileUtil.readFileAsString(file.getAbsolutePath());
                //解析
                if(initStr!=null){
                    String[] lineStr = initStr.split("\n");
                    for(String line:lineStr){
                        String[] keyValue = line.split("=");
                        //写入缓存
                        DaemonCache.getCacheMap().put(keyValue[0],keyValue[1]);
                    }
                }else{
                    System.out.println("文件已损坏");
                }

            }
        }else if(OSCheck.isLinux()){
            file = new File(ConstantPool.LINUX_INSTALL_PATH+"ocdae.os");
            if(!file.exists()){
                try {
                    file.createNewFile();
                    System.out.println("守护线程第一次启动...写入用户id和心跳地址");
                    FileUtil.writeFileAsString("userId="+args[0]+"\n"+"pingUrl="+args[1]+"\n",file.getAbsolutePath());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }else{
                //如果文件已经存在，那么直接从文件中读，放入缓存中
                String initStr = FileUtil.readFileAsString(file.getAbsolutePath());
                //解析
                if(initStr!=null){
                    String[] lineStr = initStr.split("\n");
                    for(String line:lineStr){
                        String[] keyValue = line.split("=");
                        //写入缓存
                        DaemonCache.getCacheMap().put(keyValue[0],keyValue[1]);
                    }
                }else{
                    System.out.println("文件已损坏");
                }

            }
        }else{
            logger.info("暂不支持当前环境");
            return false;
        }
    }
}
