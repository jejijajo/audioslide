package com.audioslide.schedule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "schedules")
@NoArgsConstructor
@Setter
@Getter
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = true, name = "last_upload_date_time")
    private Instant lastUploadDateTime;

    @Column(nullable = false, name = "scheduled_days")
    private Set<DayOfWeek> scheduledDays;

    @Column(nullable = false, name = "scheduled_time")
    private LocalTime scheduledTime;

    @Column(nullable = false, name = "zone_id")
    private ZoneId zoneId;

    public ScheduleEntity(Long userId, Set<DayOfWeek> scheduledDays, LocalTime scheduledTime, ZoneId zoneId) {
        this.userId = userId;
        this.scheduledDays = scheduledDays;
        this.scheduledTime = scheduledTime;
        this.zoneId = zoneId;
    }
}
