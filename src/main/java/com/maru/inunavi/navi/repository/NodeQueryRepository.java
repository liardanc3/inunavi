package com.maru.inunavi.navi.repository;

import com.maru.inunavi.navi.domain.dto.RouteInfo;

import java.util.List;

public interface NodeQueryRepository {

    Integer findSrcNodeIdByRouteInfo(RouteInfo routeInfo);
    List<Integer> findDstNodeIdByRouteInfo(RouteInfo routeInfo);
}
