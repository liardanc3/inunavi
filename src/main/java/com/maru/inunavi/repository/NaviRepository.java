package com.maru.inunavi.repository;

import com.maru.inunavi.entity.Navi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NaviRepository extends JpaRepository<Navi, Long> {

}
