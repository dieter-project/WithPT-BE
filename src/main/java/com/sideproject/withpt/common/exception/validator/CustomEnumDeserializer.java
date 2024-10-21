package com.sideproject.withpt.common.exception.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final String value = p.getValueAsString();
        final Class<?> dtoClass = p.getCurrentValue().getClass();
        final String fieldName = p.currentName();

        try {
            final Class<T> enumClass = getEnumClass(dtoClass, fieldName);
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException | NoSuchFieldException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T>> Class<T> getEnumClass(Class<?> dtoClass, String fieldName) throws NoSuchFieldException {
        Field field = dtoClass.getDeclaredField(fieldName);
        Class<?> fieldType = field.getType();

        if (Enum.class.isAssignableFrom(fieldType)) {
            return (Class<T>) fieldType.asSubclass(Enum.class); // Enum 타입으로 캐스팅
        } else {
            throw new IllegalArgumentException("Field " + fieldName + " is not an enum type");
        }
    }
}
