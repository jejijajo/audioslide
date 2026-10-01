package com.audioslide.upload.dto;

import com.audioslide.upload.entity.VideoStatus;

import java.util.UUID;

public record UploadResponseDto(String pictureObjectKey, String audioObjectKey, UUID videoId, VideoStatus videoStatus) {
}
