package org.roly.personalaccountant.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WriterReader<T> {

    private final ObjectMapper objectMapper;
    private final Class<T> type;

    @Autowired
    public WriterReader(ObjectMapper objectMapper, Class<T> type) {
        this.objectMapper = objectMapper;
        this.type = type;
    }

    protected T parseObject(String content, Class<T> type) throws IOException {
        T fileContent = objectMapper.readValue(content, type);
        return fileContent;
    }

}
