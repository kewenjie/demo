package com.example.demo.domain;

import java.io.Serializable;

/**
 * role
 * @author 
 */
public class Role implements Serializable {
    private Long id;

    private String name;

    private String desc_;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc_() {
        return desc_;
    }

    public void setDesc_(String desc_) {
        this.desc_ = desc_;
    }
}