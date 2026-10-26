package com.audioslide.template;

import java.util.Map;

public final class YoutubeCategories {

    public static final Map<Integer, String> CATEGORIES = Map.ofEntries(
            Map.entry(1, "Film & Animation"),
            Map.entry(2, "Cars & Vehicles"),
            Map.entry(10, "Music"),
            Map.entry(15, "Pets & Animals"),
            Map.entry(17, "Sports"),
            Map.entry(19, "Travel & Events"),
            Map.entry(20, "Gaming"),
            Map.entry(22, "People & Blogs"),
            Map.entry(23, "Comedy"),
            Map.entry(24, "Entertainment"),
            Map.entry(25, "News & Politics"),
            Map.entry(26, "Howto & Style"),
            Map.entry(27, "Education"),
            Map.entry(28, "Science & Technology"),
            Map.entry(29, "Nonprofits & Activism")
    );

    private YoutubeCategories() {}

    public static boolean isValid(Integer id) {
        return id != null && CATEGORIES.containsKey(id);
    }
}
