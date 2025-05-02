package org.core.moderservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.core.moderservice.model.domain.enums.BlockReason;

import java.io.IOException;
import java.util.Map;

@Converter(autoApply = true)
public class MapToJsonbConverter implements AttributeConverter<Map<BlockReason, Long>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<BlockReason, Long> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при сериализации JSON", e);
        }
    }

    @Override
    public Map<BlockReason, Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory()
                    .constructMapType(Map.class, BlockReason.class, Long.class));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при десериализации JSON", e);
        }
    }
}