package com.maru.inunavi.user.repository;

import com.maru.inunavi.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<User, Long> {

    @Query("select m from User as m where m.email= :email")
    User findByEmail(
            @Param("email") String email
    );


}
