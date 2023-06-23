package com.maru.inunavi.navi.repository;

import com.maru.inunavi.navi.domain.entity.NodePath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface NodePathRepository extends JpaRepository<NodePath, Long> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE nodepath AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Query("select t from nodepath as t where t.Query= :query")
    NodePath findByQuery(String query);

}
