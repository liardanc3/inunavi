package com.maru.inunavi.user.repository;

import com.maru.inunavi.user.domain.entity.UserLectureTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UserLectureTableRepository extends JpaRepository<UserLectureTable,Long> {

    UserLectureTable findByEmailAndLectureIdx(String email, int lectureIdx);

    @Query("select m from UserLectureTable as m where m.email= :email")
    List<UserLectureTable> findAllByEmail(
            @Param("email") String email);

    @Modifying
    @Query("delete from UserLectureTable m where m.email= :email")
    void deleteAllByEmail(String email);
}
