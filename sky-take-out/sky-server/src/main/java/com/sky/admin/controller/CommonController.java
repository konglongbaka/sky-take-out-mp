package com.sky.admin.controller;

import com.sky.minio.MinioProperties;
import com.sky.result.Result;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")

public class CommonController {

    @Autowired
    private MinioProperties properties;

    @Autowired
    private MinioClient client;

    @RequestMapping("/upload")
    public Result<String> upload(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
        if (!bucketExists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucketName()).build());
            client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(properties.getBucketName()).config(createBucketPolicyConfig(properties.getBucketName())).build());
        }

        String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        client.putObject(PutObjectArgs.builder().
                bucket(properties.getBucketName()).
                object(filename).
                stream(file.getInputStream(), file.getSize(), -1).
                contentType(file.getContentType()).build());
        String url = String.join("/", properties.getEndpoint(), properties.getBucketName(), filename);
        return Result.success(url);
    }

    private String createBucketPolicyConfig(String bucketName) {

        return """
                {
                  "Statement" : [ {
                    "Action" : "s3:GetObject",
                    "Effect" : "Allow",
                    "Principal" : "*",
                    "Resource" : "arn:aws:s3:::%s/*"
                  } ],
                  "Version" : "2012-10-17"
                }
                """.formatted(bucketName);
    }

}
