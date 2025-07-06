package com.sky.admin.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.config.RedissionConfig;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.PasswordConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.utils.PasswordUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    private static final String EMP_KEY = "EMP:";
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedissionConfig redissionConfig;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO, HttpServletRequest request) {
        log.info("员工登录：{}", employeeLoginDTO);
//        request.getSession().setAttribute("employee", employeeLoginDTO);
//        // gobal
//        Object employee1 = request.getSession().getAttribute("employee");

        Employee employee = employeeService.login(employeeLoginDTO);
        if (employee == null)
        {
//            return Result.error(SaResult.error("密码错").toString());
            return Result.error("密码错误");
        }
        //登录成功后，生成satoken
//        StpUtil.login(employee.getId());
//        SaResult.ok("登录成功");
        BaseContext.setCurrentId(employee.getId());
//        登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        //
//        String token = StpUtil.getTokenValue();
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping
    public Result<String> save(@RequestBody Employee employee){
        log.info("新增员工：{}",employee);
        if (employee.getPassword()==null){
            employee.setPassword(PasswordUtil.setPassword(PasswordConstant.DEFAULT_PASSWORD));
        }
        employeeService.save(employee);
        return Result.success();
    }
    @GetMapping("/page")
    @Cacheable(cacheNames = "userCache", key = "'user'+#page")
    public Result<IPage<Employee>> page(Integer page, Integer pageSize, String name){
        log.info("分页查询：{}",page,pageSize,name);
        long current = page;
        long size = pageSize;
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Employee::getName,name);
        lambdaQueryWrapper.orderByAsc(Employee::getName);
        IPage<Employee> page1 = new Page<>(current,size);
        IPage<Employee> pageResult = employeeService.page(page1,lambdaQueryWrapper);
        return Result.success(pageResult);
    }

    @PostMapping(("/status/{status}"))
    public Result<String> startOrStop(@PathVariable Integer status,Long id){
        log.info("员工状态：{}",status,id);
        LambdaUpdateWrapper<Employee> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Employee::getId,id);
        lambdaUpdateWrapper.set(Employee::getStatus,status);
        employeeService.update(lambdaUpdateWrapper);
        return Result.success();
    }
    @PutMapping("/editPassword")
    public Result<String> editPassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("修改密码：{}",passwordEditDTO);
        Employee employee = employeeService.getById(passwordEditDTO.getEmpId());
        if (!PasswordUtil.matches(passwordEditDTO.getOldPassword(),employee.getPassword()))
            return Result.error("密码错误");
        LambdaUpdateWrapper<Employee> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Employee::getId,passwordEditDTO.getEmpId());
        lambdaUpdateWrapper.set(Employee::getPassword, PasswordUtil.setPassword(passwordEditDTO.getNewPassword()));
        employeeService.update(lambdaUpdateWrapper);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Cacheable(cacheNames = "user:id", key = "'user'+#id")
    public Result<Employee> getById(@PathVariable Long id) throws InterruptedException {
        String key = EMP_KEY + id;
        String json = (String) redisTemplate.opsForValue().get(key);
        if(json!=null){
            if (json.isEmpty()){
                return Result.error("未找到该员工");
            }
            Employee jsonObject= JSON.parseObject(json, Employee.class);
            return Result.success(jsonObject);
        }

        String lockKey = EMP_KEY + "lockKey:" + id;
        RLock rLock = redissionConfig.redissonClient().getLock(lockKey);
        boolean lock = rLock.tryLock(1,20, TimeUnit.SECONDS);
        if (!lock){
            return Result.error("请稍后再试");
        }
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            redisTemplate.opsForValue().set(key,"", 1000, TimeUnit.SECONDS);
            rLock.unlock();
            return Result.error("未找到该员工");
        }
        redisTemplate.opsForValue().set(key,JSON.toJSONString(employee), 1000, TimeUnit.SECONDS);
        rLock.unlock();
        return Result.success(employee);
    }

    @PutMapping
    public Result<String> update(@RequestBody Employee employee){
        log.info("员工修改：{}",employee);
        LambdaUpdateWrapper<Employee> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Employee::getId,employee.getId());
        employeeService.update(employee,lambdaUpdateWrapper);
        return Result.success();
    }

//    public  boolean tryLock(String key, String value, long expire,TimeUnit timeUnit) {
//        // 获取锁
//        return BooleanUtils.isTrue(redisTemplate.opsForValue().setIfAbsent(key, value, expire, timeUnit));
//    }
//    public  void unlock(String key, String value) {
//        // 判断锁是否ours
//        String currentValue = (String) redisTemplate.opsForValue().get(key);
//        if (value.equals(currentValue)) {
//            // 释放锁
//            redisTemplate.opsForValue().getOperations().delete(key);
//        }
}
