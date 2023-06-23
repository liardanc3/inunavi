package com.maru.inunavi.lecture.repository;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    List<Lecture> findAllByOrderByNumberAsc();
    List<Lecture> findAllByOrderByLectureNameAsc();

    Lecture findByNumber(String number);

    @Modifying
    @Query(value = "alter TABLE lecture AUTO_INCREMENT = 1", nativeQuery = true)
    void deleteINCREMENT();
}
