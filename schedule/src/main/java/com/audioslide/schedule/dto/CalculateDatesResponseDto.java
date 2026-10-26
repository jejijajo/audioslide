package com.audioslide.schedule.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record CalculateDatesResponseDto(List<ZonedDateTime> dates) {
}
