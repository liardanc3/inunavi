package com.maru.inunavi.repository;

import com.maru.inunavi.entity.RecommendTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RecommendTableRepository extends JpaRepository<RecommendTable, Long> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE RecommendTable AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();
}
