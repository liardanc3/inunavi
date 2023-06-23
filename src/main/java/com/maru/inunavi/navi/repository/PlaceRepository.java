package com.maru.inunavi.navi.repository;

import com.maru.inunavi.navi.domain.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Modifying
    @Query(value = "alter TABLE place AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();

    @Query("select m from place as m where m.placeCode= :nextPlaceCode")
    Place findByPlaceCode(String nextPlaceCode);

}
