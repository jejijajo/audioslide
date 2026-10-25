package com.audioslide.schedule;

import com.audioslide.schedule.dto.CalculateDatesResponseDto;
import com.audioslide.schedule.dto.SetScheduleRequestDto;
import com.audioslide.schedule.dto.SetScheduleResponseDto;
import com.audioslide.schedule.event.VideoPublishScheduledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public SetScheduleResponseDto setSchedule(SetScheduleRequestDto setScheduleRequestDto, Long userId) {
        ScheduleEntity entity = scheduleRepository.findByUserId(userId)
                .map(e -> {
                    e.setScheduledDays(setScheduleRequestDto.days());
                    e.setScheduledTime(setScheduleRequestDto.time());
                    ZoneId.of(setScheduleRequestDto.zoneId());
                    log.info("set new schedule");
                    return e;
                })
                .orElseGet(() -> {
                            log.info("create new schedule");
                            return new ScheduleEntity(
                                    userId,
                                    setScheduleRequestDto.days(),
                                    setScheduleRequestDto.time(),
                                    ZoneId.of(setScheduleRequestDto.zoneId())
                            );
                        }
                );

        entity = scheduleRepository.save(entity);
        return new SetScheduleResponseDto(
                entity.getScheduledDays(),
                entity.getScheduledTime(),
                entity.getZoneId().toString()
        );
    }

    public CalculateDatesResponseDto calculateDateTime(Long userId, Long count) {

        ScheduleEntity entity = scheduleRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        ZoneId zone = entity.getZoneId();
        LocalTime time = entity.getScheduledTime();
        Set<DayOfWeek> days = entity.getScheduledDays();

        Instant now = Instant.now();
        Instant base;

        if(entity.getLastUploadDateTime() != null && entity.getLastUploadDateTime().isBefore(now)) {
            base = now;
        } else {
            base = entity.getLastUploadDateTime();
        }

        ZonedDateTime from = base.atZone(zone);
        LocalDate date = from.toLocalDate();

        List<ZonedDateTime> result = new ArrayList<>();

        while (result.size() < count) {
            if (days.contains(date.getDayOfWeek())) {
                ZonedDateTime candidate = ZonedDateTime.of(date, time, zone);
                if (candidate.isAfter(from)) {
                    result.add(candidate);
                }
            }
            date = date.plusDays(1);
        }
        log.info("dates calculated");
        return new CalculateDatesResponseDto(result);
    }

    public void setLastUploadDateTime(VideoPublishScheduledEvent event) {
        ScheduleEntity entity = scheduleRepository.findByUserId(event.userId())
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        Instant last = entity.getLastUploadDateTime();
        if (last == null || event.date().isAfter(last)) {
            entity.setLastUploadDateTime(event.date());
            scheduleRepository.save(entity);
            log.info("set new last upload date");
        }

    }

    public SetScheduleResponseDto getSchedule(Long userId) {
        ScheduleEntity entity = scheduleRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        return new SetScheduleResponseDto(
                entity.getScheduledDays(),
                entity.getScheduledTime(),
                entity.getZoneId().toString()
        );
    }
}
