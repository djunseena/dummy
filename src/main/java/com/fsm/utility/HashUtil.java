package com.fsm.utility;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
//import static org.apache.commons.codec.digest.MessageDigestAlgorithms.*;

/**
 * This class is an utility that simplify usage
 * of frequently used Apache Commons Digest Utility
 */
public class HashUtil {

    //#region Secure 256 Bit Hash
    /**
     * SHA_256 Digest Utility
     */
    public static class SHA_256 {

        public static byte[] digest(String data){
            return DigestUtils.sha256(data);
        }

        public static byte[] digest(byte[] data){
            return DigestUtils.sha256(data);
        }

        public static String digestAsHex(String data){
            return DigestUtils.sha256Hex(data);
        }

        public static String digestAsHex(byte[] data){
            return DigestUtils.sha256Hex(data);
        }

        public static byte[] digestAsBase64(String data){
            return Base64.encodeBase64(digest(data));
        }

        public static byte[] digestAsBase64(byte[] data){
            return Base64.encodeBase64(digest(data));
        }

        public static String digestAsBase64String(String data){
            return Base64.encodeBase64String(digest(data));
        }

        public static String digestAsBase64String(byte[] data){
            return Base64.encodeBase64String(digest(data));
        }

    }
    //#endregion

    //#region Secure 512 Bit Hash
    /**
     * SHA_512 Digest Utility
     */
    public static class SHA_512 {

        public static byte[] digest(String data){
            return DigestUtils.sha512(data);
        }

        public static byte[] digest(byte[] data){
            return DigestUtils.sha512(data);
        }

        public static String digestAsHex(String data){
            return DigestUtils.sha512Hex(data);
        }

        public static String digestAsHex(byte[] data){
            return DigestUtils.sha512Hex(data);
        }

        public static byte[] digestAsBase64(String data){
            return Base64.encodeBase64(digest(data));
        }

        public static byte[] digestAsBase64(byte[] data){
            return Base64.encodeBase64(digest(data));
        }

        public static String digestAsBase64String(String data){
            return Base64.encodeBase64String(digest(data));
        }

        public static String digestAsBase64String(byte[] data){
            return Base64.encodeBase64String(digest(data));
        }

    }
    //#endregion

    //#region Message Digest 5 Hash
    /**
     * MD5 Digest Utility
     */
    public static class MD5 {

        public static byte[] digest(String data){
            return DigestUtils.md5(data);
        }

        public static byte[] digest(byte[] data){
            return DigestUtils.md5(data);
        }

        public static String digestAsHex(String data){
            return DigestUtils.md5Hex(data);
        }

        public static String digestAsHex(byte[] data){
            return DigestUtils.md5Hex(data);
        }

        public static byte[] digestAsBase64(String data){
            return Base64.encodeBase64(digest(data));
        }

        public static byte[] digestAsBase64(byte[] data){
            return Base64.encodeBase64(digest(data));
        }

        public static String digestAsBase64String(String data){
            return Base64.encodeBase64String(digest(data));
        }

        public static String digestAsBase64String(byte[] data){
            return Base64.encodeBase64String(digest(data));
        }

    }
    //#endregion

}