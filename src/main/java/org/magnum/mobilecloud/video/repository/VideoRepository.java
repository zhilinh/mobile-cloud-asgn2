package org.magnum.mobilecloud.video.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface VideoRepository extends CrudRepository<Video, Long> {

    Video findVideoById(Long id);

    Collection<Video> findVideosByName(String name);

    Collection<Video> findVideosByDurationLessThan(Long duration);

}
