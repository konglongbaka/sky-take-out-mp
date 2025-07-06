package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
* @author tx
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2025-06-04 16:07:40
*/
public interface UserService extends IService<User> {

    User login(UserLoginDTO userLoginDTO);
}
