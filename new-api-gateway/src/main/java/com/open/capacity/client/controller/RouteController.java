package com.open.capacity.client.controller;

import com.open.capacity.client.commons.Result;
import com.open.capacity.client.dto.GatewayRouteDefinition;
import com.open.capacity.client.service.impl.DynamicRouteService;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private DynamicRouteService dynamicRouteService;

    /**
     * 初始化 转化对象
     */
    private static MapperFacade routeDefinitionMapper;
    static {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(GatewayRouteDefinition.class,RouteDefinition.class)
                .exclude("uri")
                .byDefault();
        routeDefinitionMapper = mapperFactory.getMapperFacade();
    }

    //增加路由
    @PostMapping("/add")
    public Result add(@RequestBody GatewayRouteDefinition gatewayRouteDefinition) {
        try {
            RouteDefinition definition = transformFo(gatewayRouteDefinition);
            dynamicRouteService.add(definition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succeed("");
    }

    //删除路由
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable String id) {
        return Result.succeed(dynamicRouteService.delete(id));
    }


    //更新路由
    @PostMapping("/update")
    public Result update(@RequestBody GatewayRouteDefinition gatewayRouteDefinition) {
        try {
            RouteDefinition definition = transformFo(gatewayRouteDefinition);
            dynamicRouteService.update(definition);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.succeed("");
    }


    /**
     * 转化路由对象 因为存在 uri 需要转化
     * @param gatewayRouteDefinition
     * @return
     */
    private RouteDefinition transformFo(GatewayRouteDefinition gatewayRouteDefinition){
        RouteDefinition definition = new RouteDefinition();
        routeDefinitionMapper.map(gatewayRouteDefinition,definition);
        //设置路由id
        if (!StringUtils.isNotBlank(definition.getId())){
            definition.setId(java.util.UUID.randomUUID().toString().toUpperCase().replace("-",""));
        }

        URI uri = null;
        if(gatewayRouteDefinition.getUri().startsWith("http")){
            uri = UriComponentsBuilder.fromHttpUrl(gatewayRouteDefinition.getUri()).build().toUri();
        }else{
            // uri为 lb://consumer-service 时使用下面的方法
            uri = URI.create(gatewayRouteDefinition.getUri());
        }
        definition.setUri(uri);
        return definition;
    }


}
