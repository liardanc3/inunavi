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

    @Query("SELECT m FROM UserLecture AS m WHERE m.UserID= :_UserID AND m.LectureID= :_LectureID")
    UserLecture findByUserIDAndLectureID(
            @Param("_UserID") String _UserID,
            @Param("_LectureID") String _LectureID);

    @Query("select m from UserLecture as m where m.UserID= :_UserID")
    List<UserLecture> findAllByUserID(
            @Param("_UserID") String _UserID);


}
