package com.maru.inunavi.repository;

import com.maru.inunavi.entity.UserLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserLectureRepository extends JpaRepository<UserLecture,Long> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE userlecture AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Query("SELECT m FROM userlecture AS m WHERE m.email= :email AND m.lectureIdx= :lectureIdx")
    UserLecture findByUserEmailAndLectureIdx(
            @Param("email") String email,
            @Param("lectureIdx") int lectureIdx);

    @Query("select m from userlecture as m where m.email= :email")
    List<UserLecture> findAllByEmail(
            @Param("email") String email);

    @Transactional
    @Modifying
    @Query("delete from userlecture m where m.email= :email")
    void deleteAllByEmail(String email);

}
