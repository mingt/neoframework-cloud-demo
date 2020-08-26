
package com.neoframework.common.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * The type Redis cache config.
 *
 * @author lihaixin
 * @version 1.0
 * @ClassName: RedisCacheConfig
 * @Description: TODO(redis缓存管理类)
 * @date 2018年9月12日 下午5:03:20
 */
@CacheConfig
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {

    // @Bean
    // public KeyGenerator keyGenerator() {
    // return new KeyGenerator() {
    // // 为给定的方法及其参数生成一个键
    // @Override
    // public Object generate(Object target, Method method, Object... params) {
    // StringBuffer sb = new StringBuffer();
    // sb.append(target.getClass().getName());// 类名
    // sb.append("-");
    // sb.append(method.getName());// 方法名
    // sb.append("-");
    // for (Object param : params) { // 参数
    // if (param != null) {
    // // TODO: ahming notes: 这个值大机率造成key不同，等同key失效。如果的确要用，务必检查详细参数的toString输出
    // sb.append(param.toString());
    // }
    // }
    // return sb.toString();
    // }
    // };
    // }

    /**
     * Cache manager cache manager.
     *
     * @param redisTemplate the redis template
     * @return the cache manager
     */
    @SuppressWarnings("rawtypes")
    @Bean(name = "redisCacheManager")
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        // 初始化一个RedisCacheWrite
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // 设置默认的过期时间(以秒为单位)
        cacheManager.setDefaultExpiration(3600 * 24 * 3); // 600
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }

    /**
     * Redis template redis template.
     *
     * @param factory the factory
     * @return the redis template
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 序列化忽略 null 属性

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        StringRedisTemplate template = new StringRedisTemplate(factory);

        // // 使用 snappy 压缩
        // SnappyRedisSerializer snappyRedisSerializer = new SnappyRedisSerializer(jackson2JsonRedisSerializer);
        // template.setValueSerializer(snappyRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
