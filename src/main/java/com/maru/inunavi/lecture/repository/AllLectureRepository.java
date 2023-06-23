package com.maru.inunavi.lecture.repository;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AllLectureRepository extends JpaRepository<Lecture, Integer> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE lecture AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Query(value = "select t from lecture t order by t.number ASC")
    List<Lecture> findAllByOrderByNumberAsc();

    @Query(value = "select t from lecture t order by t.lecturename ASC")
    List<Lecture> findAllByOrderByLecturenameAsc();

    @Query("select t from lecture as t where t.number= :_number")
    Lecture findByLectureId(
            @Param("_number") String _number);
}
