package com.dyl.fruitstock.utils;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;

@Slf4j
public class EncryptHelpUtils {

    public static String hmacHex(String key, String sb) {
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, StringUtils.getBytesUtf8(key));
        String sign = Hex.encodeHexString(mac.doFinal(StringUtils.getBytesUtf8(sb)));
        return sign;
    }

    /**
     * AES 加密
     *
     * @param aesKey
     * @param data
     * @return
     */
    public static String aesEncryptStr(String aesKey, String data) {
        try {
            return SecureUtil.aes(aesKey.getBytes()).encryptBase64(data);
        } catch (Exception e) {
            log.info("AES 加密失败:", e);
        }
        return data;
    }

    /**
     * AES 解密
     *
     * @param aesKey
     * @param data
     * @return
     */
    public static String aesDecryptStr(String aesKey, String data) {
        try {
            return SecureUtil.aes(aesKey.getBytes()).decryptStr(data);
        } catch (Exception e) {
            log.info("AES 解密失败:", e);
        }
        return data;
    }

    /**
     * DES 加密
     *
     * @param desKey
     * @param data
     * @return
     */
    public static String desEncryptStr(String desKey, String data) {
        try {
            return SecureUtil.des(desKey.getBytes()).encryptBase64(data);
        } catch (Exception e) {
            log.info("DES 加密失败:", e);
        }
        return data;
    }

    /**
     * DES 解密
     *
     * @param desKey
     * @param data
     * @return
     */
    public static String desDecryptStr(String desKey, String data) {
        try {
            return SecureUtil.des(desKey.getBytes()).decryptStr(data);
        } catch (Exception e) {
            log.info("DES 解密失败:", e);
        }
        return data;
    }

    public static void main(String[] args) {
        String content = "123456";
        //随机生成密钥
        String key = "ZW6wAcLaa9LujHAS";
        //AES 加密
        String encryptStr = aesEncryptStr(key, content);
        //AES 解密
        String decryptStr = aesDecryptStr(key, encryptStr);
        System.out.println(encryptStr);
        System.out.println(decryptStr);

        //DES 加密
        String encryptStr1 = desEncryptStr(key, content);
        //DES 解密
        String decryptStr1 = desDecryptStr(key, encryptStr1);
        System.out.println(encryptStr1);
        System.out.println(decryptStr1);
    }
}