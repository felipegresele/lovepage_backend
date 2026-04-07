package com.loveapp.love_app_backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {

        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "CLOUD_NAME",
                "api_key", "API_KEY",
                "api_secret", "API_SECRET"));
    }

    public String upload(MultipartFile file) throws Exception {

        Map upload = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        return upload.get("url").toString();

    }
}