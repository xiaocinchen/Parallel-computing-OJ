package com.offer.oj.util;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class SerializationValidation {
    public static void main(String[] args) {
        FrequencySet<String> set = FrequencySet.of("a", "b", "c");

        try {
            FileOutputStream fileOut = new FileOutputStream("set.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(set.getSortedElements());
            out.close();
            fileOut.close();
            System.out.println("Serialization successful. The class can be serialized.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Serialization failed. The class cannot be serialized.");
        }
    }
}
