package com.example.demo.config;

import com.example.demo.service.BaseCacheService;
import com.example.demo.service.impl.RedisCacheService;
import com.example.demo.utils.ConfigurableConstants;
import com.example.demo.utils.SpringContextUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class GetHttpResponseHeader {
    private static BaseCacheService baseCacheService =SpringContextUtils.getContext().getBean(RedisCacheService.class);

    public static String getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        String token = map.get("token");
        if (!StringUtils.isNotEmpty(token)) {
            //取消token的失效时间,重新设置时间
            baseCacheService.setPersist(token);
            String validty = ConfigurableConstants.getProperty("token.validity", "30");
            //重新设置时间
            baseCacheService.expire("token", Integer.parseInt(validty) * 60);
        }
        return token;
    }
}
