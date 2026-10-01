package com.audioslide.video.render;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface VideoRender {
    InputStream render(Path tempVideo, Path tempPicture, Path tempAudio);
}
