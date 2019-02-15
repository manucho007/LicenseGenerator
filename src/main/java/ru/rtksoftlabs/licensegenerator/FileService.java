package ru.rtksoftlabs.licensegenerator;

import java.io.IOException;

public interface FileService {
    void save(byte[] content, String fileName) throws IOException;
}
