package com.xdream.wisdom.util;

import com.xdream.wisdom.util.encryption.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class FilesUtils {

    /**
     * 生成Byte流 TODO
     *
     * @param
     * @return
     * @throws
     * @history
     * @knownBugs
     */
    public static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 把流生成图片 TODO
     *
     * @param
     * @return
     * @throws
     * @history
     * @knownBugs
     */
    public static File getFileFromBytes(byte[] files, String outputFile, String fileName) {
        File ret = null;

        BufferedOutputStream stream = null;
        try {
            if (StringUtils.isBlank(fileName)) {
                ret = new File(outputFile);
            } else {
                ret = new File(outputFile + fileName);
            }


            File fileParent = ret.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            ret.createNewFile();

            FileOutputStream fstream = new FileOutputStream(ret);

            stream = new BufferedOutputStream(fstream);

            stream.write(files);


        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // log.error("helper:get file from byte process error!");
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /***
     * 根据路径获取
     *
     * @param path
     * @return
     */
    public static byte[] getPhoto(String path) {
        byte[] data = null;
        FileImageInputStream input = null;
        try {
            input = new FileImageInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }

    /**
     * 将图片压缩
     *
     * @param srcImgData
     * @param maxSize    10240L （10KB）
     * @return
     * @throws Exception
     */

    public static byte[] compressUnderSize(byte[] srcImgData, long maxSize)
            throws Exception {
        double scale = 0.9;
        byte[] imgData = Arrays.copyOf(srcImgData, srcImgData.length);

        if (imgData.length > maxSize) {
            do {
                try {
                    imgData = compress(imgData, scale);

                } catch (IOException e) {
                    throw new IllegalStateException("压缩图片过程中出错，请及时联系管理员！", e);
                }

            } while (imgData.length > maxSize);
        }

        return imgData;
    }

    public static byte[] compress(byte[] srcImgData, double scale)
            throws IOException {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(srcImgData));
        int width = (int) (bi.getWidth() * scale); // 源图宽度
        int height = (int) (bi.getHeight() * scale); // 源图高度

        Image image = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage tag = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

        Graphics g = tag.getGraphics();
        g.setColor(Color.RED);
        g.drawImage(image, 0, 0, null); // 绘制处理后的图
        g.dispose();

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ImageIO.write(tag, "JPEG", bOut);

        return bOut.toByteArray();
    }


    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
//	byte []aBytes=	getPhoto("E://测试.jpg");
        try {
            String a = "eyJlcnJjb2RlIjo0MDAwMSwiZXJybXNnIjoiaW52YWxpZCBjcmVkZW50aWFsLCBhY2Nlc3NfdG9rZW4gaXMgaW52YWxpZCBvciBub3QgbGF0ZXN0IGhpbnQ6IFtRR2owMzg3dnI0OSFdIn0=";
            String b = Base64.encode(FilesUtils.compressUnderSize(Base64.decode(a), 10240L));
            if (a.equals(b)) {
                System.out.println("===");
            } else {
                System.out.println("!=");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
