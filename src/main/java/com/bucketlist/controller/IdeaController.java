package com.bucketlist.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bucketlist.dto.CreateIdeaDto;
import com.bucketlist.dto.IdeaResponseDto;
import com.bucketlist.dto.ModifyIdeaDto;
import com.bucketlist.dto.UpdateIdeaDto;
import com.bucketlist.service.IdeaService;

@RestController
@RequestMapping("/api/ideas")
public class IdeaController {

	@Autowired
	IdeaService ideaService;

	@PreAuthorize("hasRole('USER')")
	@PostMapping(path = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map<String, String> createIdea(@Valid CreateIdeaDto createIdeaDto,
			@RequestParam(name = "files", required = false) MultipartFile picture)
			throws IllegalStateException, IOException {
		return ideaService.createIdea(createIdeaDto, picture);
	}

	@GetMapping("/display")
	public List<IdeaResponseDto> getIdeas() {
		return ideaService.getIdeas();
	}

	@DeleteMapping("/delete-idea/{id}")
	@PreAuthorize("hasRole('USER')")
	public Map<String, String> deleteIdeaOfCurrentUserById(@PathVariable(required = true) Long id) throws IOException {
		return ideaService.deleteIdeaOfCurrentUserById(id);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	public ModifyIdeaDto getIdeaByIdForUpdate(@PathVariable(required = true) Long id) {
		return ideaService.getIdeaByIdForUpdate(id);
	}

	@GetMapping("/current-user")
	@PreAuthorize("hasRole('USER')")
	public List<IdeaResponseDto> getIdeasOfCurrentUser() {
		return ideaService.getIdeasOfCurrentUser();
	}

	@PutMapping("/{id}/update")
	@PreAuthorize("hasRole('USER')")
	public Map<String, String> updateIdeaOfCurrentUserById(@PathVariable(required = true) Long id,
			@Valid UpdateIdeaDto updateIdeaDto,
			@RequestParam(name = "files", required = false) MultipartFile picture)
			throws IllegalStateException, IOException {
		return ideaService.updateIdeaOfCurrentUserById(updateIdeaDto, picture, id);
	}

}
