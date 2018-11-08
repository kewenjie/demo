package com.example.demo.test;

import org.apache.shiro.crypto.hash.Md5Hash;

public class Main {
    public static void main(String[] args) {
        String password = "123";
        String encodedPassword = new Md5Hash(password).toString();

        System.out.println(encodedPassword);
    }
}
