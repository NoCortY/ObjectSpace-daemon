package cn.objectspace.daemon.init;

import cn.objectspace.daemon.cache.DaemonCache;
import cn.objectspace.daemon.pojo.entity.OcdaePO;
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
        //判断环境
        if(OSCheck.isWin()) file = new File(ConstantPool.WINDOWS_INSTALL_PATH+"ocdae.os");
        else if(OSCheck.isLinux()) file = new File(ConstantPool.LINUX_INSTALL_PATH+"ocdae.os");
        else{
            logger.info("暂不支持当前环境");
            return false;
        }
        if(!file.exists()){
            //如果文件不存在
            try {
                file.createNewFile();
                System.out.println("守护进程第一次启动...写入用户id和心跳地址");
                OcdaePO ocdaePO = new OcdaePO();
                //写入缓存
                ocdaePO.setUserId(Integer.valueOf(args[0]));
                ocdaePO.setPingUrl(args[1]);
                DaemonCache.getCoreMap().put(ConstantPool.OCDAE_CONFIG,ocdaePO);
                //写入文件
                FileUtil.writeFileAsBinary(ocdaePO,file.getAbsolutePath());
            } catch (IOException e) {
                logger.error("文件写入失败");
                return false;
            }
        }else{
            //如果文件已经存在，那么直接从文件中读，放入缓存中
            try {
                OcdaePO ocdaePO = (OcdaePO) FileUtil.readFileAsBinary(file.getAbsolutePath());
                DaemonCache.getCoreMap().put(ConstantPool.OCDAE_CONFIG,ocdaePO);
            } catch (IOException | ClassNotFoundException e) {
                logger.error("读取配置文件失败");
                logger.error("异常信息:{}",e.getMessage());
                return false;
            }
        }
        return true;
    }
}
