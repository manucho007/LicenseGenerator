package ru.rtksoftlabs.licensegenerator;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public void save(byte[] content, String fileName) throws IOException {
        Path path = Paths.get(fileName);
        Files.write(path, content);
    }
}
