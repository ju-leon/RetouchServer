package com.jungemeyer.retouchserver.repository;

import com.jungemeyer.retouchserver.model.Image;
import com.jungemeyer.retouchserver.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository  extends JpaRepository<Match, String> {

}
