package com.audioslide.schedule;

import com.audioslide.schedule.dto.CalculateDatesResponseDto;
import com.audioslide.schedule.dto.SetScheduleRequestDto;
import com.audioslide.schedule.dto.SetScheduleResponseDto;
import com.audioslide.schedule.exception.InvalidRequestException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/health")
    public String health() {return "Schedule Service is running...";}

    @PostMapping("/set")
    public SetScheduleResponseDto setSchedule(@Valid @RequestBody SetScheduleRequestDto dto, @AuthenticationPrincipal String userId) {
        return scheduleService.setSchedule(dto,Long.parseLong(userId));
    }

    @GetMapping("/calculate/{count}")
    public CalculateDatesResponseDto calculateDates(@PathVariable Long count, @AuthenticationPrincipal String userId) {
        if(count <= 0) {throw new InvalidRequestException("count must be > 0");}
        return scheduleService.calculateDateTime(Long.parseLong(userId), count);
    }

    @GetMapping
    public SetScheduleResponseDto getSchedule(@AuthenticationPrincipal String userId) {
        return scheduleService.getSchedule(Long.parseLong(userId));
    }

}
