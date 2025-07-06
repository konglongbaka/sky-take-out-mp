package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.entity.Role;
import com.sky.service.RoleService;
import com.sky.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author tx
* @description 针对表【role】的数据库操作Service实现
* @createDate 2025-06-11 17:09:07
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




