package com.example.demo.common.utils;

import com.example.demo.common.constant.LongEnum;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import cn.hutool.core.codec.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Security;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @Author cuilidong
 * @Date 2026/2/13 下午6:02
 */
public class EmptyUtils {

    /**
     * 判断字符串是否为空，长度为0被认为是空字符串.
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        if (null != str) {
            return str.trim().length() == 0;
        } else {
            return true;
        }
    }


    public static boolean isEmptyBigdecimal(BigDecimal decimal) {
        if (!EmptyUtils.isEmpty(decimal)) {
            return decimal.compareTo(BigDecimal.ZERO) == 0;
        }
        return true;
    }

    public static boolean isEmpty(String str, boolean isTrimed) {
        if (isTrimed) {
            return null == str || str.trim().length() == 0;
        } else {
            return null == str || str.length() == 0;
        }
    }

    public static boolean isEmpty(Collection<?> collection) {
        return null == collection || collection.size() == 0;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Object[] array) {
        return null == array || array.length == 0;
    }

    public static boolean isEmpty(Object obj) {
        return null == obj || "".equals(obj);
    }

    public static boolean isLongEmpty(Long obj) {
        return null == obj || LongEnum.ZERO.getCode().equals(obj);
    }

    /**
     * 截取字符串中的数字
     */
    public static String checkNum(String str) {
        StringBuilder builder = new StringBuilder();
        String regEx = "(\\d+(\\.\\d+)?)";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {//当符合正则表达式定义的条件时
            builder.append(m.group());
        }
        return builder.toString();
    }

    /**
     * 截取字符串中除了数字以外的字符串
     */
    public static String checkNumOhter(String str) {
        StringBuilder builder = new StringBuilder();
        String regEx = "[^0-9.]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {//当符合正则表达式定义的条件时
            builder.append(m.group());
        }
        return builder.toString();
    }

    /**
     * 图片地址转MultipartFile
     */
    public static MultipartFile downloadImageUrl(String imageUrl, Long id) {
        try {
            HttpURLConnection httpUrl = (HttpURLConnection) new URL(imageUrl).openConnection();
            httpUrl.connect();
            File file = inputStreamToFile(httpUrl.getInputStream(), "product-" + id + ".png");
            System.out.println("111====>>>>" + file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 工具类
     * inputStream 转 File
     */
    public static File inputStreamToFile(InputStream ins, String name) throws Exception {
        File file = new File("C:\\Users\\EDZ\\Pictures\\其他店铺3\\" + name);
        if (file.exists()) {
            return file;
        }
        OutputStream os = new FileOutputStream(file);
        int bytesRead;
        int len = 8192;
        byte[] buffer = new byte[len];
        while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        //ImageUtil.readImage(file);
        return file;
    }

    /**
     * 拿到字符串数组以逗号拼接成字符串返回
     */
    public static String getImage(List<String> images) {
        String image = "";
        if (!EmptyUtils.isEmpty(images)) {
            for (int i = 0; i < images.size(); i++) {
                if (i == 0 || images.size() == 1) {
                    image = images.get(i);
                } else {
                    image += "," + images.get(i);
                }
            }
        }
        return image;
    }

    /**
     * 拿到字符串数组以逗号拼接成字符串返回
     */
    public static String getString(List<Long> str) {
        String image = "";
        if (!EmptyUtils.isEmpty(str)) {
            for (int i = 0; i < str.size(); i++) {
                if (i == 0 || str.size() == 1) {
                    image = String.valueOf(str.get(i));
                } else {
                    image += "," + str.get(i);
                }
            }
        }
        return image;
    }

    /**
     * 拿到字符串数组以分号拼接成字符串返回
     */
    public static String getFenString(List<String> images) {
        String image = "";
        if (!EmptyUtils.isEmpty(images)) {
            for (int i = 0; i < images.size(); i++) {
                if (i == 0 || images.size() == 1) {
                    image = images.get(i);
                } else {
                    image += ";" + images.get(i);
                }
            }
        }
        return image;
    }


    /**
     * 拿到图片拼接字符串封装成数组对象（以逗号拼接）
     */
    public static List<String> getImages(String image) {
        if (!EmptyUtils.isEmpty(image)) {
            String[] split = image.split(",");
            return Arrays.asList(split);
        }
        return null;
    }

    /**
     * 拿到图片拼接字符串封装成数组对象（以分号拼接）
     */
    public static List<String> getFenStrings(String image) {
        if (!EmptyUtils.isEmpty(image)) {
            String[] split = image.split(";");
            return Arrays.asList(split);
        }
        return null;
    }

    /**
     * @param secretInfo 微信返回的req_info的值
     * @param weChatKey  微信的key
     */
    public static String decryption(String secretInfo, String weChatKey) {
        try {
            //对商户key做md5，得到32位小写key*
            SecretKeySpec key = new SecretKeySpec(DigestUtils.md5DigestAsHex(getContentBytes(weChatKey, "utf-8")).toLowerCase().getBytes(), "AES");
            //创建密码器
            Security.addProvider(new BouncyCastleProvider());
            //用key*对加密串B做AES-256-ECB解密（PKCS7Padding）
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, key);
            //返回解密后的内容
            return new String(cipher.doFinal(Base64.decode(secretInfo)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算切分次数
     */
    public static Integer countStep(Integer size, int input) {


        return (size + input - 1) / input;
    }



    /**
     * 过滤表情包参数
     */
    public static String filterEmoji(String source) {
        if (source != null) {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                source = emojiMatcher.replaceAll("*");
                return source;
            }
            return source;
        }
        return source;
    }
}
