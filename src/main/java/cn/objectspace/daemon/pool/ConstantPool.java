package cn.objectspace.daemon.pool;

/**
* @Description: 运行时常量池
* @Author: NoCortY
* @Date: 2020/3/5
*/
public class ConstantPool {
    /**
     * 上次硬盘读  缓存key
     */
    public static final String LAST_READ_COUNT_KEY = "last_read_count:";

    /**
     * 上次硬盘写  缓存key
     */
    public static final String LAST_WRITE_COUNT_KEY = "last_write_count:";

    /**
     * 心跳时间
     */
    public static final Integer HEART_BEAT_SEC = 30;

    /**
     * 用户id key
     */
    public static final String USER_ID = "userId";

    /**
     * windows环境下套件安装默认位置
     */
    public static final String WINDOWS_INSTALL_PATH="C:\\Users\\NoCortY\\Downloads\\hyperic-sigar-1.6.4\\hyperic-sigar-1.6.4\\sigar-bin\\";
    /**
     * Linux环境下套件安装默认位置
     */
    public static final String LINUX_INSTALL_PATH = "/usr/lib64/";

    /**
     * 配置信息缓存 key
     */
    public static final String OCDAE_CONFIG = "ocdae_config";
}
