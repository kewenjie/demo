package com.example.demo.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //用户登录次数计数  redisKey 前缀
    private String SHIRO_LOGIN_COUNT = "shiro-login-count";
    //用户登录是否被锁定    一小时 redisKey 前缀
    private String SHIRO_IS_LOCK = "shiro-is-lock";
    //用户登录剩余次数
    private String SHIRO_LOGIN_LEFTCOUNT = "shiro-login-left-count";

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(Model model, String name, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(name, password);
        try {
            subject.login(token);
            Session session = subject.getSession();
            session.setAttribute("subject", subject);
            return "redirect:index";

        } catch (AuthenticationException e) {
            model.addAttribute("error", "验证失败");
            return "login";
        }
    }

    @RequestMapping(value = "/ajaxLogin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> submitLogin(@RequestParam("username") String username,
                                           @RequestParam("password") String password,
                                           @RequestParam("password") String vcode,
                                           @RequestParam("rememberMe") Boolean rememberMe) {
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        Map<String, String> map = new HashMap<String, String>();

        if (vcode == null || vcode == "") {
            resultMap.put("status", 500);
            resultMap.put("message", "验证码不能为空！");
            return resultMap;
        }

        Session session = SecurityUtils.getSubject().getSession();
        //转化成小写字母
        vcode = vcode.toLowerCase();
        String v = (String) session.getAttribute("_code");
        //还可以读取一次后把验证码清空，这样每次登录都必须获取验证码
        session.removeAttribute("_come");
        if (!vcode.equals(v)) {
            resultMap.put("status", 500);
            resultMap.put("message", "验证码错误！");
            return resultMap;
        }
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        if (stringRedisTemplate.hasKey(SHIRO_LOGIN_COUNT + username)) {
            //计数大于5时，设置用户被锁定一小时
            if (Integer.parseInt(opsForValue.get(SHIRO_LOGIN_COUNT + username)) >= 5) {
                opsForValue.set(SHIRO_IS_LOCK + username, "LOCK");
                stringRedisTemplate.expire(SHIRO_IS_LOCK + username, 1, TimeUnit.HOURS);

            }
        }
        opsForValue.increment(SHIRO_LOGIN_COUNT + username, 1);
        int leftcount = 5 - Integer.parseInt(opsForValue.get(SHIRO_LOGIN_COUNT + username));
        opsForValue.set(SHIRO_LOGIN_LEFTCOUNT + username, String.valueOf(leftcount));
        stringRedisTemplate.expire(SHIRO_LOGIN_LEFTCOUNT + username, 1, TimeUnit.HOURS);


        try {
            if ("LOCK".equals(opsForValue.get(SHIRO_IS_LOCK + username))) {
                session.setAttribute("user", "lock");
                session.setTimeout(60);
                throw new LockedAccountException();
            }
            UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
            SecurityUtils.getSubject().login(token);
            resultMap.put("status", 200);
            resultMap.put("message", "登录成功");
            stringRedisTemplate.opsForValue().set(SHIRO_LOGIN_COUNT + username, "0");
            stringRedisTemplate.opsForValue().set(SHIRO_IS_LOCK + username, "UNLOCK");
            map.put("user", username);

        } catch (LockedAccountException e) {
            resultMap.put("status", 400);
            resultMap.put("message", "您已经被锁定1小时！");
        } catch (UnknownAccountException e) {
            resultMap.put("status", 300);
            resultMap.put("message", opsForValue.get(SHIRO_LOGIN_LEFTCOUNT + username));
        } catch (IncorrectCredentialsException e1) {
            resultMap.put("status", 100);
            resultMap.put("message", opsForValue.get(SHIRO_LOGIN_LEFTCOUNT + username));
        } catch (Exception e) {
            resultMap.put("status", 600);
            resultMap.put("message", opsForValue.get(SHIRO_LOGIN_LEFTCOUNT + username));
        }
        return resultMap;
    }

}
