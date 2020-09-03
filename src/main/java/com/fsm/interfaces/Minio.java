package com.fsm.interfaces;



import org.springframework.beans.factory.annotation.Value;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;

public class Minio {
	
	@Value("${minio.bucketName}")
	protected String bucketName;
	@Value("${minio.username}")
	protected String userName;
    @Value("${minio.password}")
    protected String password;
    @Value("${minio.url}")
    protected String url;
	
	public MinioClient minio() throws InvalidEndpointException, InvalidPortException{
		MinioClient minioClient = new MinioClient(url, userName, password);
		return minioClient;
	}
	
}
