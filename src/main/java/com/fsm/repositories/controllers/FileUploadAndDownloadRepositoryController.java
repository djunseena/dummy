package com.fsm.repositories.controllers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fsm.interfaces.Minio;
import io.minio.PutObjectOptions;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.XmlParserException;

@RestController
@RequestMapping("/api")
public class FileUploadAndDownloadRepositoryController extends Minio {

	@PostMapping("/uploadFile")
	public HashMap<String, Object> uploadFile(@RequestParam(value = "file") MultipartFile file)
			throws InvalidEndpointException, InvalidPortException, InvalidKeyException, ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidResponseException, NoSuchAlgorithmException, XmlParserException, IOException,
			ClassNotFoundException {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		try {
			String objectName = "document/" + file.getOriginalFilename();
			PutObjectOptions options = new PutObjectOptions(file.getSize(), -1);
			minio().putObject(bucketName, objectName, file.getInputStream(), options);
			showHashMap.put("Status", HttpStatus.ACCEPTED);
			showHashMap.put("Message", file.getOriginalFilename() + " Succesfully Uploaded");
			showHashMap.put("File Path", objectName);
		} catch (MinioException e) {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Error occurred", e);
		}
		return showHashMap;
	}

	@GetMapping("/downloadFile/{fileName}")
	public HashMap<String, Object> downloadFile(@PathVariable(value = "fileName") String fileName)
			throws InvalidEndpointException, InvalidPortException, InvalidKeyException, ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidResponseException, NoSuchAlgorithmException, XmlParserException, IOException,
			InvalidExpiresRangeException {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		try {
			String objectName = "document/" + fileName;
			String link = minio().presignedGetObject(bucketName, objectName);
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Link", link);
		} catch (MinioException e) {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Error occurred", e);
		}
		return showHashMap;
	}
}
