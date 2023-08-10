package com.offer.oj.util;

import com.offer.oj.domain.dto.CompareFileDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class FileUtil {
    public static CompareFileDTO compareFiles(String filePath1, String filePath2) {
        CompareFileDTO result = new CompareFileDTO();
        List<String> file1Lines, file2Lines;
        try {
            file1Lines = Files.readAllLines(Paths.get(filePath1));
            file2Lines = Files.readAllLines(Paths.get(filePath2));
        } catch (Exception e) {
            throw new RuntimeException("ReadFile Exception.", e);
        }


        for (int i = 0; i < file1Lines.size(); i++) {
            String file2Line = "";
            if (file2Lines.size() > i) {
                file2Line = file2Lines.get(i).trim();
            }
            if (!file1Lines.get(i).trim().equals(file2Line)) {
                result.setSame(false);
                result.setDifferentLineNumber(i+1);
                result.setDifferentLineContent(file1Lines.get(i).trim() + " " + file2Line);
                return result;
            }
        }
        result.setSame(true);
        return result;
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