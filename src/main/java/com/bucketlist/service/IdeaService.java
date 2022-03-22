package com.bucketlist.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.bucketlist.dto.CreateIdeaDto;
import com.bucketlist.dto.IdeaResponseDto;
import com.bucketlist.dto.ModifyIdeaDto;
import com.bucketlist.dto.UpdateIdeaDto;
import com.bucketlist.entity.Idea;
import com.bucketlist.entity.Picture;
import com.bucketlist.entity.User;
import com.bucketlist.repository.IdeaRepository;
import com.bucketlist.repository.PictureRepository;
import com.bucketlist.repository.UserRepository;

@Service
public class IdeaService {

	@Autowired
	PicturesStorageService picturesStorageService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PictureRepository pictureRepository;

	@Autowired
	IdeaRepository ideaRepository;

	@Autowired
	UserService userService;

	public IdeaResponseDto convertIdeaIntoIdeaResponseDto(Idea idea) {
		IdeaResponseDto ideaResponseDto = new IdeaResponseDto();
		ideaResponseDto.setId(idea.getId());
		ideaResponseDto.setTitle(idea.getTitle());
		ideaResponseDto.setUsername(idea.getUser().getUsername());
		ideaResponseDto.setCreationDate(idea.getCreationDate());
		if (idea.getPicture() != null) {
			ideaResponseDto.setPictureFilename(idea.getPicture().getFilename());
		}
		return ideaResponseDto;
	}

	public List<IdeaResponseDto> convertIdeasIntoListOfIdeaResponseDto(List<Idea> ideas) {

		List<IdeaResponseDto> ideasResponseDto = new ArrayList<>();

		for (Idea idea : ideas) {
			IdeaResponseDto ideaResponseDto = convertIdeaIntoIdeaResponseDto(idea);
			ideasResponseDto.add(ideaResponseDto);
		}
		return ideasResponseDto;
	}

	public List<IdeaResponseDto> getIdeas() {
		List<Idea> ideas = ideaRepository.findAll(Sort.by(Sort.Direction.DESC, "creationDate"));
		return convertIdeasIntoListOfIdeaResponseDto(ideas);
	}

	public ModifyIdeaDto getIdeaByIdForUpdate(Long id) {
		Idea idea = ideaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		User user = userService.getLoggedUser();
		if (!Objects.equals(user.getId(), idea.getUser().getId())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user ID doesn't match idea owner ID");
		}
		ModifyIdeaDto modifyIdeaDto = new ModifyIdeaDto();
		modifyIdeaDto.setId(idea.getId());
		modifyIdeaDto.setTitle(idea.getTitle());
		modifyIdeaDto.setPictureFilename(idea.getPicture().getFilename());

		return modifyIdeaDto;
	}

	public Map<String, String> updateIdeaOfCurrentUserById(UpdateIdeaDto updateIdeaDto, MultipartFile file, Long id)
			throws IllegalStateException, IOException {

		Idea ideaToUpdate = ideaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		User user = userService.getLoggedUser();

		if (!Objects.equals(ideaToUpdate.getUser().getId(), user.getId())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user ID doesn't match idea owner ID");
		}

		ideaToUpdate.setTitle(updateIdeaDto.getTitle());

		// If there is a picture to add...
		if (file != null) {
			// If there is already a picture, delete it physically
			if (ideaToUpdate.getPicture() != null) {
				picturesStorageService.deletePictureByIdeaId(ideaToUpdate.getId());
			}
			// save the new picture and set it as the idea's picture
			String fileName = picturesStorageService.save(file);
			Picture picture = new Picture();
			picture.setFilename(fileName);
			pictureRepository.save(picture);
			ideaToUpdate.setPicture(picture);	
		} else if (Boolean.TRUE.equals(updateIdeaDto.getDeleteFile())) {
			if (ideaToUpdate.getPicture() != null) {
				picturesStorageService.deletePictureByIdeaId(ideaToUpdate.getId());
			}
		}
		ideaRepository.save(ideaToUpdate);
		HashMap<String,String> message = new HashMap<>();
		message.put("message", "Success");
		return message;
	}

	public Map<String, String> createIdea(CreateIdeaDto createIdeaDto, MultipartFile file)
			throws IllegalStateException, IOException {

		Idea idea = new Idea();

		idea.setTitle(createIdeaDto.getTitle());

		User user = userService.getLoggedUser();

		idea.setUser(user);

		// Setting of default parameters
		idea.setCreationDate(new Date());

		// Save the idea without pictures. Indeed, we need an existing idea to
		// save pictures.
		idea = ideaRepository.save(idea);

		// Let's check if there's files...
		if (file != null) {
			// save the new picture and set it as the idea's picture
			String fileName = picturesStorageService.save(file);
			Picture picture = new Picture();
			picture.setFilename(fileName);
			pictureRepository.save(picture);
			idea.setPicture(picture);
			ideaRepository.save(idea);
		}
		HashMap<String,String> message = new HashMap<>();
		message.put("message", "Success");
		return message;
	}

	public Map<String, String> deleteIdeaOfCurrentUserById(Long id) throws IOException {

		Idea idea = ideaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		User user = userService.getLoggedUser();

		// Check if the current User is the creator of idea
		if (!Objects.equals(user.getId(), idea.getUser().getId())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user ID doesn't match idea owner ID");
		}
		picturesStorageService.deletePictureByIdeaId(idea.getId());
		ideaRepository.deleteById(idea.getId());
		HashMap<String,String> message = new HashMap<>();
		message.put("message", "Success");
		return message;
	}

	public List<IdeaResponseDto> getIdeasOfCurrentUser() {
		User user = userService.getLoggedUser();
		List<Idea> ideas = user.getIdeas();
		ideas.sort((Comparator.comparing(Idea::getCreationDate).reversed()));
		return convertIdeasIntoListOfIdeaResponseDto(ideas);
	}

}
