package com.maru.inunavi.repository;

import com.maru.inunavi.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Query("select m from UserInfo AS m where m.UserID= :id")
    UserInfo findById(
            @Param("id") String id
    );

    @Query("select m from UserInfo AS m where m.UserID= :id and m.UserPW= :password")
    UserInfo findByIdAndUserPW(
            @Param("id") String id,
            @Param("password") String password);


}
