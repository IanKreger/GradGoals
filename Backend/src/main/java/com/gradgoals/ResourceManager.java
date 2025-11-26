import java.util.ArrayList;
import java.util.List;

public class ResourceManager {

    // one resource, right now just videos or articles
    static class ResourceItem {
        private final String title;
        private final String type; 
        private final String url;

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

    private final List<ResourceItem> resources = new ArrayList<>();

    // add resource
    public void addResource(String title, String type, String url) {
        if (!type.equalsIgnoreCase("video") && !type.equalsIgnoreCase("article")) {
            throw new IllegalArgumentException("Type must be 'video' or 'article'.");
        }
        resources.add(new ResourceItem(title, type, url));
    }

    // get all of the resources
    public List<ResourceItem> getAllResources() {
        return new ArrayList<>(resources);
    }

    // get all of the videos
    public List<ResourceItem> getVideos() {
        List<ResourceItem> videos = new ArrayList<>();
        for (ResourceItem item : resources) {
            if (item.getType().equalsIgnoreCase("video")) {
                videos.add(item);
            }
        }
        return videos;
    }

    // get all of the articles
    public List<ResourceItem> getArticles() {
        List<ResourceItem> articles = new ArrayList<>();
        for (ResourceItem item : resources) {
            if (item.getType().equalsIgnoreCase("article")) {
                articles.add(item);
            }
        }
        return articles;
    }

    // Resources gathered so far
    public void loadDefaultResources() {

        addResource("What is budgeting:", "video", "https://www.youtube.com/watch?v=CbhjhWleKGE");
        addResource("Budgeting Basics", "video", "https://www.youtube.com/watch?v=sVKQn2I4HDM");
        addResource("Budgeting for Beginners", "video", "https://www.youtube.com/watch?v=xfPbT7HPkKA");
        addResource("How to make a budget and stick to it", "video", "https://www.youtube.com/watch?v=4Eh8QLcB1UQ");
        addResource("How to manage money like the 1%", "video", "https://www.youtube.com/watch?v=NEzqHbtGa9U");
        addResource("You need a written budget", "video", "https://www.youtube.com/watch?v=8F0mH84w6e4");
    }
    public static void main(String[] args) {
        ResourceManager manager = new ResourceManager();
        manager.loadDefaultResources();

        System.out.println("=== ALL RESOURCES ===");
        for (ResourceItem item : manager.getAllResources()) {
            System.out.println(item);
        }
    }
}
