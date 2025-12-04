package com.gradgoals;

import java.util.ArrayList;
import java.util.List;

public class ResourceManager {
    // ---- ResourceItem MODEL (not static) ----
    public class ResourceItem {
        private String title;
        private String type;
        private String url;

        public ResourceItem(String title, String type, String url) {
            this.title = title;
            this.type = type;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "[" + type.toUpperCase() + "] " + title + " — " + url;
        }
    }

    // ---- Instance list of resources ----
    private final List<ResourceItem> resources = new ArrayList<>();

    // ---- Add resource ----
    public void addResource(String title, String type, String url) {
        if (!type.equalsIgnoreCase("video") && !type.equalsIgnoreCase("article") && !type.equalsIgnoreCase("textbook")) {
            throw new IllegalArgumentException("Type must be 'video', 'article' or 'textbook'.");
        }
        resources.add(new ResourceItem(title, type, url));
    }

    // ---- Get all resources ----
    public List<ResourceItem> getAllResources() {
        return new ArrayList<>(resources);
    }

    // ---- Videos ----
    public List<ResourceItem> getVideos() {
        List<ResourceItem> videos = new ArrayList<>();
        for (ResourceItem item : resources) {
            if (item.getType().equalsIgnoreCase("video")) {
                videos.add(item);
            }
        }
        return videos;
    }

    // ---- Articles ----
    public List<ResourceItem> getArticles() {
        List<ResourceItem> articles = new ArrayList<>();
        for (ResourceItem item : resources) {
            if (item.getType().equalsIgnoreCase("article")) {
                articles.add(item);
            }
        }
        return articles;
    }

    public List<ResourceItem> getTextbooks() {
        List<ResourceItem> textbooks = new ArrayList<>();
        for (ResourceItem item : resources) {
            if (item.getType().equalsIgnoreCase("textbook")) {
                textbooks.add(item);
            }
        }
        return textbooks;
    }
    
    public void loadDefaultResources() {
        addResource("What is budgeting:", "video", "https://www.youtube.com/watch?v=CbhjhWleKGE");
        addResource("Budgeting Basics", "video", "https://www.youtube.com/watch?v=sVKQn2I4HDM");
        addResource("Budgeting for Beginners", "video", "https://www.youtube.com/watch?v=xfPbT7HPkKA");
        addResource("How to make a budget and stick to it", "video", "https://www.youtube.com/watch?v=4Eh8QLcB1UQ");
        addResource("How to manage money like the 1%", "video", "https://www.youtube.com/watch?v=NEzqHbtGa9U");
        addResource("You need a written budget", "video", "https://www.youtube.com/watch?v=8F0mH84w6e4");
        addResource("Budgeting", "textbook", "https://research.ebsco.com/c/evkh36/ebook-viewer/pdf/qthbl2jd2b/page/pp_11?location=https%3A%2F%2Fresearch.ebsco.com%2Fc%2Fevkh36%2Fsearch%2Fdetails%2Fqthbl2jd2b%3Fdb%3De000xna");
    }
}