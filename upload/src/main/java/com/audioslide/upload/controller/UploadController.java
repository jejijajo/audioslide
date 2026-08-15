package com.audioslide.upload.controller;

import com.audioslide.upload.UploadService;
import com.audioslide.upload.dto.UploadResponseDto;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    public final UploadService uploadService;

    @PostMapping
    public UploadResponseDto upload(@RequestPart("picture") MultipartFile picture, @RequestPart("audio") MultipartFile audio)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return uploadService.upload(picture,audio);
    }
}
