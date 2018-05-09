package com.trialty.function;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.trialty.exception.UnsupportedImageException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageResize implements RequestHandler<S3Event, String> {
    private static final int SMALL = 80;
    private static final int MEDIUM = 120;
    private static final String S3_DESTINATION_BUCKET = "test-sftp-001";

    private Map<String, Map<String, String>> sizes = new HashMap<>();
    private LambdaLogger logger;
    private S3Client s3Client;
    private ImageManipulator imageManipulator;
    private S3EventNotification.S3Entity s3ObjectEntity;

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        logger = context.getLogger();
        parseFunctionEnvironmentVariables();

        try{
            s3Client = new S3Client(AmazonS3ClientBuilder.standard().build());
            imageManipulator = new ImageManipulator();
            s3ObjectEntity = s3Event.getRecords().get(0).getS3();

            //get S3Object (image) binary content
            byte[] imgBytes = s3Client.getS3ObjectBytesContent(s3ObjectEntity);

            //get image type from S3Object content is an actual image.
            String imageFormat = imageManipulator.getImageFormat(new ByteArrayInputStream(imgBytes));
            logger.log("Image Format: " + imageFormat);

            //validate allowed image formats, only jpg, jpeg, png, and gif images allowed
            imageManipulator.validateAllowedImageFormats(imageFormat);

            //create thumbnails
            ByteArrayOutputStream thumbnailData = imageManipulator.createThumbnail(MEDIUM, MEDIUM, imageFormat, new ByteArrayInputStream(imgBytes));

            //save thumbnail image back to S3
            s3Client.saveToS3(S3_DESTINATION_BUCKET, s3ObjectEntity.getObject().getKey(), imageFormat, thumbnailData);

        }
        catch (AmazonServiceException e){
            logger.log("AWS Service Error: " + e.getMessage());
            e.printStackTrace();
        }
        catch (SdkClientException e) {
            logger.log("AWS SDK Client Error: " + e.getMessage());
            e.printStackTrace();
        }
        catch(UnsupportedImageException e){
            logger.log("Image Error: " + e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e) {
            logger.log("IO Error: " + e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e){
            logger.log("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private void parseFunctionEnvironmentVariables() {
        Map<String, String> envVariables = System.getenv();

        if(envVariables.containsKey("smallSizeWidth") && envVariables.containsKey("smallSizeHeight")) {
            Map smallSizeMap = Collections.singletonMap(envVariables.get("smallSizeWidth"), envVariables.get("smallSizeHeight"));
            sizes.put("small", smallSizeMap);
        }

        if(envVariables.containsKey("mediumSizeWidth") && envVariables.containsKey("mediumSizeHeight")) {
            Map mediumSizeMap = Collections.singletonMap(envVariables.get("mediumSizeWidth"), envVariables.get("mediumSizeHeight"));
            sizes.put("medium", mediumSizeMap);
        }

        if(envVariables.containsKey("largeSizeWidth") && envVariables.containsKey("largeSizeHeight")) {
            Map bigSizeMap = Collections.singletonMap(envVariables.get("largeSizeWidth"), envVariables.get("largeSizeHeight"));
            sizes.put("large", bigSizeMap);
        }

        /*logger.log("Env Width: " + System.getenv("smallSizeWidth"));
        logger.log("Env Height: " + System.getenv("smallSizeHeight"));
        createDefaultSizes();*/
    }

    private void createDefaultSizes() {
        //sizes.put("thumb", new HashMap<>(Arrays.asList(150, 150)));
        //sizes.put("medium", new HashMap<>(Arrays.asList(300, 300)));
        //sizes.put("large", new HashMap<>(Arrays.asList(640, 480)));
    }

}
