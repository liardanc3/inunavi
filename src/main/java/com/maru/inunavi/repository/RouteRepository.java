package com.maru.inunavi.repository;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.NodePath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RouteRepository extends JpaRepository<NodePath, Long> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE nodepath AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Query("select t from nodepath as t where t.Src2dst= :src2dst")
    NodePath findBySrc2dst(
            @Param("src2dst") String src2dst);

}
