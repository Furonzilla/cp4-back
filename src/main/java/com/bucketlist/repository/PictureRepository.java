package com.bucketlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bucketlist.entity.Picture;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

}
