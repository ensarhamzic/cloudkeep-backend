package com.cloudkeep.CloudKeep.config.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class FirebaseStorageStrategy {
    private StorageOptions storageOptions;

    @Value("${firebase.bucket_name}")
    private String bucketName;

    @Value("${firebase.project_id}")
    private String projectId;

    @Value("${firebase.type}")
    private String type;

    @Value("${firebase.project_id}")
    private String project_id;

    @Value("${firebase.private_key_id}")
    private String private_key_id;

    @Value("${firebase.private_key}")
    private String private_key;

    @Value("${firebase.client_email}")
    private String client_email;

    @Value("${firebase.client_id}")
    private String client_id;

    @Value("${firebase.auth_uri}")
    private String auth_uri;

    @Value("${firebase.token_uri}")
    private String token_uri;

    @Value("${firebase.auth_provider_x509_cert_url}")
    private String auth_provider_x509_cert_url;

    @Value("${firebase.client_x509_cert_url}")
    private String client_x509_cert_url;

    @PostConstruct
    public void init() throws Exception {
        InputStream firebaseCredential = createFirebaseCredential();
        this.storageOptions = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(firebaseCredential)).build();
    }

    public Blob uploadFile(MultipartFile file) throws IOException {
        var uuid = UUID.randomUUID().toString();
        BlobId blobId = BlobId.of(bucketName, uuid);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        byte[] bytes = file.getBytes();
        Storage storage = storageOptions.getService();
        storage.create(blobInfo, bytes);
        return storage.create(blobInfo, bytes);
    }


    private InputStream createFirebaseCredential() throws Exception {
        FirebaseCredential firebaseCredential = FirebaseCredential.builder()
                .type(type)
                .project_id(project_id)
                .private_key_id(private_key_id)
                .private_key(private_key.replace("\\n", "\n"))
                .client_email(client_email)
                .client_id(client_id)
                .auth_uri(auth_uri)
                .token_uri(token_uri)
                .auth_provider_x509_cert_url(auth_provider_x509_cert_url)
                .client_x509_cert_url(client_x509_cert_url)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(firebaseCredential);

        return IOUtils.toInputStream(jsonString);
    }

    public void deleteFile(String fileName) {
        Storage storage = storageOptions.getService();
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);
    }
}