package com.maru.inunavi.repository;

import com.maru.inunavi.entity.Navi;
import com.maru.inunavi.entity.NodePath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface NaviRepository extends JpaRepository<Navi, Integer> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE navi AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();


}

