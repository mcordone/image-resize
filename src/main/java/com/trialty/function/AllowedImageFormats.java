package com.trialty.function;

public enum AllowedImageFormats {
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    GIF("gif", "image/gif");

    private final String format;
    private final String mineType;

    AllowedImageFormats(String format, String mineType) {
        this.format = format;
        this.mineType = mineType;
    }

    public String getFormat() {
        return format;
    }

    public String getMineType() {
        return mineType;
    }
}
