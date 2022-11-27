package com.zy7y.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy7y.reggie_take_out.common.R;
import com.zy7y.reggie_take_out.entity.DO.User;
import com.zy7y.reggie_take_out.service.UserService;
import com.zy7y.reggie_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

/**
 * 移动端
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        if (user.getPhone() != null){
            String code = ValidateCodeUtils.generateValidateCode4String(4);
            session.setAttribute(user.getPhone(), code);
            return R.success("验证码: " + code);
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody Map<String, String> map, HttpSession session){
        String phone = map.get("phone");
        String code = map.get("code");
        if (!session.getAttribute(phone).equals(code)){
            return R.error("验证码错误");
        }
        // 查询是否存在用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userService.getOne(queryWrapper);
        if (Objects.isNull(user)){
            // 注册
            user = new User();
            user.setPhone(phone);
            userService.save(user);
        }
        session.setAttribute("user", user.getId());
        return R.success("登录成功");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
