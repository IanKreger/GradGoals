package com.gradgoals;

public class Rating {
    private String resourceId;     // which resource is being rated
    private String userId;         // who rated it
    private int stars;             // 1–5 rating

    public Rating() {}

    public Rating(String resourceId, String userId, int stars) {
        this.resourceId = resourceId;
        this.userId = userId;
        this.stars = stars;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
