package com.maru.inunavi.lecture.repository;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Integer>, LectureQueryRepository {

    Lecture findByNumber(String number);

    @Query("select distinct l.lectureName from Lecture l where l.category = :category and l.grade != :grade and l.department = :department")
    List<String> findDistinctBasicGenerals(String category, String grade, String department);

    @Query("select distinct l.department from Lecture l where l.department not in :generals")
    List<String> findDistinctMajors(String... generals);

    @Query("select distinct l.category from Lecture l")
    List<String> findDistinctCategory();

    // ------------------------- nativeQuery -------------------------- //

    @Modifying(clearAutomatically = true)
    @Query(value = "alter TABLE lecture AUTO_INCREMENT = 1", nativeQuery = true)
    void deleteINCREMENT();
}
