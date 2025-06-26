package org.roly.personalaccountant.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiskService {

    private final ObjectMapper objectMapper;

    @Autowired
    public DiskService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T parseObject(String filename, Class<T> type) throws IOException {
        return objectMapper.readValue(new File(filename), type);
    }

    public void writeToJson(Object object, String filePath) throws IOException {
        objectMapper.writeValue(new File(filePath), object);
    }

}
