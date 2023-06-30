package com.maru.inunavi.user.repository;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.maru.inunavi.lecture.domain.entity.QLecture.lecture;
import static com.maru.inunavi.user.domain.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Lecture> findNextLecture(String email, int nowTime) {
        return Optional
                .ofNullable(queryFactory
                        .selectFrom(lecture)
                        .leftJoin(lecture.users, user)
                        .fetchJoin()
                        .where(
                                user.email.eq(email),
                                Expressions.asNumber(getStartTime(nowTime))
                                        .eq(-1)
                                        .not()
                        )
                        .orderBy(
                                Expressions.numberTemplate(Integer.class, "{0} - {1}", getStartTime(nowTime), nowTime)
                                        .asc()
                        )
                        .limit(1)
                        .fetchOne());
    }

    private Integer getStartTime(int nowTime){
        return Optional.ofNullable(
                Arrays.stream(lecture.classTime.toString().split(","))
                        .map(range -> Integer.parseInt(range.split("-")[0]))
                        .filter(startTime -> startTime / 48 == nowTime / 48 && nowTime <= startTime)
                        .collect(Collectors.toList())
                        .get(0)
                )
                .orElse(-1);
    }
}
