package com.sky.utils;


import com.sky.constant.PasswordConstant;
import com.sky.properties.PasswordProperties;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


public class PasswordUtil {

//    @Value("${sky.password.salt}")
//    private String salt;

    public static String setPassword(String password) {
        return DigestUtils.md5Hex(PasswordConstant.PASSWORD_SALT + password);
    }

    public static boolean matches(String inPassword,String password) {
        return password.equals(DigestUtils.md5Hex(PasswordConstant.PASSWORD_SALT + inPassword));
    }
}
