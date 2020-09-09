package com.taras.hnativ.usermanager.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public final class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {}


    public static String getResourceFileContent(String fileName) throws IOException {
        File file = getResourceFile(fileName);
        return new String(Files.readAllBytes(file.toPath()));
    }

    public static File getResourceFile(String fileName) throws IOException {
        URL fileURL = FileUtils.class
                .getClassLoader().getResource(fileName);
        if (fileURL == null) {
            String msg = "Cannot load file: " + fileName;
            log.error(msg);
            throw new IOException(msg);
        }
        return new File(fileURL.getFile());
    }



}
