package org.roly.personalaccountant.domain.model.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiskService {

    private final ObjectMapper objectMapper;

    @Autowired
    public DiskService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T readSave(String filePath, Class<T> type) throws IOException {

        try (InputStream is = Files.newInputStream(Path.of(filePath))) {
            return objectMapper.readValue(is, type);
        }
    }

    public void writeToJson(Object object, String filePath) throws IOException {
        try (OutputStream os = Files.newOutputStream(Path.of(filePath))) {
            objectMapper.writeValue(os, object);
        }
    }

}
