package com.maru.inunavi.repository;

import com.maru.inunavi.entity.UserLecture;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserLectureRepository extends JpaRepository<UserLecture,Long> {

    @Query("SELECT m FROM UserLecture AS m WHERE m.UserID= :_UserID AND m.LectureID= :_LectureID")
    UserLecture findByUserIDAndLectureID(
            @Param("_UserID") String userID,
            @Param("_LectureID") String lectureID);
}
