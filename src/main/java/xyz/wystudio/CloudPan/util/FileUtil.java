package xyz.wystudio.CloudPan.util;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FileUtil {
    public static String getFileMD5(String filePath) {
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(filePath)) {
                byte[] dataBytes = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, bytesRead);
                }
            }

            byte[] mdBytes = md.digest();
            BigInteger bigInt = new BigInteger(1, mdBytes);
            return bigInt.toString(16);
        } catch (Exception e) {
            return null;
        }
    }
}
