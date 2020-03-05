package cn.objectspace.daemon.core;

import cn.objectspace.daemon.cache.DaemonCache;
import cn.objectspace.daemon.pojo.dto.CpuDto;
import cn.objectspace.daemon.pojo.dto.DiskDto;
import cn.objectspace.daemon.pojo.dto.NetDto;
import cn.objectspace.daemon.pojo.dto.ServerInfoDto;
import cn.objectspace.daemon.pool.ConstantPool;
import org.hyperic.sigar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

/**
* @Description: 服务器信息
* @Author: NoCortY
* @Date: 2020/3/5
*/
public class ServerCore {
    private static Sigar sigar = new Sigar();
    private static Logger logger = LoggerFactory.getLogger(ServerCore.class);

    /**
     * @Description: 生成一个服务器信息对象
     * @Param: []
     * @return: cn.objectspace.daemon.pojo.dto.ServerInfoDto
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    public static ServerInfoDto serverInfoDtoBuilder(){
        ServerInfoDto serverInfoDto = new ServerInfoDto();
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
            serverInfoDto.setServerUser(Integer.valueOf((String) DaemonCache.getCacheMap().get(ConstantPool.USER_ID)));
        } catch (SigarException e) {
            logger.error("获取服务器组件信息出现异常");
            logger.error("异常信息:{}",e.getMessage());
        }

        return serverInfoDto;
    }


    /**
     * @Description: 系统基本信息
     * @Param: [serverInfo]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    private static void putServerProperty(ServerInfoDto serverInfo) throws UnknownHostException {

        //系统变量
        Properties props = System.getProperties();
        InetAddress addr = InetAddress.getLocalHost();
        OperatingSystem OS = OperatingSystem.getInstance();
        //获取环境变量
        Map<String,String> map = System.getenv();

        serverInfo.setIp(addr.getHostAddress());
        serverInfo.setComputerName(map.get("COMPUTERNAME"));
        serverInfo.setUserDomain(map.get("USERDOMAIN"));
        serverInfo.setHostName(addr.getHostName());
        serverInfo.setOsName(OS.getName());
        serverInfo.setOsArch(OS.getArch());
        serverInfo.setOsVersion(OS.getVersion());
        /*logger.info("==================================================服务器基本信息==================================================");
        logger.info("IP:{}",ip);
        logger.info("computerName:{}",computerName);
        logger.info("userDomain:{}",userDomain);
        logger.info("主机名:{}",hostName);
        logger.info("操作系统名:{}",osName);
        logger.info("操作系统架构:{}",osArch);
        logger.info("操作系统版本:{}",osVersion);*/
    }

    /**
     * @Description: 内存信息
     * @Param: [serverInfo]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    private static void memory(ServerInfoDto serverInfo) throws SigarException {
        Mem mem = sigar.getMem();
        serverInfo.setMemTotal(mem.getTotal());
        serverInfo.setMemUsed(mem.getUsed());
        serverInfo.setMemFree(mem.getFree());
        serverInfo.setMemUsedPercent(mem.getUsedPercent());
       /* logger.info("内存总量(Byte):{}",mem.getTotal());
        logger.info("当前内存使用量(Byte):{}",mem.getUsed());
        logger.info("当前内存剩余量(Byte):{}",mem.getFree());
        logger.info("当前内存使用率(自动):{}",mem.getUsedPercent());*/

       //虚拟内存
        Swap swap = sigar.getSwap();

        serverInfo.setSwapTotal(swap.getTotal());
        serverInfo.setSwapUsed(swap.getUsed());
        serverInfo.setSwapFree(swap.getFree());
        serverInfo.setSwapUsedPercent((double)swap.getUsed()/(double)swap.getTotal());
        /*logger.info("交换区总量(Byte):{}",swap.getTotal());
        logger.info("交换区使用量(Byte):{}",swap.getUsed());
        logger.info("交换区剩余量(Byte):{}",swap.getFree());
        logger.info("交换区使用率(手动):{}",(double)swap.getUsed()/(double)swap.getTotal()*100);*/
    }

    /**
     * @Description: Cpu信息
     * @Param: []
     * @return: java.util.LinkedList<cn.objectspace.daemon.pojo.dto.CpuDto>
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    private static LinkedList<CpuDto> cpu() throws SigarException {
        LinkedList<CpuDto> cpuList = new LinkedList<>();
        //CPU基本信息
        CpuInfo[] infos = sigar.getCpuInfoList();
        //CPU动态信息
        CpuPerc[] cpuPercList = sigar.getCpuPercList();
        //logger.info("==================================================CPU基本信息==================================================");
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
            /*logger.info("第{}块CPU信息:",i+1);
            logger.info("CPU频率(MHz):"+info.getMhz());
            logger.info("CPU生产商:{}",info.getVendor());
            logger.info("CPU型号:{}",info.getModel());
            logger.info("CPU用户使用率:{}",CpuPerc.format(cpuPercList[i].getUser()));
            logger.info("CPU系统使用率:{}",CpuPerc.format(cpuPercList[i].getSys()));
            logger.info("CPU空闲率:{}",CpuPerc.format(cpuPercList[i].getIdle()));
            logger.info("CPU总使用率:{}\n",CpuPerc.format(cpuPercList[i].getCombined()));*/
            cpuList.add(cpu);
        }
        return cpuList;
    }

    /**
     * @Description: 硬盘信息
     * @Param: []
     * @return: java.util.LinkedList<cn.objectspace.daemon.pojo.dto.DiskDto>
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    private static LinkedList<DiskDto> disk() throws SigarException {
        LinkedList<DiskDto> diskList = new LinkedList<>();
        FileSystem[] fslist = sigar.getFileSystemList();
        //logger.info("==================================================盘符基本信息==================================================");
        for (FileSystem fs:fslist) {
            DiskDto disk = new DiskDto();
            FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
            if(usage.getTotal()==0L) continue;
            // 分区的盘符名称
            disk.setDiskName(fs.getDevName());
            disk.setDiskType(fs.getSysTypeName());
            disk.setTotal(usage.getTotal());
            disk.setFree(usage.getFree());
            disk.setAvail(usage.getAvail());
            disk.setUsed(usage.getUsed());
            disk.setUsePercent(usage.getUsePercent() * 100D);
            //获取读写信息时，先去缓存中拿上次的信息，然后计算出一个读写速率，再发送
            //如果获取不到上次的，说明程序挂过，或者是第一次启动，那么读设置为0.
            Long lastReadCount = DaemonCache.getRwCache().get(ConstantPool.LAST_READ_COUNT_KEY+disk.getDiskName());
            Double readRate = 0.0;
            if(lastReadCount==null) disk.setReadRate(readRate);
            else {
                readRate = getDiskRWRate(lastReadCount, usage.getDiskReads(), ConstantPool.HEART_BEAT_SEC);
                disk.setReadRate(readRate);
            }
            //如果获取不到上次的，说明程序挂过，或者是第一次启动，那么写设置为0.
            Long lastWriteCount = DaemonCache.getRwCache().get(ConstantPool.LAST_WRITE_COUNT_KEY+disk.getDiskName());
            Double writeRate = 0.0;
            if(lastWriteCount==null) disk.setWriteRate(writeRate);
            else {
                writeRate = getDiskRWRate(lastWriteCount, usage.getDiskWrites(), ConstantPool.HEART_BEAT_SEC);
                disk.setWriteRate(writeRate);
            }
            //读写信息写入缓存
            DaemonCache.getRwCache().put(ConstantPool.LAST_WRITE_COUNT_KEY+disk.getDiskName(),usage.getDiskWrites());
            DaemonCache.getRwCache().put(ConstantPool.LAST_READ_COUNT_KEY+disk.getDiskName(),usage.getDiskReads());
            disk.setReadDisk(usage.getDiskReads());
            disk.setWriteDisk(usage.getDiskWrites());
            /*logger.info("盘符名称:{}", fs.getDevName());
            logger.info("盘符路径:{}", fs.getDirName());
            logger.info("盘符类型:{}", fs.getSysTypeName());
            logger.info("硬盘空间(KB):{}", usage.getTotal());
            logger.info("剩余空间(KB):{}", usage.getFree());
            logger.info("可用大小(KB):{}", usage.getAvail());
            logger.info("已经使用量(KB):{}", usage.getUsed());
            logger.info("使用率:{}", usage.getUsePercent() * 100D);
            logger.info("当前Read:{}", usage.getDiskReads());
            logger.info("当前Write:{}", usage.getDiskWrites());
            logger.info("当前ReadRate(B/s):{}",readRate);
            logger.info("当前WriteRate(B/s):{}",writeRate);*/
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
        //获取网卡名数组
        String[] ifNames = sigar.getNetInterfaceList();
        //logger.info("==================================================网络基本信息==================================================");
        for (String name:ifNames) {
            NetDto netDto = new NetDto();
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            //过滤无用信息
            if("0.0.0.0".equals(ifconfig.getAddress())) continue;
            NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
            netDto.setNetName(name);
            netDto.setNetIp(ifconfig.getAddress());
            netDto.setNetMask(ifconfig.getNetmask());
            netDto.setRxPackets(ifstat.getRxPackets());
            netDto.setTxPackets(ifstat.getTxPackets());
            netDto.setRxBytes(ifstat.getRxBytes());
            netDto.setTxBytes(ifstat.getTxBytes());
            netDto.setRxErrors(ifstat.getRxErrors());
            netDto.setTxErrors(ifstat.getTxErrors());
            netDto.setRxDropped(ifstat.getRxDropped());
            netDto.setTxDropped(ifstat.getTxDropped());
            /*logger.info("网络设备名:{}",name);
            logger.info("IP:{}",ifconfig.getAddress());
            logger.info("子网掩码:{}",ifconfig.getNetmask());
            logger.info("接包数:{}",ifstat.getRxPackets());
            logger.info("发包数:{}",ifstat.getTxPackets());
            logger.info("接收到的总字节数:{}",ifstat.getRxBytes());
            logger.info("发送的总字节数:{}",ifstat.getTxBytes());
            logger.info("接收的错误包数:{}",ifstat.getRxErrors());
            logger.info("发送的错误包数:{}",ifstat.getTxErrors());
            logger.info("接收时丢弃的包数:{}",ifstat.getRxDropped());
            logger.info("发送时丢弃的包数:{}\n",ifstat.getTxDropped());*/
            if ((ifconfig.getFlags() & 1L) <= 0L) {
                System.out.println("!IFF_UP...skipping getNetInterfaceStat");
                continue;
            }
            netList.add(netDto);
        }
        //logger.info("==============================================================================================================");
        return netList;
    }
    /**
     * @Description: 获取硬盘读写速率
     * @Param: [last, current, second]
     * @return: java.lang.Double
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    private static Double getDiskRWRate(long last, long current, int second){
        Double lastCount = (double) last;
        Double currentCount = (double) current;
        //单位：MB
        return (currentCount-lastCount)/second;
    }
}
