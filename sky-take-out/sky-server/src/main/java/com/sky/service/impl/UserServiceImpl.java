package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* @author tx
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2025-06-04 16:07:40
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    private final static String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO);

        if(openid == null){
            throw new RuntimeException("openid为空");
        }

        User user = userMapper.selectByOpenid(openid);

        if(user == null){

            user = User.builder()
                    .openid(openid)
                    .createTime(new Date())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    private String getOpenid(UserLoginDTO userLoginDTO) {

        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        String data = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(data);
        return jsonObject.getString("openid");
    }
}




