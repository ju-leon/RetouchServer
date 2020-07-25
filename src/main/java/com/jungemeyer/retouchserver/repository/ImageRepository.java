package com.jungemeyer.retouchserver.repository;

import com.jungemeyer.retouchserver.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    Optional<Image> findById(String id);
    List<Image> findAll();
    Image findFirstByOrderByLastUpdatedAsc();
    Image findFirstByOrderByEloDesc();
    Image findFirstByOrderByEloAsc();
    Image findFirstByOrderByRandom();
}
