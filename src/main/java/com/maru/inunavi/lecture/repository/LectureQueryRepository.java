package com.maru.inunavi.lecture.repository;

import com.maru.inunavi.lecture.domain.dto.SearchFilter;
import com.maru.inunavi.lecture.domain.dto.SelectLectureDto;

import java.util.List;

public interface LectureQueryRepository {
    List<SelectLectureDto> findBySearchFilter(SearchFilter searchFilter);
}
