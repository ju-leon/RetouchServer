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

    List<Image> findAllByGender(String gender);

    List<Image> findAllByGenderOrderByEloDesc(String gender);

    List<Image> findAllByGenderOrderByEloAsc(String gender);

    Image findFirstByOrderByLastUpdatedAsc();

    Image findFirstByOrderByEloDesc();

    Image findFirstByOrderByEloAsc();

    Image findFirstByGenderOrderByEloAsc(String gender);

    Image findFirstByOrderByRandom();

    Image findFirstByGenderOrderByRandom(String gender);

    List<Image> findAllByOrderByEloAsc();

    Image findFirstByGenderOrderByLastUpdatedAsc(String gender);
}
