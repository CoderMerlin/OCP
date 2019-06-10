package com.open.capacity.client.service;

import org.springframework.cloud.gateway.route.RouteDefinition;

public interface IDynamicRouteService {

    String add(RouteDefinition definition);

    String update(RouteDefinition definition);

    String delete(String id);

}
