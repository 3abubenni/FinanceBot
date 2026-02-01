package org.dubna.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.Objects;

public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        return MAPPER.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || Objects.equals(json, "null")) {
            return null;
        }
        return MAPPER.readValue(json, clazz);
    }

}
