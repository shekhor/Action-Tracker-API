package com.tigerit.soa.serviceImpl.redis;

import com.tigerit.soa.service.redis.RedisCacheService;
import com.tigerit.soa.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by DIPU on 4/29/20
 */

@Service
public class RedisCacheServiceImpl<T> implements RedisCacheService<T> {

    private Logger logger = LoggerFactory.getLogger(RedisCacheServiceImpl.class);

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public void syncRedis(String key, T object, boolean isNewKey)
    {
        if(Util.isEmpty(key)) return;

        if(!isNewKey && isKeyPresent(key))
        {
            redisTemplate.opsForValue().set(key,object);
            logger.info("redis synch: true, old key: {}", key);
        }
        else if(isNewKey)
        {
            redisTemplate.opsForValue().set(key, object);
            logger.info("redis synch: true, new key inserted: {}",key);
        }
        else
        {
            logger.info("redis synch: redis not updated");
        }
    }

    @Override
    public void syncRedisForTTL(String key, T object, Long timeInMs, TimeUnit unit) {
        //no condition applied, will do upon requirement
        redisTemplate.opsForValue().set(key, object, timeInMs, TimeUnit.MILLISECONDS);
        logger.info("sync redis with TTL, key: {}", key);
    }

    @Override
    public T getRedisObjectByKey(String key, T clazz) {

        T t=(T)redisTemplate.opsForValue().get(key);
        return t;
    }

    private boolean isKeyPresent(String key)
    {
        return redisTemplate.hasKey(key);
    }


}
