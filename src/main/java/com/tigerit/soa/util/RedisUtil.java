package com.tigerit.soa.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by DIPU on 4/29/20
 */

@Component
@RequiredArgsConstructor
public class RedisUtil<T> {

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

 /*   @Autowired
    private HashOperations<String,Object,T> hashOperation;
    @Autowired
    private ListOperations<String,T> listOperation;*/

    /*@Autowired
    private  ValueOperations<String, T>valueOperations;*/
    //private final ValueOperations<String, T> valueOperations;

    public long getNextId(String redisKey, long initVal) {
        Boolean hasKey = redisTemplate.hasKey(redisKey);

        if (hasKey != null && !hasKey) {
            redisTemplate.opsForValue().set(redisKey, initVal);
            return initVal;
        }

        return redisTemplate.opsForValue().increment(redisKey);
    }

    public void deleteKey(String redisKey) {
        Boolean hasKey = redisTemplate.hasKey(redisKey);

        if (hasKey != null && !hasKey) {
            redisTemplate.delete(redisKey);
        }
    }

   /* public boolean isKeyPresent(String key)
    {
        return redisTemplate.hasKey(key);
    }

    public void putValue(String key,T value) {
        valueOperations.set(key, value);
    }
    public void putValueWithExpireTime(String key, T value, long timeout, TimeUnit unit) {
        valueOperations.set(key, value, timeout, unit);
    }
    public T getValue(String key) {
        return valueOperations.get(key);
    }*/
}
