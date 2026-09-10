package com.audioslide.upload.mapper;

import com.audioslide.upload.dto.UploadRequestDto;
import com.audioslide.upload.entity.VideoEntity;
import com.audioslide.upload.entity.VideoStatus;
import com.audioslide.upload.event.YoutubeDataCreatedEvent;
import org.springframework.stereotype.Component;

public final class VideoMapper {

    private VideoMapper() {}

    public static VideoEntity uploadRequestDtoToVideoEntity(UploadRequestDto metadata) {
        VideoEntity entity = new VideoEntity();
        entity.setTitle(metadata.title());
        entity.setDescription(metadata.description());
        entity.setPrivacyStatus(metadata.privacyStatus());
        entity.setCategoryId(metadata.categoryId());
        entity.setTags(metadata.tags());
        entity.setPublishAt(metadata.publishAt());
        entity.setMadeForKids(metadata.madeForKids());
        entity.setVideoStatus(VideoStatus.GENERATING);
        return entity;
    }

    public static YoutubeDataCreatedEvent videoEntityToYoutubeDataCreatedEvent(VideoEntity videoEntity) {

        return new YoutubeDataCreatedEvent(
                videoEntity.getId(),
                videoEntity.getUserId(),
                videoEntity.getTitle(),
                videoEntity.getDescription(),
                videoEntity.getPrivacyStatus(),
                videoEntity.getCategoryId(),
                videoEntity.getTags(),
                videoEntity.getCreatedAt(),
                videoEntity.getUpdatedAt(),
                videoEntity.getPublishAt(),
                videoEntity.getMadeForKids(),
                videoEntity.getVideoStatus(),
                videoEntity.getThumbnailS3Key(),
                videoEntity.getVideoS3Key()
        );
    }

}
