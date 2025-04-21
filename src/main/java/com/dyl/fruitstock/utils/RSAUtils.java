package com.dyl.fruitstock.utils;
import com.dyl.fruitstock.exception.BusinessException;
import org.apache.commons.lang3.ObjectUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class RSAUtils {

    // 默认密钥长度 (推荐2048位以上)
    private static final int DEFAULT_KEY_SIZE = 2048;
    // 默认加密填充模式
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    // 签名算法
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";


    public static final String SAAS_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiEh8YE7pL8bzixssPGfwpifHmRlJZMiIu01b1znQLnkIL+NTd6cu2KSWRLRykZiTPpI7z/tISlH9JTd372uiCyBHP98r7cbta4SM4YFdJyKQQyX78Bo0MFmn+caaCH1fxpJ8VHfuQM1Dii3PPgRMCqQZ5h/vUYDiIOqdhoAY1+G3TpZyX35Dq+HBDIQ2Ecpeo5HxhvWEaMg6Vk2s+Mz8AuGR9jji77cEXYx9JI2tEyVv/u6LqBtd/rZ1DXDgBLV07QNSR7hmpNto0pdSpZBd1/CFz0zrt37j5gqcPTw9nn0G8sJ5jjFViYY5tbzpZ9zl0zTTtB9Tddcwj5a+m/jNJQIDAQAB";
    public static final String SAAS_PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCISHxgTukvxvOLGyw8Z/CmJ8eZGUlkyIi7TVvXOdAueQgv41N3py7YpJZEtHKRmJM+kjvP+0hKUf0lN3fva6ILIEc/3yvtxu1rhIzhgV0nIpBDJfvwGjQwWaf5xpoIfV/GknxUd+5AzUOKLc8+BEwKpBnmH+9RgOIg6p2GgBjX4bdOlnJffkOr4cEMhDYRyl6jkfGG9YRoyDpWTaz4zPwC4ZH2OOLvtwRdjH0kja0TJW/+7ouoG13+tnUNcOAEtXTtA1JHuGak22jSl1KlkF3X8IXPTOu3fuPmCpw9PD2efQbywnmOMVWJhjm1vOln3OXTNNO0H1N11zCPlr6b+M0lAgMBAAECggEAYSWyq4B0z1716tpS3TiGnhrLOIKDA/nDJilN541JrmcOg/x7WlbtmPcyOwWaidkGVVsukeKhNIFff7EbqKrEF0RKJeizSss8ISGav4FU4vnLdmJURmKXg0PV7sI4uQaOSGHRiPD5AG6Rz6U6Dw5HGmrCH33yPdou16IG6oLTsscjHYKrk+KDFWwmJ3tykZXjsRrGu3RZOYBipuMD/FWzrGGKQsj0WJjMdl/UjdAfPV/p6Ce5x8Hk5Ai9y8SBcWIWGoz3xVVXeiDFuSD/ACzC4nTAL6iEkPRW0Wj1gjIeLBUxNmx6U4HP3lLClnKJR+DuVk8Osrx09DcsGmFXbPWBYQKBgQDOgcPQyIt/EeTeVlxJmR2C2ry2fdvd6kcfKvu8itpoOgOT6CUCUIltnJmJ60q3bpesEYNvPdya45jLL1N5YJjcoyAM3P5LwvQ/z9Ed0dWtdOFrxaDXdCt1cBrb48xur7DL478467wF06mw3zdQ8Re5RI/F6t2w4wAAhcCrZhKPyQKBgQCo8iQz/ZLWtQeGMrKYaTg8Xx6Vi1D7UR4DlSipF2ruH82WfCNpdd7az9o8j9VRwAXvlxHVmjNh7AepvonfXJu6qPIPNBvxRY6sUuxjkDmQBaQruAe7reto3Jitf3wuAZ0JeVgyQA0ZkbaWXdFKIiOSzbWrKAMmij9fmDiUwF/YfQKBgQDJrSmTyYeSveMK0MSojASqOv16LEiB3b8/zTa019adW6sK0/jw3b6ZcgrxCv2ZT0SJI0F9bqj4C7a5GKspKxGeySEP7vPbAbaQa2ELAlbwY+mS2xtNz4Jx2t4gScTphGPhe7bQ3EhwWFqtBhJ71jkZEsBmxg1kJw1ldTrJKZKPIQKBgQCKt784HSXbO2sBKlFwSTzOfT+MhIaqw/ED+g4XwM2g1eYlgKpG5mxkd7P2dBcwd52oGv+exm0YIwgrvPJRvyVy/1M5Xozu7zD1l2TXX02UTr181C8tT+FyCWcnVfztfY+mfscHA4Z+DyG/lyrIVv2khVl8Pm9PzsW4RTF4GfjYtQKBgQDFwPKmV4S3S01gehr/tszzw1LorrLyV8Upg9Sx0DDI8NMrSLPlGhwN3X1luvFmrT2mgcoC6c7WvDyxI2nX1qhAGVCA5G/xLM1vrEn5EQvY4mXpYBaZCfBg3SFDF++CFHrlVNcpsg9Wm/IVYffxrofvoOfxdHTKBeVt4v78EIvSWA==";

    public static final String MOM_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoCX/9gsjcbgjBRHgtUPFsOjbQA7KGPOxUXqRR4iAJEjpYbPBJtFKhbr0W4UPkR9P0vKRpvQy81YQiDkWNraqyjAtCwhyMOiB9K8JrnWpiEX/Q6aLWHrN3C45nvR0tY4R4H4xqpL6qAve3/1YVTD3w8ohtyoFJJLWXFGcfdXg1H73iRAJ8whuoCeF071GahrlalIn8W0J503NVkXqZETxOrpAGB4eG5ZTSB3rKpk4ixOfpswlzqopwM33QS/V7nQh0s/9Ev7lW7TrONGdwBQ3YJEBVq0cg0B6QtlLs2DnjAsd7TJpVRUfej6NYuFKCH9hCZGfr6kmHWh5G1i4DI+rowIDAQAB";
    public static final String MOM_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCgJf/2CyNxuCMFEeC1Q8Ww6NtADsoY87FRepFHiIAkSOlhs8Em0UqFuvRbhQ+RH0/S8pGm9DLzVhCIORY2tqrKMC0LCHIw6IH0rwmudamIRf9DpotYes3cLjme9HS1jhHgfjGqkvqoC97f/VhVMPfDyiG3KgUkktZcUZx91eDUfveJEAnzCG6gJ4XTvUZqGuVqUifxbQnnTc1WRepkRPE6ukAYHh4bllNIHesqmTiLE5+mzCXOqinAzfdBL9XudCHSz/0S/uVbtOs40Z3AFDdgkQFWrRyDQHpC2UuzYOeMCx3tMmlVFR96Po1i4UoIf2EJkZ+vqSYdaHkbWLgMj6ujAgMBAAECggEAWhRlwACN5EY9tHlU2sCPLy4bPRHUjWptX7ZAh2r5IO++Dk2spGfusf/xuJTEp81j+fSlBrKvAf5z+BucKhRfRrEIdP9AM9BkYLwgBvh57oPozCWelZslteZuVMGN8B1EYvC4gQiBYwqiR31aNTkA9Cj7A+rnz6WkW3MwQfuOphcKKib2/QEeDhJ4adJj8gGHsEv4/B164tpj4D0sl6oEG1NU363+/zt27li2b/+KINXNZIBI2WS4tXpmm/TnKGK6gCa1E3zCvw3nAXkGZGppzCnuiiWtl0Vg9s1fnAlM4cEko/+c2F9RL/VLovjg0W1EGKJvFVtCVEDH5xoFigO1oQKBgQDsLckOej+TlVBlxswLXAlxqtEwjnmytOkuIWbklOswZ/OvIHoTgOZ7ZOS5M8u6d7/XT82bZGouXmIKKP7uRMOFMqKClGf4dS69aBzDvYAyN15u5y8pJz0Zd6IX8YDFIjbt8+VP0vTPcKVEuhzImfrbLv5fRcoDFzUJVXaAa14jsQKBgQCtlrq4eF/Gps5irqnQTmZjGlzdvw+ZKyXbsmUjfEnfV7a11hlfjKYJTpGvaDAZM70FHDXRSYloKCfFWtXKYdzCd8Pwd/OdIPc1msKlAzReyMtFzQqoW5U08bNQVOamKN5byVqJ4SfLifeKCL7sbrfuK3n7Z/y8rXf1TiGFJwA9kwKBgQCE+nKN+D77tcKCy5/vdW7L1UXbdR0IhdwU4TGx2jdiFeWa4Upa2OTs7S3rKUK4Rtx5OrGloLA4U2QYHGUNeyIrSQs/QpgXyML8WjZ376bSn2JIR1rCbRl10Wou2QeL92u+JukiPMEiW2Zyf+fc+vXHm3oV4pGPk+2Ex93lCI8EwQKBgDO9b544UuPVKEMFqJvsIlx7qR53KmEPGVOokrjG/QXTESSV0i5Gr96qSoYr2dpJL5fsVqYw8wHWpjQO5RZToQddx0tPlY9KGiUiIc4pbFysINqHSRvxYEHNoRLam3z/RXe6RoA+WiwyzVOZU78JKU2WK9+hmRVr5wt45EjZ3Rp9AoGBAN6Po9n5b22k5cs+rahMc21C8U/7G7eE7r+j78bXpRgUev4Jo/w5xXSojnlHR5gf1yDchE9HlnZAGcpOI2ug+FGDlFXPvtUl9Yu+tOF9AKal2xHEPxZJaJw4f7YhZly5IbUSB1qNiRfXFkxDCCIIozSDM6E1n6AiuB0mhz2aXbod";


    /**
     * 生成RSA密钥对
     * @param keySize 密钥长度 (至少1024位)
     * @return KeyPair对象
     */
    public static KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        if (keySize < 1024) {
            throw new IllegalArgumentException("密钥长度至少为1024位");
        }
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(keySize);
        return keyPairGen.generateKeyPair();
    }

    /**
     * 公钥加密
     * @param plainText 明文
     * @param publicKey 公钥
     * @return Base64编码的密文
     */
    public static String encrypt(String plainText, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new BusinessException(500, "Encryption exception");
        }
    }

    /**
     * 私钥解密
     * @param cipherText Base64编码的密文
     * @param privateKey 私钥
     * @return 解密后的明文
     */
    public static String decrypt(String cipherText, PrivateKey privateKey) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(cipherText);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new BusinessException(500, "Decryption exception");
        }
    }

    /**
     * 私钥签名
     * @param params 原始数据
     * @param privateKey 私钥
     * @return Base64编码的签名
     */
    public static String sign(Map<String, String> params, PrivateKey privateKey) {
        try {

            // 1. 过滤空值和签名字段，按参数名排序
            Map<String, String> filteredParams = filterAndSortParams(params);

            // 2. 拼接键值对
            String data = buildQueryString(filteredParams);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new BusinessException(500, "Sign exception");
        }
    }

    /**
     * 公钥验签
     * @param params 原始数据
     * @param sign Base64编码的签名
     * @param publicKey 公钥
     * @return 验签是否通过
     */
    public static boolean verify(Map<String, String> params, String sign, PublicKey publicKey) {
        try {

            // 1. 过滤空值和签名字段，按参数名排序
            Map<String, String> filteredParams = filterAndSortParams(params);

            // 2. 拼接键值对
            String data = buildQueryString(filteredParams);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new BusinessException(500, "Authentication exception");
        }
    }


    /**
     * 过滤空值和 sign 字段，并按参数名排序
     * @param params
     * @return
     */
    private static Map<String, String> filterAndSortParams(Map<String, String> params) {
        Map<String, String> result = new TreeMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!"sign".equals(key) && ObjectUtils.isNotEmpty(value)) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 拼接键值对字符串
     * @param params
     * @return
     */
    private static String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }


    /**
     * 从Base64字符串加载公钥
     */
    public static PublicKey loadPublicKey(String publicKeyStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new BusinessException(500, "Public key loading exception");
        }
    }

    /**
     * 从Base64字符串加载私钥
     */
    public static PrivateKey loadPrivateKey(String privateKeyStr) {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new BusinessException(500, "Private key loading exception");
        }
    }

    /**
     * 将公钥转换为Base64字符串
     */
    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 将私钥转换为Base64字符串
     */
    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }


    public static void main(String[] args) {
        String decryptedText = RSAUtils.encrypt("123456333", RSAUtils.loadPublicKey(RSAUtils.MOM_PUBLIC_KEY));
        System.out.println(decryptedText);



        String decrypt = RSAUtils.decrypt(decryptedText, RSAUtils.loadPrivateKey(MOM_PRIVATE_KEY));
        System.out.println(decrypt);
    }
}