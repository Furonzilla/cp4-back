package com.bucketlist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bucketlist.repository.PictureRepository;
import com.bucketlist.service.PicturesStorageService;


@RestController
@RequestMapping("/api/files")
public class PictureController {

	@Autowired
	PicturesStorageService picturesStorageService;

	@Autowired
	PictureRepository pictureRepository;

	// Used to display each product picture and profile picture in front-end template
	@GetMapping("/pictures/{filename:.+}")
	public ResponseEntity<Resource> getPictureForDisplay(@PathVariable(required = true) String filename) {
		Resource file = picturesStorageService.load(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
}
