package ru.rtksoftlabs.licensegenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public interface FileService {
    void save(byte[] content, String fileName) throws IOException;
    byte[] load(String fileName) throws IOException;
    FileInputStream loadInputStream(String fileName) throws FileNotFoundException;
    FileOutputStream saveOutputStream(String fileName) throws FileNotFoundException;
}
