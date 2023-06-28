package com.maru.inunavi.navi.repository;

import com.maru.inunavi.navi.domain.entity.Navi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface NaviRepository extends JpaRepository<Navi, Integer> {





    @Modifying
    @Query(value = "alter TABLE navi AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();
}

