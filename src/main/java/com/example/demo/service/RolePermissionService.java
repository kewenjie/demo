package com.example.demo.service;

import com.example.demo.domain.Role;
import org.springframework.stereotype.Service;

@Service
public interface RolePermissionService {
    public void setPermissions(Role role, long[] permissionIds);

    public void deleteByRole(long roleId);

    public void deleteByPermission(long permissionId);
}