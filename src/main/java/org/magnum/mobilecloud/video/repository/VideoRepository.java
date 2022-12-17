package org.magnum.mobilecloud.video.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<Video, Long> {
	
	public Collection<Video> findByName(String title);
	
	public Collection<Video> findByDurationLessThan (long duration);

}
