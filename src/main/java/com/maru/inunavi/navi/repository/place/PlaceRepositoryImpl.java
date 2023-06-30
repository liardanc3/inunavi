package com.maru.inunavi.navi.repository.place;

import com.maru.inunavi.navi.domain.entity.Place;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.maru.inunavi.navi.domain.entity.QPlace.place;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<Place>> findByTitleOrSort(String searchKeyword) {

        searchKeyword = searchKeyword.replaceAll(" ", "").toUpperCase();

        return Optional.of(
                queryFactory
                        .selectFrom(place)
                        .where(
                                Expressions.stringTemplate("replace(upper({0}), ' ', '')", place.title).toUpperCase().contains(searchKeyword),
                                Expressions.stringTemplate("replace(upper({0}), ' ', '')", place.sort).toUpperCase().contains(searchKeyword)
                        )
                        .fetch()
        );
    }
}
