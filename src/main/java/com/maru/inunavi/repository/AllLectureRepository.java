package com.maru.inunavi.repository;

import com.maru.inunavi.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AllLectureRepository extends JpaRepository<Lecture,Long> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE all_lecture AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Query(value = "select t from AllLecture t order by t.number ASC")
    List<Lecture> findAllByOrderByNumberAsc();

    @Query(value = "select t from AllLecture t order by t.lecturename ASC")
    List<Lecture> findAllByOrderByLecturenameAsc();

    @Query("select t from AllLecture as t where t.number= :_number")
    Lecture findByLectureId(
            @Param("_number") String _number);
}
