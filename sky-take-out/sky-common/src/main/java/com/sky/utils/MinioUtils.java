//package com.sky.utils;
//
//import com.sky.minio.MinioProperties;
//import io.minio.*;
//import io.minio.errors.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.UUID;
//
//
//public class MinioUtils {
//    @Autowired
//    private static MinioProperties properties;
//
//    @Autowired
//    private static MinioClient client;
//
//
//    public static  String upload(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
//        if (!bucketExists) {
//            client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucketName()).build());
//            client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(properties.getBucketName()).config(createBucketPolicyConfig(properties.getBucketName())).build());
//        }
//
//        String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
//        client.putObject(PutObjectArgs.builder().
//                bucket(properties.getBucketName()).
//                object(filename).
//                stream(file.getInputStream(), file.getSize(), -1).
//                contentType(file.getContentType()).build());
//
//        return String.join("/", properties.getEndpoint(), properties.getBucketName(), filename);
//    }
//
//    private static String createBucketPolicyConfig(String bucketName) {
//
//        return """
//                {
//                  "Statement" : [ {
//                    "Action" : "s3:GetObject",
//                    "Effect" : "Allow",
//                    "Principal" : "*",
//                    "Resource" : "arn:aws:s3:::%s/*"
//                  } ],
//                  "Version" : "2012-10-17"
//                }
//                """.formatted(bucketName);
//    }
//}
