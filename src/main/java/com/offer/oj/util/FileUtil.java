package com.offer.oj.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class FileUtil {
    public static boolean compareFiles(String filePath1, String filePath2) {
        List<String> file1Lines, file2Lines;
        try {
            file1Lines = Files.readAllLines(Paths.get(filePath1));
            file2Lines = Files.readAllLines(Paths.get(filePath2));
        } catch (Exception e) {
            throw new RuntimeException("ReadFile Exception.", e);
        }

        if (file1Lines.size() != file2Lines.size()) {
            return false;
        }

        for (int i = 0; i < file1Lines.size(); i++) {
            if (!file1Lines.get(i).trim().equals(file2Lines.get(i).trim())) {
                log.info("Files are different! "+file1Lines.get(i).trim()+" "+file2Lines.get(i).trim());
                return false;
            }
        }

        return true;
    }

    public static File getDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean result = file.mkdirs();
            if (result) {
                log.info("dir create success {}", path);
            } else {
                throw new RuntimeException("dir create exception {}" + path);
            }
        }
        return file;
    }
}