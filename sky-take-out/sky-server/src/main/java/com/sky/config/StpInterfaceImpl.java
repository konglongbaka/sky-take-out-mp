//package com.sky.config;
//
//import com.sky.entity.Employee;
//import com.sky.entity.Permission;
//import com.sky.entity.Role;
//import com.sky.service.EmployeeService;
//import com.sky.service.PermissionService;
//import com.sky.service.RoleService;
//import com.sky.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//@Component
//public class StpInterfaceImpl implements StpInterface {
//    @Autowired
//    private RoleService roleService;
//    @Autowired
//    private EmployeeService employeeService;
//    @Autowired
//    private PermissionService permissionService;
//
//    @Override
//    public List<String> getPermissionList(Object loginId, String loginType) {
//        Employee employee = employeeService.getById((Serializable) loginId);
//        List<String> list = new ArrayList<>();
//        Permission permission = permissionService.getById(employee.getRoleId());
//        list.add(permission.getPermissionName());
//        return list;
//    }
//
//    @Override
//    public List<String> getRoleList(Object loginId, String loginType) {
//        Employee employee = employeeService.getById((Serializable) loginId);
//        List<String> list = new ArrayList<>();
//        Role role = roleService.getById(employee.getRoleId());
//        list.add(role.getRoleName());
//        return list;
//    }
//}
