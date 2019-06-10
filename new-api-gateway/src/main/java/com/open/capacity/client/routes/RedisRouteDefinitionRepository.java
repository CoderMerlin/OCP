package com.open.capacity.client.routes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {

    public static final String GATEWAY_ROUTES_PREFIX = "geteway_routes_";

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Set<RouteDefinition> routeDefinitions = new HashSet<>();

    /**
     * 获取全部路由
     * @return
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        Set<String> gatewayKeys = redisTemplate.keys(GATEWAY_ROUTES_PREFIX + "*");
        if (!CollectionUtils.isEmpty(gatewayKeys)) {
            List<String> gatewayRoutes = Optional.ofNullable(redisTemplate.opsForValue().multiGet(gatewayKeys)).orElse(Lists.newArrayList());
            gatewayRoutes
                    .forEach(routeDefinition -> routeDefinitions.add(JSON.parseObject(routeDefinition, RouteDefinition.class)));
        }

        return Flux.fromIterable(routeDefinitions);
    }

    /**
     * 添加路由方法
     * @param route
     * @return
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(routeDefinition -> {
            routeDefinitions.add( routeDefinition );
            return Mono.empty();
        });
    }

    /**
     * 删除路由
     * @param routeId
     * @return
     */
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            List<RouteDefinition> collect = routeDefinitions.stream().filter(
                    routeDefinition -> StringUtils.equals(routeDefinition.getId(), id)
            ).collect(Collectors.toList());
            routeDefinitions.removeAll(collect);
            return Mono.empty();
        });
    }
}
