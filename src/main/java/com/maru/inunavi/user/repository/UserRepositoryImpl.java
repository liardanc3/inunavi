package com.maru.inunavi.user.repository;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.maru.inunavi.lecture.domain.entity.QLecture.lecture;
import static com.maru.inunavi.user.domain.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Lecture> findNextLecture(String email, int nowTime) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(lecture)
                        .leftJoin(lecture.users, user)
                        .fetchJoin()
                        .where(
                                user.email.eq(email),
                                lecture.classTime.ne("-"),
                                Expressions.numberTemplate(
                                        Integer.class,
                                        "FLOOR(SUBSTRING_INDEX(SUBSTRING_INDEX({0}, ',', 1), '-', 1) / 48)",
                                        getStartTime()
                                ).eq(nowTime / 48),
                                getStartTime().gt(nowTime)
                        )
                        .orderBy(
                                Expressions.numberTemplate(
                                        Integer.class,
                                        "{0} - {1}",
                                        getStartTime(),
                                        nowTime
                                ).asc()
                        )
                        .limit(1)
                        .fetchOne()
        );
    }

    private NumberTemplate<Integer> getStartTime() {
        return Expressions.numberTemplate(
                Integer.class,
                "{0}",
                Expressions.stringTemplate(
                        "SUBSTRING_INDEX({0}, '-', {1})",
                        Expressions.stringTemplate("SUBSTRING_INDEX({0}, ',', {1})", lecture.classTime, 1),
                        1
                )
        );
    }
}
