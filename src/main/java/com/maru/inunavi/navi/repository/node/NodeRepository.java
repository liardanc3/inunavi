package com.maru.inunavi.navi.repository.node;

import com.maru.inunavi.navi.domain.entity.Node;
import com.maru.inunavi.navi.repository.node.NodeQueryRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Integer>, NodeQueryRepository {

    @Cacheable("latList")
    @Query("select n.lat4326 from Node n")
    List<Double> findAllOfLat();

    @Cacheable("lngList")
    @Query("select n.lng4326 from Node n")
    List<Double> findAllOfLng();

    @Query("select n.id from Node n where n.placeCode like %:placeCode%")
    List<Integer> findIdByPlaceCode(String placeCode);

    // ---------------------------------------- //

    @Modifying
    @Query(value = "alter TABLE node AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

}

