package com.tigerit.soa.service.redis;

import com.tigerit.soa.response.ServiceResponse;

import java.util.concurrent.TimeUnit;

/**
 * Created by DIPU on 4/29/20
 */
public interface RedisCacheService<T> {

    void syncRedis(String key, T t, boolean isNewKey);
    void syncRedisForTTL(String key, T object, Long timeInMs, TimeUnit unit);
    T getRedisObjectByKey(String key, T clazz);
}
