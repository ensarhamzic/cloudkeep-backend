package com.cloudkeep.CloudKeep.config.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

@Service
public class FirebaseStorageStrategy {
    private final Logger log = LoggerFactory.getLogger(FirebaseStorageStrategy.class);
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

    public Blob uploadFile(MultipartFile multipartFile) throws IOException {
        File file = convertMultiPartToFile(multipartFile);
        Path filePath = file.toPath();
        String objectName = generateFileName(multipartFile);

        Storage storage = storageOptions.getService();

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        return storage.create(blobInfo, Files.readAllBytes(filePath));
    }

    public ResponseEntity<Object> downloadFile(String fileName, HttpServletRequest request) throws Exception {
        Storage storage = storageOptions.getService();

        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        ReadChannel reader = blob.reader();
        InputStream inputStream = Channels.newInputStream(reader);

        byte[] content = null;
        log.info("File downloaded successfully.");

        content = IOUtils.toByteArray(inputStream);

        final ByteArrayResource byteArrayResource = new ByteArrayResource(content);

        return ResponseEntity
                .ok()
                .contentLength(content.length)
                .header("Content-type", "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(byteArrayResource);

    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
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

        var inputStream = IOUtils.toInputStream(jsonString);
        return inputStream;
    }
}