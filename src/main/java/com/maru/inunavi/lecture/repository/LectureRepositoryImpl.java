package com.maru.inunavi.lecture.repository;

import com.maru.inunavi.lecture.domain.dto.QSelectLectureDto;
import com.maru.inunavi.lecture.domain.dto.SearchFilter;
import com.maru.inunavi.lecture.domain.dto.SelectLectureDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static com.maru.inunavi.lecture.domain.entity.QLecture.lecture;

@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SelectLectureDto> findBySearchFilter(SearchFilter searchFilter) {
        return queryFactory
                .select(new QSelectLectureDto(
                        lecture.id.stringValue(),
                        lecture.department,
                        lecture.grade,
                        lecture.category,
                        lecture.number,
                        lecture.lectureName,
                        lecture.professor,
                        lecture.classRoomRaw,
                        lecture.classTimeRaw,
                        lecture.classRoom,
                        lecture.classTime,
                        lecture.how,
                        lecture.point
                ))
                .from(lecture)
                .where(
                        gradeCondition(searchFilter.getGradeOption()),
                        cseCondition(searchFilter.getCseOption(), searchFilter.getMajorOption()),
                        majorCondition(searchFilter.getMajorOption()),
                        scoreCondition(searchFilter.getScoreOption()),
                        categoryCondition(searchFilter.getCategoryOption()),
                        keywordCondition(searchFilter.getMainKeyword(), searchFilter.getKeywordOption())
                )
                .orderBy(orderCondition(searchFilter.getSortOption()))
                .fetch();
    }

    // -------------------------- CONDITION ---------------------------------- //

    private OrderSpecifier<String> orderCondition(String sortOption){
        if(sortOption.equals("과목코드")){
            return lecture.number.asc();
        }
        if(sortOption.equals("과목명")){
            return lecture.lectureName.asc();
        }
        return null;
    }

    private BooleanExpression gradeCondition(String gradeOption){
        if(gradeOption.equals("전체")){
            return null;
        }

        List<String> targetGrade = List.of("전학년");

        StringTokenizer gradeToken = new StringTokenizer(gradeOption);
        while(gradeToken.hasMoreTokens()){
            String grade = gradeToken.nextToken(", ");

            targetGrade.add(grade.substring(0, 1));
        }

        return lecture.grade.in(targetGrade);
    }


    private BooleanExpression cseCondition(String cseOption, String majorOption){
        if(cseOption.equals("전체")){
            return null;
        }

        BooleanExpression booleanExpression = lecture.category.eq("교양필수");

        if(!cseOption.equals("기타")){
            booleanExpression.and(lecture.lectureName.contains(cseOption));
        }
        if(cseOption.equals("기타") && !majorOption.equals("전체")){
            booleanExpression.and(lecture.department.eq(majorOption));
        }

        return booleanExpression;
    }

    private BooleanExpression majorCondition(String majorOption){
        return !majorOption.equals("전체") ? lecture.department.eq(majorOption) : null;
    }

    private BooleanExpression scoreCondition(String scoreOption){
        if(scoreOption.equals("전체")){
            return null;
        }
        
        List<String> targetScore = new ArrayList<>();

        StringTokenizer scoreToken = new StringTokenizer(scoreOption);
        while(scoreToken.hasMoreTokens()){
            String score = scoreToken.nextToken(", ");

            targetScore.add(score.substring(0, 1));
        }

        return lecture.point.in(targetScore);
    }
    
    private BooleanExpression categoryCondition(String categoryOption){
        if(categoryOption.equals("전체")){
            return null;
        }
        
        Set<String> targetCategory = new HashSet<>();

        StringTokenizer categoryToken = new StringTokenizer(categoryOption);
        while(categoryToken.hasMoreTokens()){
            String category = categoryToken.nextToken(", ");

            targetCategory.add(category.substring(0, 1));
        }

        return lecture.category.in(targetCategory);
    }

    private BooleanExpression keywordCondition(String mainKeyword, String keywordOption) {
        if(mainKeyword.equals("")){
            return null;
        }

        BooleanExpression booleanExpression = null;

        if(keywordOption.equals("전체")){
            booleanExpression
                    .or(lecture.lectureName.toUpperCase().contains(mainKeyword.toUpperCase()))
                    .or(lecture.professor.contains(mainKeyword))
                    .or(lecture.number.contains(mainKeyword));
        }

        if(keywordOption.equals("과목명")){
            booleanExpression
                    .or(lecture.lectureName.toUpperCase().contains(mainKeyword.toUpperCase()));
        }
        else if(keywordOption.equals("교수명")){
            booleanExpression
                    .or(lecture.professor.contains(mainKeyword));
        }
        else if(keywordOption.equals("과목코드")){
            booleanExpression
                    .or(lecture.number.contains(mainKeyword));
        }

        return booleanExpression;
    }

}
