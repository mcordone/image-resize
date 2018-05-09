package com.trialty.function;

import com.trialty.exception.UnsupportedImageException;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ImageManipulator {

    public ByteArrayOutputStream createThumbnail(int w, int h, String imageFormat, InputStream inputStream) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        BufferedImage thumbnail = Thumbnails
                .of(ImageIO.read(inputStream))
                .size(w, h)
                .asBufferedImage();

        ImageIO.write(thumbnail, imageFormat, os);

        return os;
    }

    public String getImageFormat(InputStream inputStream) throws IOException {
        String imageFormat = null;

        ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);

        //get list of readers, if not suitable reader found most likely object is not a valid image
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
        if(!iterator.hasNext()) throw new UnsupportedImageException("Unrecognized image format.");

        //get first reader
        ImageReader reader = (ImageReader) iterator.next();
        imageFormat = reader.getFormatName();

        imageInputStream.close();

        return imageFormat;
    }

    public void validateAllowedImageFormats(String imageFormat) {
        AllowedImageFormats formats = AllowedImageFormats.valueOf(imageFormat.toUpperCase());
        switch (formats.getFormat()){
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                break;
            default: throw new UnsupportedImageException("Unsupported image format, allowed format are jpg, png, and gif only.");

        }
    }
}
