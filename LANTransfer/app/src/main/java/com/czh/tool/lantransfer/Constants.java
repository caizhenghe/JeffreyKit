package com.czh.tool.lantransfer;

import android.os.Environment;

import java.io.File;

public class Constants {
    public static final int HTTP_PORT = 54321;
    public static final String DIR_NAME = "Transfer";
    public static final File DIR = new File(Environment.getExternalStorageDirectory() + File.separator + DIR_NAME);
    public static final String INDEX_FILE_NAME = "index.html";
    public static final String ENCODE_UTF8 = "utf-8";


    class ContentType{
        public static final String TEXT = "text/html;charset=utf-8";
        public static final String CSS = "text/css;charset=utf-8";
        public static final String BINARY = "application/octet-stream";
        public static final String JS = "application/javascript";
        public static final String PNG = "application/x-png";
        public static final String JPG = "application/jpeg";
        public static final String SWF = "application/x-shockwave-flash";
        public static final String WOFF = "application/x-font-woff";
        public static final String TTF = "application/x-font-truetype";
        public static final String SVG = "image/svg+xml";
        public static final String EOT = "image/vnd.ms-fontobject";
        public static final String MP3 = "audio/mp3";
        public static final String MP4 = "video/mpeg4";
    }

    class Url{
        public static final String RESOURCE_IMAGE = "/images/.*";
        public static final String RESOURCE_SCRIPT = "/script/.*";
        public static final String RESOURCE_CSS = "/css/.*";

        public static final String INDEX = "/";
        public static final String FILE_LIST = "/files";
        public static final String UPLOAD_FILE_CONTENT = "/files";
        public static final String DOWNLOAD_FILE_CONTENT= "/files/.*";


    }

    class ErrorCode {
        public static final int SERVER_DENY = 500;

        public static final int NOT_FOUND = 404;
    }
}
