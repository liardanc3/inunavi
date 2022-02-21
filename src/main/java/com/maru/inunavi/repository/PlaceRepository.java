package com.maru.inunavi.repository;

import com.maru.inunavi.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE Place AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Query("select m from Place as m where m.placeCode= :nextPlaceCode")
    Place findByPlaceCode(String nextPlaceCode);
}
