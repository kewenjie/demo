package com.example.demo.service.impl;

import java.util.List;

import com.example.demo.domain.User;
import com.example.demo.domain.UserExample;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserRoleService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleService userRoleService;

    @Override
    public String getPassword(String name) {
        User user = getByName(name);
        if (null == user)
            return null;
        return user.getPassword();
    }

    @Override
    public User getByName(String name) {
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(example);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public void add(User u) {
        userMapper.insert(u);
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
        userRoleService.deleteByUser(id);
    }

    @Override
    public void update(User u) {
        userMapper.updateByPrimaryKeySelective(u);
    }

    @Override
    public User get(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<User> list() {
        UserExample example = new UserExample();
        example.setOrderByClause("id desc");
        return userMapper.selectByExample(example);
    }

}