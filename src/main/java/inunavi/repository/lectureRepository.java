package inunavi.repository;

import inunavi.entity.lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface lectureRepository extends JpaRepository<lecture,Long> {

    @Transactional
    @Modifying
    @Query(value = "alter TABLE lecture_Table AUTO_INCREMENT = 1",nativeQuery = true)
    void deleteINCREMENT();



}
