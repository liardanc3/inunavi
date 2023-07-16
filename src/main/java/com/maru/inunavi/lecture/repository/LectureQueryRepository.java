package com.maru.inunavi.lecture.repository;

import com.maru.inunavi.lecture.domain.dto.LectureSearchFilter;
import com.maru.inunavi.lecture.domain.dto.SelectLectureDto;

import java.util.List;
import java.util.Optional;

public interface LectureQueryRepository {
    List<SelectLectureDto> findBySearchFilter(LectureSearchFilter lectureSearchFilter);
}
