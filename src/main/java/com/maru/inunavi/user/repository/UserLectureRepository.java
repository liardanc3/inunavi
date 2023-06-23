package com.maru.inunavi.user.repository;

import com.maru.inunavi.user.domain.entity.UserLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UserLectureRepository extends JpaRepository<UserLecture,Long> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE userlecture AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Query("SELECT m FROM UserLecture AS m WHERE m.email= :email AND m.lectureIdx= :lectureIdx")
    UserLecture findByUserEmailAndLectureIdx(
            @Param("email") String email,
            @Param("lectureIdx") int lectureIdx);

    @Query("select m from UserLecture as m where m.email= :email")
    List<UserLecture> findAllByEmail(
            @Param("email") String email);

    @Modifying
    @Query("delete from UserLecture m where m.email= :email")
    void deleteAllByEmail(String email);
}
