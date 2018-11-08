package com.example.demo.service;

import com.example.demo.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public String getPassword(String name);

    public User getByName(String name);

    public List<User> list();

    public void add(User user);

    public void delete(Long id);

    public User get(Long id);

    public void update(User user);
}