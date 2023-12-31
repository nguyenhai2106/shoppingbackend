package com.donations.admin;

import java.io.ByteArrayInputStream;
import org.springframework.core.io.Resource;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

 @Configuration
 public class GoogleCloudStorageConfig {
 	@Bean
 	public Storage googleCloudStorage() throws IOException {
 		String credentialsFilePath = System.getenv("GCS_CREDENTIALS_JSON");
 		String projectId = System.getenv("GCS_PROJECT_ID");
 		Resource credentialsResource = new FileSystemResource(credentialsFilePath);
 		Credentials credentials = GoogleCredentials.fromStream(credentialsResource.getInputStream());
 		return StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
 	}
 }

//@Configuration
//public class GoogleCloudStorageConfig {
//	@Bean
//	Storage googleCloudStorage() throws IOException {
//		String credentialsJson = System.getenv("GCS_CREDENTIALS_JSON");
//		String projectId = System.getenv("GCS_PROJECT_ID");
//
//		Credentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(credentialsJson.getBytes()));
//		return StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
//	}
//}