package com.offer.oj.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JudgeUtil {
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
                return false;
            }
        }

        return true;
    }


}
