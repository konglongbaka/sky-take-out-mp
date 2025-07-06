package com.sky.mapper;

import com.sky.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author tx
* @description 针对表【user(用户信息)】的数据库操作Mapper
* @createDate 2025-06-04 16:07:40
* @Entity com.sky.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

    User selectByOpenid(String openid);
}




