package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Controller
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 新增用户
     */
    @PostMapping("/save")
    public String save(@RequestBody User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        boolean result = userService.save(user);
        return result ? "保存成功" : "保存失败";
    }

    /**
     * 根据ID删除用户
     */
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boolean result = userService.removeById(id);
        return result ? "删除成功" : "删除失败";
    }

    /**
     * 根据ID更新用户
     */
    @PutMapping("/update")
    public String update(@RequestBody User user) {
        user.setUpdateTime(LocalDateTime.now());
        boolean result = userService.updateById(user);
        return result ? "更新成功" : "更新失败";
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/get/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/list")
    public List<User> listAll() {
        return userService.list();
    }

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Page<User> page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<User> page = new Page<>(current, size);
        return userService.page(page);
    }

    /**
     * 根据条件查询用户
     */
    @GetMapping("/listByCondition")
    public List<User> listByCondition(@RequestParam(required = false) String name,
                                     @RequestParam(required = false) Integer age) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (age != null) {
            queryWrapper.eq("age", age);
        }
        return userService.list(queryWrapper);
    }
}