package cn.objectspace.daemon.util;

import cn.objectspace.daemon.pojo.dto.CpuDto;
import cn.objectspace.daemon.pojo.dto.DiskDto;
import cn.objectspace.daemon.pojo.dto.NetDto;
import cn.objectspace.daemon.pojo.dto.ServerInfoDto;
import org.hyperic.sigar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

/**
 * @Description: 服务器工具
 * @Author: NoCortY
 * @Date: 2020/1/17
 */
public class ServerUtil {

    private static Logger logger = LoggerFactory.getLogger(ServerUtil.class);

    public static Boolean isInit = false;

    public static ServerInfoDto serverInfoDtoBuilder(){
        ServerInfoDto serverInfoDto = new ServerInfoDto();
        //初始化sigar
        if(!isInit) initSigar(null);
        try {
            putServerProperty(serverInfoDto);
        } catch (UnknownHostException e) {
            logger.error("获取服务器基本信息异常");
            logger.error("异常信息:{}",e.getMessage());
        }
        try {
            memory(serverInfoDto);
            serverInfoDto.setCpuList(cpu());
            serverInfoDto.setDiskList(disk());
            serverInfoDto.setNetList(net());
        } catch (SigarException e) {
            logger.error("获取内存信息异常");
            logger.error("异常信息:{}",e.getMessage());
        }

        return serverInfoDto;
    }
    /**
     * @Description: 初始化Sigar,自动加载sigar的依赖dll库
     * @Param: []
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/1/17
     */
    private static Sigar initSigar(String classPath){
        //分环境加载不同的依赖库
        if(isWin()){
            logger.info("当前环境为windows");
            if(classPath==null||classPath.length()==0)
                classPath = "C:\\Users\\NoCortY\\Downloads\\hyperic-sigar-1.6.4\\hyperic-sigar-1.6.4\\sigar-bin\\";
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
            logger.info("当前环境为linux");
            if(classPath==null||classPath.length()==0) classPath="/usr/lib64/";
            //linux64位依赖的dll
            InputStream inputStream = ServerUtil.class.getResourceAsStream("/sigar/libsigar-amd64-linux.so");
            FileUtil.copyFile(inputStream,classPath+"libsigar-amd64-linux.so");
            logger.info("libsigar-amd64-linux.so安装完毕");
            //linux32位依赖的dll
            inputStream = ServerUtil.class.getResourceAsStream("/sigar/libsigar-x86-linux.so");
            FileUtil.copyFile(inputStream,classPath+"libsigar-x86-linux.so");
            logger.info("libsigar-x86-linux.so安装完毕");
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
    private static void putServerProperty(ServerInfoDto serverInfo) throws UnknownHostException {
        //系统变量
        Properties props = System.getProperties();
        InetAddress addr = InetAddress.getLocalHost();
        OperatingSystem OS = OperatingSystem.getInstance();
        //获取环境变量
        Map<String,String> map = System.getenv();
        String ip = addr.getHostAddress();
        String computerName = map.get("COMPUTERNAME");
        String userDomain = map.get("USERDOMAIN");
        String hostName = addr.getHostName();
        String osName = OS.getName();
        String osArch = OS.getArch();
        String osVersion = OS.getVersion();

        serverInfo.setIp(ip);
        serverInfo.setComputerName(computerName);
        serverInfo.setUserDomain(userDomain);
        serverInfo.setHostName(hostName);
        serverInfo.setOsName(osName);
        serverInfo.setOsArch(osArch);
        serverInfo.setOsVersion(osVersion);
        logger.info("==================================================服务器基本信息==================================================");
        logger.info("IP:{}",ip);
        logger.info("computerName:{}",computerName);
        logger.info("userDomain:{}",userDomain);
        logger.info("主机名:{}",hostName);
        logger.info("操作系统名:{}",osName);
        logger.info("操作系统架构:{}",osArch);
        logger.info("操作系统版本:{}",osVersion);
    }

    /**
     * 内存相关
     * @throws SigarException
     */
    private static void memory(ServerInfoDto serverInfo) throws SigarException {
        Sigar sigar = new Sigar();
        Mem mem = sigar.getMem();
        serverInfo.setMemTotal(mem.getTotal());
        serverInfo.setMemUsed(mem.getUsed());
        serverInfo.setMemFree(mem.getFree());
        serverInfo.setMemUsedPercent(mem.getUsedPercent());
        logger.info("内存总量(Byte):{}",mem.getTotal());
        logger.info("当前内存使用量(Byte):{}",mem.getUsed());
        logger.info("当前内存剩余量(Byte):{}",mem.getFree());
        logger.info("当前内存使用率(自动):{}",mem.getUsedPercent());

        Swap swap = sigar.getSwap();

        serverInfo.setSwapTotal(swap.getTotal());
        serverInfo.setSwapUsed(swap.getUsed());
        serverInfo.setSwapFree(swap.getFree());
        serverInfo.setSwapUsedPercent((double)swap.getUsed()/(double)swap.getTotal());
        logger.info("交换区总量(Byte):{}",swap.getTotal());
        logger.info("交换区使用量(Byte):{}",swap.getUsed());
        logger.info("交换区剩余量(Byte):{}",swap.getFree());
        logger.info("交换区使用率(手动):{}",(double)swap.getUsed()/(double)swap.getTotal()*100);
    }

    private static LinkedList<CpuDto> cpu() throws SigarException {
        LinkedList<CpuDto> cpuList = new LinkedList<>();
        Sigar sigar = new Sigar();
        //CPU基本信息
        CpuInfo[] infos = sigar.getCpuInfoList();
        //CPU动态信息
        CpuPerc[] cpuPercList = null;
        cpuPercList = sigar.getCpuPercList();
        logger.info("==================================================CPU基本信息==================================================");
        for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
            CpuDto cpu = new CpuDto();
            CpuInfo info = infos[i];
            cpu.setFrequency(info.getMhz());
            cpu.setVendor(info.getVendor());
            cpu.setModel(info.getModel());
            cpu.setUserUsed(cpuPercList[i].getUser());
            cpu.setSystemUsed(cpuPercList[i].getSys());
            cpu.setIdle(cpuPercList[i].getIdle());
            cpu.setCombine(cpuPercList[i].getCombined());
            logger.info("第{}块CPU信息:",i+1);
            logger.info("CPU频率(MHz):"+info.getMhz());
            logger.info("CPU生产商:{}",info.getVendor());
            logger.info("CPU型号:{}",info.getModel());
            logger.info("CPU用户使用率:{}",CpuPerc.format(cpuPercList[i].getUser()));
            logger.info("CPU系统使用率:{}",CpuPerc.format(cpuPercList[i].getSys()));
            logger.info("CPU空闲率:{}",CpuPerc.format(cpuPercList[i].getIdle()));
            logger.info("CPU总使用率:{}\n",CpuPerc.format(cpuPercList[i].getCombined()));
            cpuList.add(cpu);
        }
        return cpuList;
    }

    private static LinkedList<DiskDto> disk() throws SigarException {
        Sigar sigar = new Sigar();
        LinkedList<DiskDto> diskList = new LinkedList<>();
        FileSystem fslist[] = sigar.getFileSystemList();
        logger.info("==================================================盘符基本信息==================================================");
        for (int i = 0; i < fslist.length; i++) {
            DiskDto disk = new DiskDto();
            FileSystem fs = fslist[i];
            FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
            if(usage.getTotal()==0L) continue;
            // 分区的盘符名称
            disk.setDiskName(fs.getDevName());
            disk.setDisType(fs.getSysTypeName());
            disk.setTotal(usage.getTotal());
            disk.setFree(usage.getFree());
            disk.setAvail(usage.getAvail());
            disk.setUsed(usage.getUsed());
            disk.setUsePercent(usage.getUsePercent() * 100D);
            disk.setRead(usage.getDiskReads());
            disk.setWrite(usage.getDiskWrites());
            logger.info("盘符名称:{}", fs.getDevName());
            logger.info("盘符路径:{}", fs.getDirName());
            logger.info("盘符类型:{}", fs.getSysTypeName());
            logger.info("硬盘空间(KB):{}", usage.getTotal());
            logger.info("剩余空间(KB):{}", usage.getFree());
            logger.info("可用大小(KB):{}", usage.getAvail());
            logger.info("已经使用量(KB):{}", usage.getUsed());
            logger.info("使用率:{}", usage.getUsePercent() * 100D);
            logger.info("当前Read:{}", usage.getDiskReads());
            logger.info("当前Write:{}\n", usage.getDiskWrites());
            diskList.add(disk);
        }
        return diskList;
    }
    /**
     * @Description: 网络信息
     * @Param: []
     * @return: java.util.LinkedList<cn.objectspace.daemon.pojo.dto.NetDto>
     * @Author: NoCortY
     * @Date: 2020/1/19
     */
    private static LinkedList<NetDto> net() throws SigarException {
        LinkedList<NetDto> netList = new LinkedList<>();
        Sigar sigar = new Sigar();
        String ifNames[] = sigar.getNetInterfaceList();
        logger.info("==================================================网络基本信息==================================================");
        for (int i = 0; i < ifNames.length; i++) {
            NetDto netDto = new NetDto();
            String name = ifNames[i];
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            //过滤无用信息
            if("0.0.0.0".equals(ifconfig.getAddress())) continue;
            NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
            netDto.setNetName(name);
            netDto.setIp(ifconfig.getAddress());
            netDto.setNetMask(ifconfig.getNetmask());
            netDto.setRxPackets(ifstat.getRxPackets());
            netDto.setTxPackets(ifstat.getTxPackets());
            netDto.setRxBytes(ifstat.getRxBytes());
            netDto.setTxBytes(ifstat.getTxBytes());
            netDto.setRxErrors(ifstat.getRxErrors());
            netDto.setTxErrors(ifstat.getTxErrors());
            netDto.setRxDropped(ifstat.getRxDropped());
            netDto.setTxDropped(ifstat.getTxDropped());
            logger.info("网络设备名:{}",name);
            logger.info("IP:{}",ifconfig.getAddress());
            logger.info("子网掩码:{}",ifconfig.getNetmask());
            logger.info("接包数:{}",ifstat.getRxPackets());
            logger.info("发包数:{}",ifstat.getTxPackets());
            logger.info("接收到的总字节数:{}",ifstat.getRxBytes());
            logger.info("发送的总字节数:{}",ifstat.getTxBytes());
            logger.info("接收的错误包数:{}",ifstat.getRxErrors());
            logger.info("发送的错误包数:{}",ifstat.getTxErrors());
            logger.info("接收时丢弃的包数:{}",ifstat.getRxDropped());
            logger.info("发送时丢弃的包数:{}\n",ifstat.getTxDropped());
            if ((ifconfig.getFlags() & 1L) <= 0L) {
                System.out.println("!IFF_UP...skipping getNetInterfaceStat");
                continue;
            }
            netList.add(netDto);
        }
        logger.info("==============================================================================================================");
        return netList;
    }
    /**
     * @Description: 判断是否为windows系统
     * @Param: []
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/1/17
     */
    private static boolean isWin(){
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            return true;
        } else return false;
    }
   /* public static void main(String[] args) throws UnknownHostException, SigarException{
        System.out.println(System.getProperty("java.library.path"));
        ServerInfoDto serverInfoDto = ServerUtil.serverInfoDtoBuilder();
        System.out.println(serverInfoDto);
    }*/
}

