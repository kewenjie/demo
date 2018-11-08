package com.example.demo.config;

import com.example.demo.domain.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.PermissionService;
import com.example.demo.service.RoleService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ShiroRealm extends AuthorizingRealm {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleService roleService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("欢迎认证");
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();//从令牌张获取用户名
        User user = userMapper.findUserByUsername(username);
        if (user == null) {
            return null;
        } else {
//            用户名存在
            String password = user.getPassword();
            String salt = user.getSalt();
            //创建简单认证信息对象
            /**
             * 参数一:签名,程序可以任意位置获取当前放入的对象
             * 参数二:从数据库中查询出的密码
             * 参数三：为密码设置的盐
             * 参数四:当前realm的名称
             */
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, ByteSource.Util.bytes(salt), this.getClass().getSimpleName());
            return info;
        }

    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //能进入到这里，表示账号已经通过验证了
        User user =(User)principalCollection.getPrimaryPrincipal();
        String userName = user.getName();
        //通过service获取角色和权限
        Set<String> permissions = permissionService.listPermissions(userName);
        Set<String> roles = roleService.listRoleNames(userName);
        //授权对象
        //把通过service获取到的角色和权限放进去
        info.setStringPermissions(permissions);
        info.setRoles(roles);
        return info;
    }

}
