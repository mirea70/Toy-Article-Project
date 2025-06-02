package kuke.board.articleread.cache;

import kuke.board.common.dataserializer.DataSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OptimizedCacheManager {
    private final StringRedisTemplate redisTemplate;
    private final OptimizedCacheLockProvider optimizedCacheLockProvider;

    private static final String DELIMITER = "::";

    public Object process(String type, long ttlSeconds, Object[] args, Class<?> returnType,
                          OptimizedCacheOriginDataSupplier<?> originDataSupplier) throws Throwable {
        String key = generateKey(type, args);

        String cachedData = redisTemplate.opsForValue().get(key);
        // 물리 TTL 만료되서 캐시데이터 삭제된 경우
        if (cachedData == null) {
            return refresh(originDataSupplier, key, ttlSeconds);
        }

        OptimizedCache optimizedCache = DataSerializer.deserialize(cachedData, OptimizedCache.class);
        // 역직렬화 오류난 경우
        if (optimizedCache == null) {
            return refresh(originDataSupplier, key, ttlSeconds);
        }

        // 유효 캐시 데이터 존재하는 경우
        if (!optimizedCache.isExpired()) {
            return optimizedCache.parseData(returnType);
        }

        // 논리적 TTL 만료되서 Lock 획득하는 경우 : Lock 획득 못할시, 가진 캐시데이터 그대로 반환
        if (!optimizedCacheLockProvider.lock(key)) {
            return optimizedCache.parseData(returnType);
        }

        // 락 획득, 성공 시 캐시 데이터 갱신 후 반환
        try {
            return refresh(originDataSupplier, key, ttlSeconds);
        } finally {
            // 락을 점유했으므로 락도 반환
            optimizedCacheLockProvider.unlock(key);
        }
    }

    private Object refresh(OptimizedCacheOriginDataSupplier<?> originDataSupplier, String key, long ttlSeconds) throws Throwable {
        Object result = originDataSupplier.get();

        OptimizeCacheTTL optimizeCacheTTL = OptimizeCacheTTL.of(ttlSeconds);
        OptimizedCache optimizedCache = OptimizedCache.of(result, optimizeCacheTTL.getLogicalTTL());

        redisTemplate.opsForValue()
                .set(
                        key,
                        DataSerializer.serialize(optimizedCache),
                        optimizeCacheTTL.getPhysicalTTL()
                );
        return result;
    }

    private String generateKey(String prefix, Object[] args) {
        return prefix + DELIMITER +
                Arrays.stream(args)
                        .map(String::valueOf)
                        .collect(Collectors.joining(DELIMITER));
        // prefix = a, args = [1,2]
        // a::1::2
    }
}
