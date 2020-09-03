package com.fsm.repositories.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.interfaces.Minio;
import com.fsm.models.Dispatch;
import com.fsm.models.TroubleTicket;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.TroubleTicketRepository;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.XmlParserException;

@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("api")
public class DownloadAttachmentRepositoryController extends Minio {

	@Autowired
	private DispatchRepository dispatchRepository;

	@Autowired
	private TroubleTicketRepository troubleTicketRepository;

	private String baseFolder = "/";

	@GetMapping("/downloadAttachmentUrl")
	public String viewObject(@RequestParam(value = "orderId") Long orderId) throws InvalidEndpointException,
			InvalidPortException, InvalidKeyException, ErrorResponseException, IllegalArgumentException,
			InsufficientDataException, InternalException, InvalidBucketNameException, InvalidResponseException,
			NoSuchAlgorithmException, XmlParserException, IOException, InvalidExpiresRangeException {

		Dispatch dispatch = dispatchRepository.findById(orderId).orElse(null);
		TroubleTicket troubleTicket = troubleTicketRepository.findById(dispatch.getTicketId().getTicketId())
				.orElse(null);

		String filePath = troubleTicket.getFilePath();

		return minio().presignedGetObject(bucketName, filePath);
	}

	@GetMapping(path = "/downloadAttachment")
	public ResponseEntity<ByteArrayResource> uploadFile(@RequestParam(value = "orderId") Long orderId)
			throws IOException {

		Dispatch dispatch = dispatchRepository.findById(orderId).orElse(null);
		TroubleTicket troubleTicket = troubleTicketRepository.findById(dispatch.getTicketId().getTicketId())
				.orElse(null);

		String filePath = troubleTicket.getFilePath();

		byte[] data = getFile(filePath);
		ByteArrayResource resource = new ByteArrayResource(data);

		return ResponseEntity.ok().contentLength(data.length).header("Content-type", "application/octet-stream")
				.header("Content-disposition", "attachment; filename=\"" + filePath + "\"").body(resource);

	}

	public byte[] getFile(String key) {
		try {
			InputStream obj = minio().getObject(bucketName, baseFolder + "/" + key);

			byte[] content = IOUtils.toByteArray(obj);
			obj.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}