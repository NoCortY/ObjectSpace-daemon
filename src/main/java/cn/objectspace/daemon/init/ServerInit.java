package cn.objectspace.daemon.init;

import cn.objectspace.daemon.pool.ConstantPool;
import cn.objectspace.daemon.util.FileUtil;
import cn.objectspace.daemon.util.OSCheck;
import io.netty.util.internal.StringUtil;
import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
* @Description: 初始化服务器相关配置
* @Author: NoCortY
* @Date: 2020/3/5
*/
public class ServerInit implements ObjectInit, Serializable {
    private static final long serialVersionUID = -7971056687391866939L;
    private static Logger logger = LoggerFactory.getLogger(ServerInit.class);
    @Override
    public boolean init(String[] args) {
        logger.info("初始化服务器管理套件...");
        //初始化
        return initSigar(null);
    }


    /**
     * @Description: 初始化Sigar
     * @Param: [classPath]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    private boolean initSigar(String classPath) {

        if(OSCheck.isWin()){
            logger.info("当前环境:windows");
            return winSigarInit(classPath);
        }else if(OSCheck.isLinux()){
            logger.info("当前环境:linux");
            return linuxSigarInit(classPath);
        }else{
            logger.info("暂不支持当前环境");
            return false;
        }
    }
    /**
     * @Description: Windows环境下初始化
     * @Param: [classPath]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    private boolean winSigarInit(String classPath){
        if(StringUtil.isNullOrEmpty(classPath)) classPath = ConstantPool.WINDOWS_INSTALL_PATH;
        try {
            //加载window依赖的三个dll
            InputStream inStream = ServerInit.class.getResourceAsStream("/sigar/sigar-amd64-winnt.dll");
            FileUtil.copyFile(inStream,classPath+"sigar-amd64-winnt.dll");
            inStream = ServerInit.class.getResourceAsStream("/sigar/sigar-x86-winnt.dll");
            FileUtil.copyFile(inStream,classPath+"sigar-x86-winnt.dll");
            inStream = ServerInit.class.getResourceAsStream("/sigar/sigar-x86-winnt.lib");
            FileUtil.copyFile(inStream,classPath+"sigar-x86-winnt.lib");
        } catch (IOException e) {
            logger.error("初始化服务器管理套件失败");
            logger.error("异常信息:{}",e.getMessage());
            return false;
        }
        //获取系统变量
        String path = System.getProperty("java.library.path");
        path += ";" + classPath;
        logger.info("服务器管理套件环境变量:{}",path);
        System.setProperty("java.library.path", path);
        return true;
    }
    /**
     * @Description: Linux环境下初始化
     * @Param: [classPath]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    private boolean linuxSigarInit(String classPath){
        logger.info("当前环境为linux");
        if(StringUtil.isNullOrEmpty(classPath)) classPath=ConstantPool.LINUX_INSTALL_PATH;
        try {
            //linux64位依赖的dll
            InputStream inputStream = ServerInit.class.getResourceAsStream("/sigar/libsigar-amd64-linux.so");
            FileUtil.copyFile(inputStream,classPath+"libsigar-amd64-linux.so");
            logger.info("libsigar-amd64-linux.so安装完毕");
            //linux32位依赖的dll
            inputStream = ServerInit.class.getResourceAsStream("/sigar/libsigar-x86-linux.so");
            FileUtil.copyFile(inputStream,classPath+"libsigar-x86-linux.so");
            logger.info("libsigar-x86-linux.so安装完毕");
        } catch (IOException e) {
            logger.error("初始化服务器管理套件失败");
            logger.error("异常信息:{}",e.getMessage());
            return false;
        }
        String path = System.getProperty("java.library.path");
        path += ":" + classPath;
        System.setProperty("java.library.path", path);
        logger.info("服务器管理套件环境变量:{}",path);
        return true;
    }
}
