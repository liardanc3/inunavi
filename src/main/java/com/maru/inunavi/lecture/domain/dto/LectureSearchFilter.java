package com.maru.inunavi.lecture.domain.dto;

import lombok.Data;

@Data
public class LectureSearchFilter {

    private String mainKeyword;
    private String keywordOption;
    private String majorOption;
    private String cseOption;
    private String sortOption;
    private String gradeOption;
    private String categoryOption;
    private String scoreOption;

}
