package com.maru.inunavi.repository;

import com.maru.inunavi.entity.UserLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface UserLectureRepository extends JpaRepository<UserLecture,Long> {

    @Query("SELECT m FROM UserLecture AS m WHERE m.email= :email AND m.lectureId= :lectureId")
    UserLecture findByUserEmailAndLectureID(
            @Param("email") String email,
            @Param("lectureId") String lectureId);

    @Query("select m from UserLecture as m where m.email= :email")
    List<UserLecture> findAllByUserEmail(
            @Param("email") String email);


}
