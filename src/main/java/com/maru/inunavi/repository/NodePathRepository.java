package com.maru.inunavi.repository;

import com.maru.inunavi.entity.NodePath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface NodePathRepository extends JpaRepository<NodePath, Long> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE nodepath AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

}
