package cn.objectspace.daemon.util;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;

/**
* @Description: 服务器工具
* @Author: NoCortY
* @Date: 2020/1/17
*/
public class ServerUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ServerUtil.class);
    /**
     * @Description: 初始化Sigar,自动加载sigar的依赖dll库
     * @Param: []
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/1/17
     */
    public static Sigar initSigar(String classPath){
    	//分环境加载不同的依赖库
        if(isWin()){
            if(classPath==null||classPath.length()==0)
                classPath = "C:\\Object\\Temp\\";
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
     * 获取服务器基本信息
     * @throws UnknownHostException 
     */
    public static void property() throws UnknownHostException {
    	Runtime r = Runtime.getRuntime();
    	//系统变量
    	Properties props = System.getProperties();
    	InetAddress addr = InetAddress.getLocalHost();
    	//获取ip
    	String ip = addr.getHostAddress();
    	//获取环境变量
    	Map<String,String> map = System.getenv();
    	//用户名
    	String username = map.get("USERNAME");
    	//主机名
    	String computerName = map.get("COMPUTERNAME");
    	//计算机域名
    	String userDomain = map.get("USERDOMAIN");
    	logger.info("IP:{}",ip);
    	logger.info("username:{}",username);
    	logger.info("computerName:{}",computerName);
    	logger.info("userDomain:{}",userDomain);
    }
    
    /**
     * 内存相关
     * @throws SigarException
     */
    private static void memory() throws SigarException {
        Sigar sigar = new Sigar();
        Mem mem = sigar.getMem();
        // 内存总量
        System.out.println("内存总量:    " + mem.getTotal() / (1024L*1024L) + "M av");
        // 当前内存使用量
        System.out.println("当前内存使用量:    " + mem.getUsed() / (1024L*1024L) + "M used");
        // 当前内存剩余量
        System.out.println("当前内存剩余量:    " + mem.getFree() / (1024L*1024L) + "M free");
        Swap swap = sigar.getSwap();
        // 交换区总量
        System.out.println("交换区总量:    " + swap.getTotal() / (1024L*1024L) + "M av");
        // 当前交换区使用量
        System.out.println("当前交换区使用量:    " + swap.getUsed() / (1024L*1024L) + "M used");
        // 当前交换区剩余量
        System.out.println("当前交换区剩余量:    " + swap.getFree() / (1024L*1024L) + "M free");
    }
    
    private static void cpu() throws SigarException {
        Sigar sigar = new Sigar();
        CpuInfo infos[] = sigar.getCpuInfoList();
        CpuPerc cpuList[] = null;
        cpuList = sigar.getCpuPercList();
        for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
            CpuInfo info = infos[i];
            System.out.println("第" + (i + 1) + "块CPU信息");
            System.out.println("CPU的总量MHz:    " + info.getMhz());// CPU的总量MHz
            System.out.println("CPU生产商:    " + info.getVendor());// 获得CPU的卖主，如：Intel
            System.out.println("CPU类别:    " + info.getModel());// 获得CPU的类别，如：Celeron
            System.out.println("CPU缓存数量:    " + info.getCacheSize());// 缓冲存储器数量
            printCpuPerc(cpuList[i]);
        }
    }
    private static void printCpuPerc(CpuPerc cpu) {
        System.out.println("CPU用户使用率:    " + CpuPerc.format(cpu.getUser()));// 用户使用率
        System.out.println("CPU系统使用率:    " + CpuPerc.format(cpu.getSys()));// 系统使用率
        System.out.println("CPU当前等待率:    " + CpuPerc.format(cpu.getWait()));// 当前等待率
        System.out.println("CPU当前错误率:    " + CpuPerc.format(cpu.getNice()));//
        System.out.println("CPU当前空闲率:    " + CpuPerc.format(cpu.getIdle()));// 当前空闲率
        System.out.println("CPU总的使用率:    " + CpuPerc.format(cpu.getCombined()));// 总的使用率
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
    public static void main(String[] args) throws UnknownHostException, SigarException{
    	logger.info("123");
        ServerUtil.initSigar(null);
        ServerUtil.property();
        ServerUtil.memory();
        ServerUtil.cpu();
    }
}

