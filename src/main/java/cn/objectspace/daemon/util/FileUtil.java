package cn.objectspace.daemon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @Description: 文件工具类
 * @Author: NoCortY
 * @Date: 2020/3/5
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    /**
     * @Description: 从文件中读入二进制数据
     * @Param: [filePath]
     * @return: java.lang.Object
     * @Author: NoCortY
     * @Date: 2020/3/6
     */
    public static Object readFileAsBinary(String filePath) throws IOException, ClassNotFoundException {

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath));

        return objectInputStream.readObject();

    }
    /**
     * @Description: 将任意格式内容输出到文件
     * @Param: [object, destFilePath]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/6
     */
    public static void writeFileAsBinary(Object object,String destFilePath) throws IOException {
        if(object==null) return;

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(destFilePath));

        objectOutputStream.writeObject(object);

        objectOutputStream.flush();

        objectOutputStream.close();
    }
    /**
     * @Description: 将文件内容生成一个字符串（字符流方式）
     * @Param: [destFilePath]
     * @return: java.lang.String
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    public static String readFileAsString(String destFilePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = new FileReader(destFilePath);
        char[] buf = new char[1024];
        int num = 0;
        while ((num = fileReader.read(buf)) != -1) {
            sb.append(new String(buf, 0, num));
        }

        fileReader.close();

        return sb.toString();
    }

    /**
     * @Description: 写一段内容到一个文件中（字符流方式）
     * @Param: [content, destFilePath]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    public static void writeFileAsString(String content, String destFilePath) throws IOException {
        FileWriter fileWriter = fileWriter = new FileWriter(destFilePath);

        fileWriter.write(content);

        fileWriter.close();
    }

    /**
     * @Description: 拷贝文件
     * @Param: [inStream文件流, destFilePath目标文件路径]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/3/5
     */
    public static void copyFile(InputStream inStream, String destFilePath) throws IOException {
        File destFile = new File(destFilePath);
        BufferedInputStream bis = new BufferedInputStream(inStream);
        ;
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
        int len = 0;
        byte[] buffer = new byte[65536];
        while ((len = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
            bos.flush();
        }

        bis.close();
        bos.close();
    }
}
