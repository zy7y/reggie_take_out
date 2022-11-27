package com.zy7y.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy7y.reggie_take_out.common.R;
import com.zy7y.reggie_take_out.entity.DO.Employee;
import com.zy7y.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 1. 加密前端过来的密码
        String passwd = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        // 2. 根据用户名查用户
        // 2.1 条件
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        // 2,2 查
        Employee emp = employeeService.getOne(lambdaQueryWrapper);

        if (Objects.isNull(emp)){
            return R.error("用户名错误");
        }
        if (!emp.getPassword().equals(passwd)){
            return R.error("密码错误");
        }
        if (emp.getStatus() == 0){
            return R.error("账号禁用");
        }

        // 登录成功 注入session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    };

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        // md5 password
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long id = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(id);
        employee.setUpdateUser(id);

        employeeService.save(employee);
        return R.success("新增成功");
    }

    /**
     * 分页员工列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page, Integer pageSize, String name){
        // 分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        // 条件
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like((name != null), Employee::getName, name)
                .orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 更新状态
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        Long id = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(id);
        employeeService.updateById(employee);
        return R.success("更新成功");
    }

    /**
     * 根据id查员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
       Employee employee = employeeService.getById(id);
       if (Objects.isNull(employee)){
           return R.error("没有查询到数据");
       }
       return R.success(employee);
    }

}
