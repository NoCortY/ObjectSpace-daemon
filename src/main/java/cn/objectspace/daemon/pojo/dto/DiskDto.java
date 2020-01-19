package cn.objectspace.daemon.pojo.dto;

import java.io.Serializable;

/**
* @Description: 硬盘信息Dto
* @Author: NoCortY
* @Date: 2020/1/19
*/
public class DiskDto implements Serializable {
    private static final long serialVersionUID = -333899734218316019L;
    //盘符名称
    private String diskName;
    //盘符类型
    private String disType;
    //总大小
    private Long total;
    //剩余大小
    private Long free;
    //可用大小
    private  Long avail;
    //已经使用量
    private Long used;
    //使用率
    private Double usePercent;
    //读写
    private Long read;
    private Long write;

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getDisType() {
        return disType;
    }

    public void setDisType(String disType) {
        this.disType = disType;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }

    public Long getAvail() {
        return avail;
    }

    public void setAvail(Long avail) {
        this.avail = avail;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Double getUsePercent() {
        return usePercent;
    }

    public void setUsePercent(Double usePercent) {
        this.usePercent = usePercent;
    }

    public Long getRead() {
        return read;
    }

    public void setRead(Long read) {
        this.read = read;
    }

    public Long getWrite() {
        return write;
    }

    public void setWrite(Long write) {
        this.write = write;
    }

    @Override
    public String toString() {
        return "DiskDto{" +
                "diskName='" + diskName + '\'' +
                ", disType='" + disType + '\'' +
                ", total=" + total +
                ", free=" + free +
                ", avail=" + avail +
                ", used=" + used +
                ", usePercent=" + usePercent +
                ", read=" + read +
                ", write=" + write +
                '}';
    }
}
