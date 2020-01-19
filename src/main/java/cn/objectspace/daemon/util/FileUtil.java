package cn.objectspace.daemon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileUtil {
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private static BufferedOutputStream bos = null;
    private static BufferedInputStream bis = null;
    public static boolean copyFile(InputStream inStream, String destFilePath){
        File destFile = new File(destFilePath);
        try {
            bis = new BufferedInputStream(inStream);
            bos = new BufferedOutputStream(new FileOutputStream(destFile));
            int len = 0;
            byte[] buffer = new byte[65536];
            while((len = bis.read(buffer)) != -1){
                bos.write(buffer, 0, len);
                bos.flush();
            }
        } catch (IOException e) {
            logger.error("复制dll库异常");
            logger.error("异常信息:{}",e.getMessage());
            return false;
        }finally {
            try {
                if(bis!=null) bis.close();
                if(bos!=null) bos.close();
            } catch (IOException e) {
                logger.error("关闭流异常");
                logger.error("异常信息:{}",e.getMessage());
                return false;
            }
        }
        logger.info(destFilePath+"复制成功");
        return true;
    }
}
