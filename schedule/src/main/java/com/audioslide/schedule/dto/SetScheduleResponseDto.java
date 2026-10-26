package com.audioslide.schedule.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record SetScheduleResponseDto(Set<DayOfWeek> days, LocalTime time, String zoneId) {
}
