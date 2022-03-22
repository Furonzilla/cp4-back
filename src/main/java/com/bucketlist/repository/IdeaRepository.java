package com.bucketlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bucketlist.entity.Idea;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
	
}
