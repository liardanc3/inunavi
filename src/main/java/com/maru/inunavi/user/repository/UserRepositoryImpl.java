package com.maru.inunavi.user.repository;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.domain.entity.QLecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.maru.inunavi.lecture.domain.entity.QLecture.lecture;
import static com.maru.inunavi.user.domain.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserQueryRepository{

    private final JPAQueryFactory queryFactory;


    // TODO - query 확인 필요
    @Override
    public Optional<Lecture> findNextLecture(String email, int nowTime) {
        return Optional
                .ofNullable(queryFactory
                        .selectFrom(lecture)
                        .leftJoin(lecture.users, user)
                        .fetchJoin()
                        .where(
                                user.email.eq(email),
                                lectureDayEq(nowTime),
                                lectureStartAfter(nowTime)
                        )
                        .orderBy(timeGap(nowTime))
                        .limit(1)
                        .fetchOne());
    }

    private OrderSpecifier<Integer> timeGap(int nowTime){
        int nextTime = Integer.parseInt(String.valueOf(lecture.classTime.substring(0, 3)));

        return Expressions.numberTemplate(Integer.class, "{0} - {1}", nextTime, nowTime).asc();
    }

    private BooleanExpression lectureStartAfter(int nowTime) {
        int nextTime = Integer.parseInt(String.valueOf(lecture.classTime.substring(0, 3)));

        return Expressions.numberTemplate(Integer.class, "{0}", nowTime)
                .loe(Expressions.numberTemplate(Integer.class, "{1}", nextTime));
    }

    private BooleanExpression lectureDayEq(int nowTime) {
        int nextTime = Integer.parseInt(String.valueOf(lecture.classTime.substring(0, 3)));

        return Expressions.numberTemplate(Integer.class, "({0}/48)", nowTime)
                .eq(Expressions.numberTemplate(Integer.class, "({0}/48)", nextTime));
    }
}
