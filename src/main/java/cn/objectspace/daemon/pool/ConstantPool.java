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
}
