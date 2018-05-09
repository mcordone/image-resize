package com.trialty.function;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class S3Client {
    private AmazonS3 s3Client;

    public S3Client(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public byte[] getS3ObjectBytesContent(S3EventNotification.S3Entity s3Entity) throws IOException {

        byte[] imgBytes = new byte[0];

        S3Object s3Object = s3Client.getObject(s3Entity.getBucket().getName(), s3Entity.getObject().getKey());
        S3ObjectInputStream contentStream = s3Object.getObjectContent();
        imgBytes = IOUtils.toByteArray(contentStream);

        return imgBytes;
    }

    public void saveToS3(String bucketName, String key, String imageFormat, ByteArrayOutputStream os) {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(os.size());
        metadata.setContentType(AllowedImageFormats.valueOf(imageFormat.toUpperCase()).getMineType());

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        s3Client.putObject(bucketName, key, is, metadata);
    }
}
