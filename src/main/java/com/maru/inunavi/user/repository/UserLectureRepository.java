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

    UserLecture findByEmailAndLectureIdx(String email, int lectureIdx);

    @Query("select m from UserLecture as m where m.email= :email")
    List<UserLecture> findAllByEmail(
            @Param("email") String email);

    @Modifying
    @Query("delete from UserLecture m where m.email= :email")
    void deleteAllByEmail(String email);
}
