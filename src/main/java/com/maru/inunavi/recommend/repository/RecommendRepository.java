package com.maru.inunavi.recommend.repository;

import com.maru.inunavi.recommend.domain.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Integer> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE recommend AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Transactional
    @Modifying
    @Query("update recommend r set r.similarityString = :similarityString where r.id =:id")
    void updateSimilarityString(int id, String similarityString);
}