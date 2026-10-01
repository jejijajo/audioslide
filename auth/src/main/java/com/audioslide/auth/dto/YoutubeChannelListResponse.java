package com.audioslide.auth.dto;


import java.util.List;

public record YoutubeChannelListResponse(List<Item> items) {
    public record Item(String id) {}
}
