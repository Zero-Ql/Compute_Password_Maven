package org.example;

import org.apache.log4j.Logger;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


public class Main {
    private static final String[] DateEnum = {
            "5084972163", "9801567243", "7286059143", "1850394726", "1462578093", "5042936178", "0145937682", "0964238571", "3497651802", "9125780643", "8634972150",
            "5924673801", "8274053169", "5841792063", "2469385701", "8205349671", "7429516038", "3769458021", "5862370914", "8529364170", "7936082154", "5786241930",
            "0728643951", "9418360257", "5093287146", "5647830192", "3986145207", "0942587136", "4357069128", "0956723814", "1502796384",
    };
    private static final Logger logger = Logger.getLogger(String.valueOf(Main.class));

    public static void main(String[] args) {
        int i = 0;
        var scanner = new Scanner(System.in);
        var file = new File("Password.txt");
        System.out.println("输入你的宽带密码：");
        String password = scanner.next();
        if (file.exists()) {
            if (file.delete())
                logger.info("文件删除成功");
            else
                logger.info("文件不存在");
        }
        while (i <= 31) {
            if (file.exists()) {
                for (i = 1; i < 32; i++) {
                    PasswordFileWrite(i, password, file);
                }
            } else {
                logger.info("文件不存在！正在创建...");
                try {
                    if (file.createNewFile())
                        logger.info("文件创建成功！");
                    else
                        logger.info("文件创建失败");
                } catch (IOException e) {
                    logger.error("未知原因，文件创建失败！");
                }
            }
        }
        logger.info("输出完成！！！" + "\n" +
                "请前往程序所在目录的 Password.txt 文件中查看结果！"
        );
    }

    private static void PasswordFileWrite(int i, String password, File file) {

        logger.info("文件已存在！直接输入结果..." + i);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file, true);
            String s = i + "=" + (RC4Tool.Encrypt(DateEnum[i - 1], password).length() == 32 ?
                    RC4Tool.Encrypt(DateEnum[i - 1], password).substring(8, 24) :
                    RC4Tool.Encrypt(DateEnum[i - 1], password));
            fileOutputStream.write(s.getBytes());
            fileOutputStream.write('\n');
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (i >= 31) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

class RC4Tool {
    private static final int BYTE_SIZE = 'Ā';
    private static final byte[] s = new byte[BYTE_SIZE];

    RC4Tool(byte[] key) {
        final byte[] k = new byte[BYTE_SIZE];
        byte temp;
        int j;
        for (int i = 0; i < BYTE_SIZE; i++) {
            s[i] = (byte) i;
            k[i] = key[(i % key.length)];
        }
        for (int i = j = 0; i < BYTE_SIZE; i++) {
            j = j + s[i] + k[i] & 0xFF;
            temp = s[i];
            s[i] = s[j];
            s[j] = temp;
        }
    }

    public static String Encrypt(String key, String password) throws NoSuchAlgorithmException {
        var message = MessageDigest.getInstance("md5");
        message.update(new RC4Tool(StringToByteArray(key)).password_stream(password.getBytes()));
        return GetMD5ToString(message.digest());
    }

    private static String GetMD5ToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte Byte : bytes) {
            sb.append(String.format("%02x", Byte));
        }
        return sb.toString();
    }

    private static byte[] StringToByteArray(String key) {
        byte[] bytes = new byte[key.length()];
        for (int i = 0; i < key.length(); i++) {
            bytes[i] = (byte) Integer.parseInt(((Character) (key.charAt(i))).toString());
        }
        return bytes;
    }

    private byte[] password_stream(byte[] password) {
        byte[] password_stream = new byte[password.length];
        int i = 0, j = 0, m = 0;
        byte temp;
        while (m < password.length) {
            i = i + 1 & 0xFF;
            j = j + s[i] & 0xFF;
            temp = s[i];
            s[i] = s[j];
            s[j] = temp;
            password_stream[m] = (byte) (s[(s[i] + s[j] & 0xFF)] ^ password[m]);
            m++;
        }
        return password_stream;
    }
}