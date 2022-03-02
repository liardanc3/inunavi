package com.maru.inunavi.repository;

import com.maru.inunavi.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Query("select m from userinfo as m where m.email= :email")
    UserInfo findByEmail(
            @Param("email") String email
    );


}
