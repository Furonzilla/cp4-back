package com.bucketlist.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.bucketlist.entity.Idea;
import com.bucketlist.entity.Picture;
import com.bucketlist.repository.IdeaRepository;
import com.bucketlist.repository.PictureRepository;

@Service
public class PicturesStorageService {

	@Value("${com.bucketlist.picture.folder.location}")
	private String pictureFolderLocation;

	@Value("${com.bucketlist.image.quality.after.compression}")
	private float imageQualityAfterCompression;

	@Value("${com.bucketlist.allowed.extensions}")
	private String[] allowedExtensions;

	@Autowired
	PictureRepository pictureRepository;

	@Autowired
	IdeaRepository ideaRepository;

	public ResponseEntity<String> deletePictureByIdeaId(Long id) throws IOException {

		// Check if the idea exists
		Idea ideaToUpdate = ideaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		// Prepare the picture to delete
		Picture pictureToDelete = ideaToUpdate.getPicture();
		// Make the picture to delete orphan
		ideaToUpdate.setPicture(null);

		// Delete the picture physically in the server
		String fileToDelete = pictureFolderLocation + pictureToDelete.getFilename();
		File existFile = new File(fileToDelete);
		Path path = FileSystems.getDefault().getPath(fileToDelete);
		if (!existFile.isDirectory() && existFile.exists()) {
			Files.delete(path);
		}

		// Delete the picture in the database
		pictureRepository.deleteById(pictureToDelete.getId());

		return new ResponseEntity<>("Picture deleted successfully", HttpStatus.OK);
	}

	public String getExtension(MultipartFile file, String[] allowedExtensions) {
		String extension = "";
		String fileName = file.getOriginalFilename();

		if (fileName != null) {
			int index = fileName.lastIndexOf('.');
			if (index > 0) {
				extension = fileName.substring(index + 1);
			}
			if (Arrays.stream(allowedExtensions).noneMatch(extension::equals)) {
				throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, extension + " extension isn't allowed !");
			}
		}
		return extension;
	}

	public File renamePictureWithRandomName(Path path, String extension) {
		File fileToTest;
		do {
			String generatedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
			fileToTest = new File(path.toString() + "/" + generatedName);
		} while (fileToTest.exists());
		return fileToTest;
	}

	public Path getPath() {
		Path path;
		path = Paths.get(this.pictureFolderLocation);
		return path;
	}

	public String save(MultipartFile file) throws IllegalStateException, IOException {

		Path path = getPath();

		String extension = getExtension(file, allowedExtensions);

		File fileToTest = renamePictureWithRandomName(path, extension);

		byte[] compressedImage = this.compressImage(file);
		InputStream image = new ByteArrayInputStream(compressedImage);

		Files.copy(image, path.resolve(fileToTest.getName()));
		return fileToTest.getName();
	}

	public byte[] compressImage(MultipartFile image) throws IOException {

		InputStream inputStream = image.getInputStream();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// Could be set in application.properties (default value is 0.3f)
		float imageQuality = imageQualityAfterCompression;

		// Create the buffered image
		BufferedImage bufferedImage = ImageIO.read(inputStream);

		// Get image writers
		Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("png"); // Input your Format Name here

		if (!imageWriters.hasNext())
			throw new IllegalStateException("Writers not found");

		ImageWriter imageWriter = imageWriters.next();
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
		imageWriter.setOutput(imageOutputStream);

		ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

		// Set the compress quality metrics
		imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		imageWriteParam.setCompressionQuality(imageQuality);

		// Compress and insert the image into the byte array.
		imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);

		byte[] imageBytes = outputStream.toByteArray();

		// close all streams
		inputStream.close();
		outputStream.close();
		imageOutputStream.close();
		imageWriter.dispose();

		return imageBytes;
	}

	public Resource load(String filename) {
		try {
			Path path = getPath();

			Path file = path.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file : " + filename);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error : " + e.getMessage());
		}
	}

}
