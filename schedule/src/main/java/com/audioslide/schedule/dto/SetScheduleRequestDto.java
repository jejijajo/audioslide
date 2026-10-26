package com.audioslide.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record SetScheduleRequestDto(
        @NotEmpty Set<DayOfWeek> days,
        @NotNull LocalTime time,
        @NotBlank String zoneId) {}
