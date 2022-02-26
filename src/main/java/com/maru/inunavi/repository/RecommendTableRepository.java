package com.maru.inunavi.repository;

import com.maru.inunavi.entity.RecommendTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RecommendTableRepository extends JpaRepository<RecommendTable, Integer> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE RecommendTable AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Modifying
    @Query("update RecommendTable r set r.similarityString = :similarityString where r.id =:id")
    void updateSimilarityString(int id, String similarityString);
}
