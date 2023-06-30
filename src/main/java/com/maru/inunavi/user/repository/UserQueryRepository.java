package com.maru.inunavi.user.repository;

import com.maru.inunavi.lecture.domain.entity.Lecture;

import java.util.Optional;

public interface UserQueryRepository {

    Optional<Lecture> findNextLecture(String email, int nowTime);

}
