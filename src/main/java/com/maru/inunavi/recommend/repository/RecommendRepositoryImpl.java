package com.maru.inunavi.recommend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.maru.inunavi.lecture.domain.entity.QLecture.lecture;
import static com.maru.inunavi.recommend.domain.entity.QRecommend.recommend;
import static com.maru.inunavi.user.domain.entity.QUser.user;

@RequiredArgsConstructor
public class RecommendRepositoryImpl implements RecommendQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findRecommendsByEmail(String email) {
        return queryFactory
                .select(recommend.similarity)
                .from(recommend)
                .leftJoin(recommend.lecture, lecture)
                .leftJoin(lecture.users, user)
                .where(user.email.eq(email))
                .fetch();
    }
}
