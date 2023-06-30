package com.maru.inunavi.navi.repository.place;

import com.maru.inunavi.navi.domain.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceQueryRepository {

    @Query("select p from Place p where p.placeCode= :placeCode")
    Optional<Place> findByPlaceCode(String placeCode);

    @Modifying
    @Query(value = "alter TABLE place AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();
}
