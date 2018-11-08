package com.example.demo.service.impl;

import com.example.demo.service.BaseCacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component("redisCache")
public class RedisCacheService implements BaseCacheService {
    //注入redisTemplate
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void append(String key, String value) {
        redisTemplate.opsForValue().append(key, value);
    }

    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void expire(String key, long seconds) {
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     */
    @Override
    public Long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public <T> long rPush(String key, List<T> list) {
        return this.redisTemplate.opsForList().rightPush(key, list);
    }

    @Override
    public <T> T lindex(String key, long index) {
        return (T) this.redisTemplate.opsForList().index(key, index);
    }

    @Override
    public <T> List<T> lRange(String key, long start, long end) {
        return (List<T>) this.redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public void lTrim(String key, long start, long end) {
        this.redisTemplate.opsForList().trim(key, start, end);
    }

    @Override
    public <T> void setList(String key, List<T> list) {
        this.del(key);
        this.rPush(key, list);
    }

    @Override
    public void setHmap(String key, Map<String, Object> map) {
        this.redisTemplate.opsForHash().put(key, "12454", map);
    }

    @Override
    public Map<String, Object> getHmap(String key) {
        return (Map<String, Object>) this.redisTemplate.opsForHash().get(key, "12454");
    }

    @Override
    public void setPersist(String key) {
        this.redisTemplate.persist(key);
    }

    @Override
    public boolean isExist(String key) {
        return this.redisTemplate.hasKey(key);
    }
}
