package com.example.demo.service;

import com.example.demo.domain.User;
import org.springframework.stereotype.Service;

@Service
public interface UserRoleService {

    public void setRoles(User user, long[] roleIds);

    public void deleteByUser(long userId);

    public void deleteByRole(long roleId);

}