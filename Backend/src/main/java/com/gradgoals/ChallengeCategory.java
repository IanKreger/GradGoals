// This class represents one challenge category
// Each category has an id, a display name, and a short blurb the user sees before starting the quiz
// Basically the metadata that tells the frontend how to label and describe each section of challenges

package com.gradgoals;

public class ChallengeCategory {
    
    // unique ID for the category — used internally (e.g. "budgeting")
    private final String id;

    // the user-facing name that shows up on the challenge page
    private final String name;

    // the short description that appears before the quiz starts
    private final String blurb;

    // constructor — sets all the fields when we create a new category
    public ChallengeCategory(String id, String name, String blurb) {
        this.id = id;
        this.name = name;
        this.blurb = blurb;
    }

    // getters — used by the frontend to display each category properly
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
