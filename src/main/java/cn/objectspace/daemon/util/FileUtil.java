package cn.objectspace.daemon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileUtil {
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	public static String readFileAsString(String destFilePath){
	    StringBuilder sb = new StringBuilder();
	    FileReader fileReader = null;
        try {
            fileReader = new FileReader(destFilePath);
            char[] buf = new char[1024];
            int num = 0;
            while((num = fileReader.read())!=-1){
                sb.append(new String(buf,0,num));
            }
        } catch (IOException e) {
            logger.error("读取文件异常");
            logger.error("异常信息:{}",e.getMessage());
            return null;
        }finally {
            if(fileReader!=null){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    logger.error("字符流关闭异常");
                    logger.error("异常信息:{}",e.getMessage());
                }
            }
        }
        return sb.toString();
    }
	public static boolean writeFileAsString(String content,String destFilePath) {
        FileWriter fileWriter = null;
        try {
             fileWriter = new FileWriter(destFilePath);
             fileWriter.write(content);
        } catch (IOException e) {
            logger.error("文件写入异常");
            logger.error("异常信息:{}",e.getMessage());
            return false;
        }finally {
            if(fileWriter!=null){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    logger.error("字符流关闭异常");
                    logger.error("异常信息:{}",e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean copyFile(InputStream inStream, String destFilePath){
        File destFile = new File(destFilePath);

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
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
