package com.maru.inunavi.user.repository;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.user.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {

    User findByEmail(@Param("email") String email);

    @Query("select l from User u join fetch Lecture l where u.email = :email")
    List<Lecture> findLecturesByEmail(String email);

    @Query("select l from User u join fetch Lecture l " +
            "where u.email = :email and l.classRoom <> '-' and l.placeCode not like '%ZZ%' and l.placeCode not like '%NC%'")
    Optional<List<Lecture>> findOfflineLecturesByEmail(String email);
}
