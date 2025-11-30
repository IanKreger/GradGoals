package com.gradgoals.challenge;

public class ChallengeCategory {
    private final String id;      // e.g. "budgeting"
    private final String name;    // e.g. "Budgeting & Cash Flow"
    private final String blurb;   // short description shown before quiz

    public ChallengeCategory(String id, String name, String blurb) {
        this.id = id;
        this.name = name;
        this.blurb = blurb;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBlurb() {
        return blurb;
    }
}
