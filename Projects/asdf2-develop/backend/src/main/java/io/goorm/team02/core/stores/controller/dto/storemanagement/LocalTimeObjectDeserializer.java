package io.goorm.team02.core.stores.controller.dto.storemanagement;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.LocalTime;

public class LocalTimeObjectDeserializer extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // 문자열 형태 ("09:00")
        if (node.isTextual()) {
            return LocalTime.parse(node.asText());
        }

        // 객체 형태 {"hour": 9, "minute": 0, "second": 0, "nano": 0}
        if (node.isObject()) {
            int hour = node.get("hour").asInt();
            int minute = node.get("minute").asInt();
            int second = node.has("second") ? node.get("second").asInt() : 0;

            return LocalTime.of(hour, minute, second);
        }

        return null;
    }
}