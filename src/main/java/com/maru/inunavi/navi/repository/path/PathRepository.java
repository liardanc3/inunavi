package com.maru.inunavi.navi.repository.path;

import com.maru.inunavi.navi.domain.dto.PathDto;
import com.maru.inunavi.navi.domain.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PathRepository extends JpaRepository<Path, Long> {

    @Query("select new com.maru.inunavi.navi.domain.dto.PathDto(p) from Path p where p.query= :query")
    Optional<PathDto> findByRouteInfoQuery(String query);

    @Modifying
    @Query(value = "alter TABLE path AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();
}
