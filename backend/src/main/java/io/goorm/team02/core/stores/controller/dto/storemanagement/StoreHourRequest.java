package io.goorm.team02.core.stores.controller.dto.storemanagement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreHourRequest {

    @Schema(description = "요일", example = "1", allowableValues = {"0", "1", "2", "3", "4", "5", "6", "7"})
    private Integer dayOfWeek; // 0=일요일, 1=월요일, ..., 6=토요일, 7=전체

    @JsonDeserialize(using = LocalTimeObjectDeserializer.class)
    @Schema(description = "오픈 시간", example = "09:00")
    private LocalTime openTime;

    @JsonDeserialize(using = LocalTimeObjectDeserializer.class)
    @Schema(description = "마감 시간", example = "22:00")
    private LocalTime closeTime;

    @Schema(description = "휴무 여부", example = "false", defaultValue = "false")
    private Boolean isClosed = false; // ✅ 기본값 설정
}