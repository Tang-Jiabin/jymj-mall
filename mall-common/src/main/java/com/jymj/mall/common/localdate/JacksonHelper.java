package com.jymj.mall.common.localdate;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-14
 */
@Slf4j
public class JacksonHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String DEFAULT_DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    private static final String DEFAULT_DATE_FORMATTER = "yyyy-MM-dd";

    private static final String DEFAULT_TIME_FORMATTER = "HH:mm:ss";

    static {

        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        OBJECT_MAPPER.configure(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature(), true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        OBJECT_MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        OBJECT_MAPPER.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jg,
                                  SerializerProvider sp) throws IOException {
                jg.writeString("");
            }
        });
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {
                String value = jsonParser.getValueAsString();
                if (Strings.isNullOrEmpty(value)) {
                    return null;
                }
                return LocalDateTime.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER));
            }
        });
        javaTimeModule.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                if (Objects.isNull(localDateTime)) {
                    jsonGenerator.writeString("");
                }
                jsonGenerator.writeString(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER).format(localDateTime));
            }
        });
        javaTimeModule.addDeserializer(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {
                if (Strings.isNullOrEmpty(jsonParser.getValueAsString())) {
                    return null;
                }
                return LocalDate.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMATTER));
            }
        });
        javaTimeModule.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                if (Objects.isNull(localDate)) {
                    jsonGenerator.writeString("");
                } else {
                    jsonGenerator.writeString(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMATTER).format(localDate));
                }
            }
        });
        javaTimeModule.addDeserializer(LocalTime.class, new JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {
                if (Strings.isNullOrEmpty(jsonParser.getValueAsString())) {
                    return null;
                }
                return LocalTime.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMATTER));
            }
        });
        javaTimeModule.addSerializer(LocalTime.class, new JsonSerializer<LocalTime>() {
            @Override
            public void serialize(LocalTime localTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                if (Objects.isNull(localTime)) {
                    jsonGenerator.writeString("");
                }
                jsonGenerator.writeString(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMATTER).format(localTime));
            }
        });
        OBJECT_MAPPER.registerModules(javaTimeModule);
    }

    public static String obj2Json(Object obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    public static <T> T json2Obj(String json, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    /**
     * 可以把Json字符串转换成List
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonParseException e) {
            log.error("parseObject(String, TypeReference<T>) JsonParseException", e);
        } catch (JsonMappingException e) {
            log.error("parseObject(String, TypeReference<T>) JsonMappingException", e);
        } catch (IOException e) {
            log.error("parseObject(String, TypeReference<T>)", e);
        }
        return null;
    }


    public static JsonNode readRootNode(String json) {
        return readAnyNode(json, null);
    }

    /**
     * 根据json key 获取任意层级的jsonNode
     */
    public static JsonNode readAnyNode(String json, String fieldName) {
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(json);
            if (Strings.isNullOrEmpty(fieldName)) {
                return rootNode;
            }
            return rootNode.findValue(fieldName);
        } catch (IOException e) {
            log.error("readNode(String json,String fieldName)", e);
        }
        return null;
    }



    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    /**
     * 格式化输出json
     */
    public static <T> String console(T t) {
        String json = "";
        try {
            json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(t);
        } catch (JsonGenerationException e) {
            log.error("console(T t) JsonGenerationException", e);
        } catch (JsonMappingException e) {
            log.error("console(T t) JsonMappingException", e);
        } catch (IOException e) {
            log.error("console(T t)", e);
        }
        return json;
    }
}






