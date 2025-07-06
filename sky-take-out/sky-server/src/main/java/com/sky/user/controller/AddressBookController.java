package com.sky.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端地址簿接口")
public class AddressBookController {
    private final Integer IS_DEFAULT = 1;
    @Autowired
    private StringRedisTemplate   redisTemplate;

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前登录用户的所有地址信息
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list() {
        List<AddressBook> list = addressBookService.list(new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getUserId, BaseContext.getCurrentId()));
        return Result.success(list);
    }

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        AddressBook oldDefault = addressBookService.getOne(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getIsDefault, IS_DEFAULT)
                .eq(AddressBook::getUserId, BaseContext.getCurrentId()));
        if (oldDefault != null){
            oldDefault.setIsDefault(0);
            addressBookService.saveOrUpdate(oldDefault);
        }
        AddressBook newDefault = addressBookService
                .getOne(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getId, addressBook.getId()));
        newDefault.setIsDefault(IS_DEFAULT);
        addressBookService.saveOrUpdate(newDefault);
        return Result.success();
    }

    /**
     * 根据id删除地址
     *
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteById(Long id) {
        addressBookService.removeById(id);
        return Result.success();
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefault() {
        //SQL:select * from address_book where user_id = ? and is_default = 1
        List<AddressBook> list = addressBookService.list(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getIsDefault, IS_DEFAULT)
                .eq(AddressBook::getUserId, BaseContext.getCurrentId()));

        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }
        return Result.error("没有查询到默认地址");
    }

}
