package cr.ac.una.turnos.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = crearObjectMapper();

    private JsonUtil() {
    }

    private static ObjectMapper crearObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }

    public static String convertirAJson(Object objeto) {
        try {
            return OBJECT_MAPPER.writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "No se pudo convertir el objeto a JSON.", e
            );
        }
    }

    public static <T> T convertirDesdeJson(String json, Class<T> clase) {
        try {
            return OBJECT_MAPPER.readValue(json, clase);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "No se pudo convertir el JSON al objeto.", e
            );
        }
    }
}