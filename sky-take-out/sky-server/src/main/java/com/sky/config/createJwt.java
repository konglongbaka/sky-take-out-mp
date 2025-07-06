package com.sky.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class createJwt {
    public static void main(String[] args) {
        Set<Integer> random = generateRandomNumbers(10000, 20000);
        String secretKey = "itcast";

        // 使用 try-with-resources 自动关闭文件流
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("jwts.txt"))) {
            random.forEach(id -> {
                Map<String, Object> claims = new HashMap<>();
                claims.put("empId", id);
                String jwt = createJWT(secretKey, claims);

                try {
                    writer.write(jwt);  // 写入JWT字符串
                    writer.newLine();   // 换行
                } catch (IOException e) {
                    // 处理单条写入失败（避免中断整个循环）
                    e.printStackTrace();
                }
            });
            System.out.println("JWT已成功写入 jwts.txt");
        } catch (IOException e) {
            // 处理文件打开/关闭错误
            e.printStackTrace();
        }
    }

    public static String createJWT(String secretKey, Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8));

        return builder.compact();
    }

    public static Set<Integer> generateRandomNumbers(int n, int max) {
        if (n > max) {
            throw new IllegalArgumentException("n must be less than or equal to max");
        }

        Set<Integer> set = new HashSet<>();
        Random random = new Random();

        while (set.size() < n) {
            int num = random.nextInt(max) + 1;
            set.add(num);
        }

        return set;
    }
}
