package com.cloudkeep.CloudKeep.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.SingletonManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig implements ApplicationRunner {

    @Value("${cloudinary.url}")
    private String CLOUDINARY_URL;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Setup cloudinary when the application starts
        Cloudinary cloudinary = new Cloudinary(CLOUDINARY_URL);
        SingletonManager cloudinaryManager = new SingletonManager();
        cloudinaryManager.setCloudinary(cloudinary);
        cloudinaryManager.init();
        // end of cloudinary setup
    }
}
