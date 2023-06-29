package com.maru.inunavi.navi.repository;

import com.maru.inunavi.navi.domain.entity.Node;
import com.maru.inunavi.navi.domain.entity.Path;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Integer>, NodeQueryRepository{

    @Cacheable("latList")
    @Query("select n.lat4326 from Node n")
    List<Double> findAllOfLat();

    @Cacheable("lngList")
    @Query("select n.lng4326 from Node n")
    List<Double> findAllOfLng();

    @Modifying
    @Query(value = "alter TABLE navi AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();
}

