package com.maru.inunavi.navi.repository.node;

import com.maru.inunavi.navi.domain.dto.RouteInfo;
import com.maru.inunavi.navi.domain.entity.Node;
import com.maru.inunavi.navi.repository.node.NodeQueryRepository;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static com.maru.inunavi.navi.domain.entity.QNode.node;

@RequiredArgsConstructor
public class NodeRepositoryImpl implements NodeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Integer findSrcNodeIdByRouteInfo(RouteInfo routeInfo) {
        return queryFactory
                .select(Projections.fields(Node.class,
                        node.id
                ))
                .from(node)
                .where(ifPlaceCode(routeInfo.getStartPlaceCode()))
                .orderBy(ifLocation(routeInfo.getStartLocation(), routeInfo.getStartPlaceCode()))
                .limit(1)
                .fetchOne()
                .getId();
    }

    @Override
    public List<Integer> findDstNodeIdByRouteInfo(RouteInfo routeInfo) {
        return queryFactory
                .select(Projections.fields(Node.class,
                        node.id
                ))
                .from(node)
                .where(ifPlaceCode(routeInfo.getEndPlaceCode()))
                .orderBy(ifLocation(routeInfo.getEndLocation(), routeInfo.getEndPlaceCode()))
                .fetch()
                .stream()
                .map(Node::getId)
                .collect(Collectors.toList());
    }

    // ----------------- EXPRESSIONS AND CONDITIONS ---------------------------- //

    private OrderSpecifier<Double> distanceAsc(String location) {
        StringTokenizer locationToken = new StringTokenizer(location);

        String lat = locationToken.nextToken(", ");
        String lng = locationToken.nextToken(", ");

        NumberTemplate<Double> nodeLat4326 = Expressions.numberTemplate(Double.class, "{0}", node.lat4326);
        NumberTemplate<Double> lat4326 = Expressions.numberTemplate(Double.class, "{0}", lat);
        NumberTemplate<Double> nodeLng4326 = Expressions.numberTemplate(Double.class, "{0}", node.lng4326);
        NumberTemplate<Double> lng4326 = Expressions.numberTemplate(Double.class, "{0}", lng);

        return Expressions.numberTemplate(Double.class, "abs({0} - {1})", nodeLat4326, lat4326)
                .add(Expressions.numberTemplate(Double.class, "abs({0} - {1})", nodeLng4326, lng4326))
                .asc();
    }

    private BooleanExpression ifPlaceCode(String placeCode) {
        return !placeCode.equals("LOCATION") ? node.placeCode.contains(placeCode) : null;
    }

    private OrderSpecifier<Double> ifLocation(String location, String placeCode){
        return placeCode.equals("LOCATION") ? distanceAsc(location) : new OrderSpecifier(Order.ASC, NullExpression.DEFAULT, OrderSpecifier.NullHandling.Default);
    }
}
